package supercoder79.wavedefense;

import java.util.Set;
import java.util.stream.Collectors;

import supercoder79.wavedefense.map.WaveDefenseMap;
import supercoder79.wavedefense.map.WaveDefenseSpawnLogic;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.event.EntityDeathListener;
import xyz.nucleoid.plasmid.game.event.GameCloseListener;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.PlayerRef;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
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
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class WaveDefenseActive {
	private final GameWorld world;
	private final WaveDefenseMap map;
	private final WaveDefenseConfig config;

	private final Set<PlayerRef> participants;

	private final WaveDefenseSpawnLogic spawnLogic;

	private boolean shouldSpawn = true;
	private int zombiesToSpawn = 8;
	private int killedZombies = 0;
	private int currentWave = 1;
	private long nextWaveTick = Long.MAX_VALUE;

	private WaveDefenseActive(GameWorld world, WaveDefenseMap map, WaveDefenseConfig config, Set<PlayerRef> participants) {
		this.world = world;
		this.map = map;
		this.config = config;
		this.participants = participants;

		this.spawnLogic = new WaveDefenseSpawnLogic(world, config);
	}

	public static void open(GameWorld world, WaveDefenseMap map, WaveDefenseConfig config) {
		Set<PlayerRef> participants = world.getPlayers().stream()
				.map(PlayerRef::of)
				.collect(Collectors.toSet());

		WaveDefenseActive active = new WaveDefenseActive(world, map, config, participants);

		world.openGame(game -> {
			game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.DENY);
			game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
			game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
			game.setRule(GameRule.HUNGER, RuleResult.DENY);

			world.getWorld().getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, world.getWorld().getServer());

			game.on(GameOpenListener.EVENT, active::open);
			game.on(GameCloseListener.EVENT, active::close);

			game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
			game.on(PlayerAddListener.EVENT, active::addPlayer);

			game.on(GameTickListener.EVENT, active::tick);

			game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
			game.on(EntityDeathListener.EVENT, active::onEntityDeath);
		});
	}

	private void open() {
		ServerWorld world = this.world.getWorld();

		// World border stuff
		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(config.borderSize);
		world.getWorldBorder().setDamagePerBlock(0.5);

		// We do this to ensure that the world's time is set... thanks, UnmodifiableLevelProperties
		for (ServerWorld serverWorld : world.getServer().getWorlds()) {
			serverWorld.setTimeOfDay(18000L);
		}

		for (PlayerRef playerId : this.participants) {
			ServerPlayerEntity player = (ServerPlayerEntity) world.getPlayerByUuid(playerId.getId());
			if (player != null) {
				this.spawnParticipant(player);
				player.networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.SET_CENTER));
				player.networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.SET_SIZE));

				player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), 18000, false));
			}
		}
	}

	private void close() {
		ServerWorld world = this.world.getWorld();
		world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, world.getServer());

		for (ServerWorld serverWorld : world.getServer().getWorlds()) {
			serverWorld.setTimeOfDay(1000L);
		}

		for (PlayerRef playerId : this.participants) {
			ServerPlayerEntity player = (ServerPlayerEntity) world.getPlayerByUuid(playerId.getId());
			if (player != null) {
				player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), 1000, false));
			}
		}
	}

	private void addPlayer(ServerPlayerEntity player) {
		if (!this.participants.contains(PlayerRef.of(player))) {
			this.spawnSpectator(player);
		}
	}

	private void tick() {
		ServerWorld world = this.world.getWorld();

		if (world.getTime() > nextWaveTick) {
			shouldSpawn = true;
			killedZombies = 0;
			nextWaveTick = Long.MAX_VALUE;
			broadcastMessage(new LiteralText("Starting wave " + currentWave + "!"));
		}


		if (shouldSpawn) {
			shouldSpawn = false;

			for (int i = 0; i < zombiesToSpawn; i++) {
				ZombieEntity zombie = EntityType.ZOMBIE.create(world);
				BlockPos pos = WaveDefenseSpawnLogic.topPos(this.world, this.config);
				zombie.refreshPositionAndAngles(pos, 0, 0);
				world.spawnEntity(zombie);
			}
		}
	}

	private ActionResult onEntityDeath(LivingEntity entity, DamageSource source) {
		if (entity.getType() == EntityType.ZOMBIE) {
			killedZombies++;

			if (source.getAttacker() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) source.getAttacker();
				player.sendMessage(new LiteralText("Killed zombie! " + (zombiesToSpawn - killedZombies) + " remain."), false);
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