package supercoder79.wavedefense.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.map.WdMap;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.player.MutablePlayerSet;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.PlayerRef;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

import java.util.*;

public final class WdActive {
	public final GameSpace space;
	public final WdMap map;
	public final WdConfig config;
	private final MutablePlayerSet participants;
	private final WdSpawnLogic spawnLogic;
	public final WdWaveManager waveManager;
	private final Object2IntMap<UUID> playerKillAmounts = new Object2IntOpenHashMap<>();
	public final Object2IntMap<PlayerRef> sharpnessLevels = new Object2IntOpenHashMap<>();
	public final Object2IntMap<PlayerRef> protectionLevels = new Object2IntOpenHashMap<>();
	public final Object2IntMap<PlayerRef> powerLevels = new Object2IntOpenHashMap<>();
	private final Set<BlockPos> openedChests = new HashSet<>();
	public final WdBar bar;

	public final WdGuide guide;

	private long gameCloseTick = Long.MAX_VALUE;

	public final int groupSize;

	private WdActive(GameSpace space, WdMap map, WdConfig config, MutablePlayerSet participants, GlobalWidgets widgets) {
		this.space = space;
		this.map = map;
		this.config = config;
		this.participants = participants;

		this.spawnLogic = new WdSpawnLogic(this.space, config);
		this.waveManager = new WdWaveManager(this);
		this.bar = WdBar.create(widgets);

		this.guide = new WdGuide(this);

		this.groupSize = participants.size();
	}

