package supercoder79.wavedefense.game;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import supercoder79.wavedefense.map.WaveDefenseMap;
import supercoder79.wavedefense.map.WaveDefenseProgress;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.event.EntityDeathListener;
import xyz.nucleoid.plasmid.game.event.GameCloseListener;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class WaveDefenseActive {
	private final GameWorld world;
	private final WaveDefenseMap map;
	private final WaveDefenseConfig config;
	private final Set<ServerPlayerEntity> participants;
	private final WaveDefenseSpawnLogic spawnLogic;
	private final Map<UUID, Integer> playerKillAmounts = new HashMap<>();
	private final WaveDefenseBar bar;
	private Difficulty oldDifficulty;

	private boolean shouldSpawn = false;
	private int zombiesToSpawn = 0;
	private int killedZombies = 0;
	private int currentWave = 1;
	private long nextWaveTick = -1;
	private long gameCloseTick = Long.MAX_VALUE;

	private final WaveDefenseProgress progress;

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
		active.oldDifficulty = world.getWorld().getDifficulty();

		world.openGame(game -> {
			game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.DENY);
			game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
			game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
			game.setRule(GameRule.HUNGER, RuleResult.DENY);

			ServerWorld serverWorld = world.getWorld();
			serverWorld.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, world.getWorld().getServer());

			serverWorld.getServer().setDifficulty(Difficulty.NORMAL, true);

			game.on(GameOpenListener.EVENT, active::open);
			game.on(GameCloseListener.EVENT, active::close);

			game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
			game.on(PlayerAddListener.EVENT, active::addPlayer);
			game.on(PlayerRemoveListener.EVENT, active::removePlayer);

			game.on(GameTickListener.EVENT, active::tick);

			game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
			game.on(EntityDeathListener.EVENT, active::onEntityDeath);
		});
	}

	private void open() {
		ServerWorld world = this.world.getWorld();

		// We do this to ensure that the world's time is set... thanks, UnmodifiableLevelProperties
		for (ServerWorld serverWorld : world.getServer().getWorlds()) {
			serverWorld.setTimeOfDay(18000L);
		}

		for (ServerPlayerEntity player : this.participants) {
			this.spawnParticipant(player);

			player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), 18000, false));
		}

		this.progress.start(world.getTime());
	}

	private void close() {
		ServerWorld world = this.world.getWorld();
		world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, world.getServer());
		world.getServer().setDifficulty(this.oldDifficulty, true);

		for (ServerWorld serverWorld : world.getServer().getWorlds()) {
			serverWorld.setTimeOfDay(1000L);
		}

		for (ServerPlayerEntity player : this.participants) {
			player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), 1000, false));
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
				ZombieEntity zombie = EntityType.ZOMBIE.create(world);
				BlockPos pos = WaveDefenseSpawnLogic.topPos(this.world, this.config);
				zombie.refreshPositionAndAngles(pos, 0, 0);
				// todo: zombie tiers
				zombie.setCustomName(new LiteralText("T1 Zombie"));
				world.spawnEntity(zombie);
			}
		}
	}

	private int zombieCount(int wave) {
		return (int) ((0.24 * wave * wave) + (0.95 * wave) + 8);
	}

	private ActionResult onEntityDeath(LivingEntity entity, DamageSource source) {
		if (entity.getType() == EntityType.ZOMBIE) {
			killedZombies++;

			if (source.getAttacker() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) source.getAttacker();
				player.inventory.insertStack(new ItemStack(Items.IRON_INGOT));
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

		ItemStackBuilder swordBuilder = ItemStackBuilder.of(Items.IRON_SWORD)
				.setUnbreakable();

		player.inventory.insertStack(swordBuilder.build());

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

		ItemScatterer.spawn(this.world.getWorld(), player.getBlockPos(), player.inventory);

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
}
