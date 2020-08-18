package supercoder79.wavedefense.game;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import supercoder79.wavedefense.entity.SillyZombieEntity;
import supercoder79.wavedefense.map.WaveDefenseMap;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.*;

public final class WaveDefenseActive {
	private final GameWorld world;
	private final WaveDefenseMap map;
	public final WaveDefenseConfig config;
	private final Set<ServerPlayerEntity> participants;
	private final WaveDefenseSpawnLogic spawnLogic;
	private final Map<UUID, Integer> playerKillAmounts = new HashMap<>();
	private final Map<PlayerRef, Integer> sharpnessLevels = new HashMap<>();
	private final Map<PlayerRef, Integer> protectionLevels = new HashMap<>();
	private final Map<PlayerRef, Integer> powerLevels = new HashMap<>();
	private final WaveDefenseBar bar;
	private final Random random = new Random();

	private boolean shouldSpawn = false;
	private int zombiesToSpawn = 0;
	private int killedZombies = 0;
	private int currentWave = 1;
	private long nextWaveTick = -1;
	private long gameCloseTick = Long.MAX_VALUE;

	public final WaveDefenseProgress progress;

	private WaveDefenseActive(GameWorld world, WaveDefenseMap map, WaveDefenseConfig config, Set<ServerPlayerEntity> participants) {
		this.world = world;
		this.map = map;
		this.config = config;
		this.participants = participants;

		this.spawnLogic = new WaveDefenseSpawnLogic(world, config);
		this.progress = new WaveDefenseProgress(config, map);
		this.bar = new WaveDefenseBar();
	}

	public static void open(GameWorld world, WaveDefenseMap map, WaveDefenseConfig config) {
		WaveDefenseActive active = new WaveDefenseActive(world, map, config, new HashSet<>(world.getPlayers()));

		world.openGame(game -> {
			game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.DENY);
			game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
			game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
			game.setRule(GameRule.HUNGER, RuleResult.DENY);
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
		this.bar.addPlayer(player);

		if (this.participants.add(player)) {
			this.spawnSpectator(player);
		}
	}

	private void removePlayer(ServerPlayerEntity player) {
		this.participants.remove(player);
		this.bar.removePlayer(player);
	}

	private void tick() {
		ServerWorld world = this.world.getWorld();
		long time = world.getTime();

		if (time > gameCloseTick) {
			this.world.close();
			return;
		}

		if (time > nextWaveTick) {
			shouldSpawn = true;
			killedZombies = 0;
			nextWaveTick = Long.MAX_VALUE;
			zombiesToSpawn = zombieCount(currentWave);
			broadcastMessage(new LiteralText("Starting wave " + currentWave + " with " + zombiesToSpawn + " zombies!"));
		}

		this.progress.tick(world, time);

		if (time % 4 == 0) {
			this.bar.tick(currentWave, zombiesToSpawn, killedZombies);
		}

		if (shouldSpawn) {
			shouldSpawn = false;

			for (int i = 0; i < zombiesToSpawn; i++) {
				ZombieEntity zombie = new SillyZombieEntity(world, this);
				zombie.setPersistent();

				BlockPos pos = WaveDefenseSpawnLogic.topPos(progress.getCenterPos(), this.world, this.config);
				zombie.refreshPositionAndAngles(pos, 0, 0);
				setupZombie(zombie);

				world.spawnEntity(zombie);
			}
		}
	}

	private void setupZombie(ZombieEntity zombie) {
		double t2Chance = MathHelper.clamp((0.1 * currentWave) - 1, 0, 1);
		if (random.nextDouble() < t2Chance) {
			zombie.setCustomName(new LiteralText("T2 Zombie"));
			zombie.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
			zombie.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
			zombie.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
		} else {
			zombie.setCustomName(new LiteralText("T1 Zombie"));
		}
	}

	private TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() == Items.COMPASS) {
			player.openHandledScreen(WaveDefenseItemShop.create(player, this));
			return TypedActionResult.success(stack);
		}

		return TypedActionResult.pass(stack);
	}

	private int zombieCount(int wave) {
		return (int) ((0.24 * wave * wave) + (0.95 * wave) + 8);
	}

	private ActionResult onEntityDeath(LivingEntity entity, DamageSource source) {
		if (entity.getType() == EntityType.ZOMBIE) {
			killedZombies++;

			if (source.getAttacker() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) source.getAttacker();
				int ironCount = 1;

				if (entity.getCustomName() != null && entity.getCustomName().asString().equals("T2 Zombie")) {
					ironCount = 2;
				}

				player.inventory.insertStack(new ItemStack(Items.IRON_INGOT, ironCount));
			}

			if (killedZombies == zombiesToSpawn) {
				broadcastMessage(new LiteralText("Wave ended! Next wave starting in 15 seconds."));
				currentWave++;

				nextWaveTick = world.getWorld().getTime() + (15 * 20);
			}

			return ActionResult.FAIL;
		}

		return ActionResult.SUCCESS;
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		this.eliminatePlayer(player);

		boolean playersDead = true;
		for (ServerPlayerEntity playerEntity : this.world.getPlayers()) {
			if (!playerEntity.isSpectator()) {
				playersDead = false;
			}
		}

		if (playersDead) {
			// Display win results
			broadcastMessage(new LiteralText("All players died....").formatted(Formatting.DARK_RED));
			broadcastMessage(new LiteralText("You made it to wave " + currentWave + ".").formatted(Formatting.DARK_RED));

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
		Text message = player.getDisplayName().shallowCopy().append(" succumbed to the zombies....")
				.formatted(Formatting.RED);

		this.broadcastMessage(message);
		this.broadcastSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);

		this.spawnSpectator(player);
	}

	private void spawnSpectator(ServerPlayerEntity player) {
		this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
		this.spawnLogic.spawnPlayer(player);
	}

	private void broadcastMessage(Text message) {
		for (ServerPlayerEntity player : this.world.getPlayers()) {
			player.sendMessage(message, false);
		}
	}

	private void broadcastSound(SoundEvent sound) {
		for (ServerPlayerEntity player : this.world.getPlayers()) {
			player.playSound(sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}

	public int getSharpnessLevel(ServerPlayerEntity player) {
		return this.sharpnessLevels.computeIfAbsent(PlayerRef.of(player), ref -> 0);
	}

	public void increaseSharpness(ServerPlayerEntity player) {
		PlayerRef ref = PlayerRef.of(player);
		int level = this.sharpnessLevels.get(ref);
		this.sharpnessLevels.put(ref, level + 1);
	}

	public int getProtectionLevel(ServerPlayerEntity player) {
		return this.protectionLevels.computeIfAbsent(PlayerRef.of(player), ref -> 0);
	}

	public void increaseProtection(ServerPlayerEntity player) {
		PlayerRef ref = PlayerRef.of(player);
		int level = this.protectionLevels.get(ref);
		this.protectionLevels.put(ref, level + 1);
	}

	public int getPowerLevel(ServerPlayerEntity player) {
		return this.powerLevels.computeIfAbsent(PlayerRef.of(player), ref -> 0);
	}

	public void increasePower(ServerPlayerEntity player) {
		PlayerRef ref = PlayerRef.of(player);
		int level = this.powerLevels.get(ref);
		this.powerLevels.put(ref, level + 1);
	}
}