	public static void open(GameSpace world, WdMap map, WdConfig config) {
		world.openGame(game -> {
			GlobalWidgets widgets = new GlobalWidgets(game);
			WdActive active = new WdActive(world, map, config, (MutablePlayerSet) world.getPlayers().copy(), widgets);

			game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.DENY);
			game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
			game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
			game.setRule(GameRule.HUNGER, RuleResult.ALLOW);
			game.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);
			game.setRule(GameRule.INTERACTION, RuleResult.ALLOW);

			game.on(GameOpenListener.EVENT, active::open);
			game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
			game.on(PlayerAddListener.EVENT, active::addPlayer);
			game.on(PlayerRemoveListener.EVENT, active::removePlayer);

			game.on(GameTickListener.EVENT, active::tick);
			game.on(UseItemListener.EVENT, active::onUseItem);

			game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
			game.on(EntityDeathListener.EVENT, active::onEntityDeath);
			game.on(UseBlockListener.EVENT, active::onUseBlock);
		});
	}

	private void open() {
		for (ServerPlayerEntity player : this.participants) {
			this.spawnParticipant(player);
		}
	}

	private void addPlayer(ServerPlayerEntity player) {
		this.spawnSpectator(player);
	}

	private void removePlayer(ServerPlayerEntity player) {
		participants.remove(player);
	}

	private void tick() {
		ServerWorld world = this.space.getWorld();
		long time = world.getTime();

		if (time > gameCloseTick) {
			this.space.close();
			return;
		}

		this.guide.tick(time, waveManager.isActive());
		this.waveManager.tick(time, guide.getProgressBlocks());

		this.damageFarPlayers(guide.getCenterPos());

		this.bar.tick(waveManager.getActiveWave());

		// This is a horrifically cursed workaround for UseBlockListener not working. I'm sorry.
		if (time % 20 == 0) {
			for (ServerPlayerEntity player : this.participants) {
				BlockPos.Mutable mutable = player.getBlockPos().mutableCopy();

				for(int x = -1; x <= 1; x++) {
					for(int z = -1; z <= 1; z++) {
						for(int y = 0; y <= 2; y++) {

							BlockPos local = mutable.add(x, y, z);
							if (this.space.getWorld().getBlockState(local).isOf(Blocks.CHEST)) {
								if (!this.openedChests.contains(local)) {
									this.participants.forEach((participant) -> {
										participant.sendMessage(new LiteralText(player.getEntityName() + " has found a loot chest!"), false);
										participant.sendMessage(new LiteralText("You recieved 12 iron."), false);
										participant.inventory.insertStack(new ItemStack(Items.IRON_INGOT, 12));
									});

									// Change glowstone to obsidian
									world.setBlockState(local.down(), Blocks.OBSIDIAN.getDefaultState());

									this.openedChests.add(local);
								}
							}
						}
					}
				}
			}
		}

	}

	private TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() == Items.COMPASS) {
			player.openHandledScreen(WdItemShop.create(player, this));
			return TypedActionResult.success(stack);
		}

		return TypedActionResult.pass(stack);
	}

	private ActionResult onEntityDeath(LivingEntity entity, DamageSource source) {
		if (entity instanceof WaveEntity) {
			WdWave activeWave = waveManager.getActiveWave();
			if (activeWave != null) {
				activeWave.onZombieKilled();

				if (source.getAttacker() instanceof ServerPlayerEntity) {
					ServerPlayerEntity player = (ServerPlayerEntity) source.getAttacker();

					player.inventory.insertStack(new ItemStack(Items.IRON_INGOT, ((WaveEntity) entity).ironCount()));
				}
			}

			return ActionResult.FAIL;
		}

		return ActionResult.SUCCESS;
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		this.eliminatePlayer(player);

		if (participants.isEmpty()) {
			// Display win results
			PlayerSet players = space.getPlayers();
			players.sendMessage(new LiteralText("All players died....").formatted(Formatting.DARK_RED));
			players.sendMessage(new LiteralText("You made it to wave " + waveManager.getWaveOrdinal() + ".").formatted(Formatting.DARK_RED));

			// Close game in 10 secs
			this.gameCloseTick = this.space.getWorld().getTime() + (10 * 20);
		}

		return ActionResult.FAIL;
	}

	// TODO: this doesn't work. The logic has been moved to tick() as a hacky workaround.
	private ActionResult onUseBlock(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
		if (this.space.getWorld().getBlockState(hitResult.getBlockPos()).isOf(Blocks.CHEST)) {
			if (!this.openedChests.contains(hitResult.getBlockPos())) {
				for (ServerPlayerEntity participant : this.participants) {
					participant.sendMessage(new LiteralText(player.getDisplayName() + " has found a loot chest!"), false);
					participant.sendMessage(new LiteralText("You recieved 12 iron."), false);
					participant.inventory.insertStack(new ItemStack(Items.IRON_INGOT, 12));
				}

				this.openedChests.add(hitResult.getBlockPos());
			}

			return ActionResult.FAIL;
		}

		return ActionResult.PASS;
	}

	private void spawnParticipant(ServerPlayerEntity player) {
		this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
		this.spawnLogic.spawnPlayer(player);
		this.guide.onAddPlayer(player);

		player.inventory.insertStack(0,
				ItemStackBuilder.of(Items.IRON_SWORD)
						.setUnbreakable()
						.build()
		);

		player.inventory.insertStack(1,
				ItemStackBuilder.of(Items.COOKED_BEEF)
						.setCount(8)
						.build()
		);

		player.inventory.insertStack(8,
				ItemStackBuilder.of(Items.COMPASS)
						.setName(new LiteralText("Item Shop"))
						.build()
		);

		player.inventory.armor.set(3, ItemStackBuilder.of(Items.CHAINMAIL_HELMET).setUnbreakable().build());
		player.inventory.armor.set(2, ItemStackBuilder.of(Items.CHAINMAIL_CHESTPLATE).setUnbreakable().build());
		player.inventory.armor.set(1, ItemStackBuilder.of(Items.CHAINMAIL_LEGGINGS).setUnbreakable().build());
		player.inventory.armor.set(0, ItemStackBuilder.of(Items.CHAINMAIL_BOOTS).setUnbreakable().build());
	}

	private void eliminatePlayer(ServerPlayerEntity player) {
		if (!participants.remove(player)) {
			return;
		}

		Text message = player.getDisplayName().shallowCopy().append(" succumbed to the zombies....")
				.formatted(Formatting.RED);

		PlayerSet players = this.space.getPlayers();
		players.sendMessage(message);
		players.sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);

		this.spawnSpectator(player);
	}

	private void spawnSpectator(ServerPlayerEntity player) {
		this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
		this.spawnLogic.spawnPlayer(player);
	}

	public int getEnchantmentLevel(Object2IntMap<PlayerRef> map, ServerPlayerEntity player) {
		return map.getOrDefault(PlayerRef.of(player), 0);
	}

	public void increaseEnchantment(Object2IntMap<PlayerRef> map, ServerPlayerEntity player) {
		PlayerRef ref = PlayerRef.of(player);
		int level = map.getOrDefault(ref, 0);
		map.put(ref, level + 1);
	}

	private void damageFarPlayers(Vec3d centerPos) {
		int maxDistance = this.config.spawnRadius + 5;
		double maxDistance2 = maxDistance * maxDistance;

		List<ServerPlayerEntity> farPlayers = new ArrayList<>();

		for (ServerPlayerEntity player : participants) {
			double deltaX = player.getX() - centerPos.getX();
			double deltaZ = player.getZ() - centerPos.getZ();

			if (deltaX * deltaX + deltaZ * deltaZ > maxDistance2) {
				if (!player.isCreative() && !player.isSpectator()) {
					farPlayers.add(player);
				}
			}
		}

		for (ServerPlayerEntity player : farPlayers) {
			LiteralText message = new LiteralText("You are too far away from your villager!");
			player.sendMessage(message.formatted(Formatting.RED), true);

			player.damage(DamageSource.OUT_OF_WORLD, 0.5F);
		}
	}

	public PlayerSet getParticipants() {
		return participants;
	}
}
