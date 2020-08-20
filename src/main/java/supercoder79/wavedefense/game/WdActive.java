package supercoder79.wavedefense.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.map.WdMap;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class WdActive {
	public final GameWorld world;
	public final WdMap map;
	public final WdConfig config;
	private final Set<ServerPlayerEntity> participants;
	private final WdSpawnLogic spawnLogic;
	public final WdWaveManager waveManager;
	private final Object2IntMap<UUID> playerKillAmounts = new Object2IntOpenHashMap<>();
	public final Object2IntMap<PlayerRef> sharpnessLevels = new Object2IntOpenHashMap<>();
	public final Object2IntMap<PlayerRef> protectionLevels = new Object2IntOpenHashMap<>();
	public final Object2IntMap<PlayerRef> powerLevels = new Object2IntOpenHashMap<>();
	public final WdBar bar;

	public final WdGuide guide;

	private long gameCloseTick = Long.MAX_VALUE;

	private WdActive(GameWorld world, WdMap map, WdConfig config, Set<ServerPlayerEntity> participants) {
		this.world = world;
		this.map = map;
		this.config = config;
		this.participants = participants;

		this.spawnLogic = new WdSpawnLogic(world, config);
		this.waveManager = new WdWaveManager(this);
		this.bar = world.addResource(new WdBar(world));

		this.guide = new WdGuide(this);
	}

	public static void open(GameWorld world, WdMap map, WdConfig config) {
		WdActive active = new WdActive(world, map, config, new HashSet<>(world.getPlayers()));

		world.openGame(game -> {
			game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.DENY);
			game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
			game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
			game.setRule(GameRule.HUNGER, RuleResult.ALLOW);
			game.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);

			game.on(GameOpenListener.EVENT, active::open);
			game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
			game.on(PlayerAddListener.EVENT, active::addPlayer);
			game.on(PlayerRemoveListener.EVENT, active::removePlayer);

			game.on(GameTickListener.EVENT, active::tick);
			game.on(UseItemListener.EVENT, active::onUseItem);

			game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
			game.on(EntityDeathListener.EVENT, active::onEntityDeath);
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
		this.participants.remove(player);
	}

	private void tick() {
		ServerWorld world = this.world.getWorld();
		long time = world.getTime();

		if (time > gameCloseTick) {
			this.world.close();
			return;
		}

		this.guide.tick(time, waveManager.isActive());
		this.waveManager.tick(time, guide.getProgressBlocks());

		this.damageFarPlayers(guide.getCenterPos());

		this.bar.tick(waveManager.getActiveWave());
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

					int tier = ((WaveEntity) entity).getTier();
					player.inventory.insertStack(new ItemStack(Items.IRON_INGOT, tier + 1));
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
			PlayerSet players = world.getPlayerSet();
			players.sendMessage(new LiteralText("All players died....").formatted(Formatting.DARK_RED));
			players.sendMessage(new LiteralText("You made it to wave " + waveManager.getWaveOrdinal() + ".").formatted(Formatting.DARK_RED));

			// Close game in 10 secs
			gameCloseTick = this.world.getWorld().getTime() + (10 * 20);
		}

		return ActionResult.FAIL;
	}

	private void spawnParticipant(ServerPlayerEntity player) {
		this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
		this.spawnLogic.spawnPlayer(player);

		player.inventory.insertStack(0,
				ItemStackBuilder.of(Items.IRON_SWORD)
						.setUnbreakable()
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

		PlayerSet players = this.world.getPlayerSet();
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

		for (ServerPlayerEntity player : participants) {
			double deltaX = player.getX() - centerPos.getX();
			double deltaZ = player.getZ() - centerPos.getZ();

			if (deltaX * deltaX + deltaZ * deltaZ > maxDistance2) {
				// Don't touch creative or spectator players
				if (player.isCreative() || player.isSpectator()) {
					continue;
				}

				LiteralText message = new LiteralText("You are too far away from your villager!");
				player.sendMessage(message.formatted(Formatting.RED), true);

				player.damage(DamageSource.OUT_OF_WORLD, 0.5F);
			}
		}
	}
}
