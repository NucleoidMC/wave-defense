package supercoder79.wavedefense.game;

import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.clock.ClockState;
import net.minecraft.world.clock.PackedClockStates;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import supercoder79.wavedefense.map.WdMap;
import supercoder79.wavedefense.map.WdMapGenerator;
import xyz.nucleoid.fantasy.RuntimeLevelConfig;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.item.ItemUseEvent;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

import java.util.Map;

public final class WdWaiting {
	private final GameSpace gameSpace;
	private final WdMap map;
	private final WdConfig config;

	private final WdSpawnLogic spawnLogic;
	private final ServerLevel world;

	private WdWaiting(GameSpace gameSpace, WdMap map, WdConfig config, ServerLevel world) {
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
		this.world = world;

		this.spawnLogic = new WdSpawnLogic(world, config);
	}

	public static GameOpenProcedure open(GameOpenContext<WdConfig> context) {
		WdMapGenerator generator = new WdMapGenerator();
		WdConfig config = context.config();

		WdMap map = generator.build(config, RandomSource.createThreadLocalInstance());
		RuntimeLevelConfig worldConfig = new RuntimeLevelConfig()
				.setGenerator(map.chunkGenerator(context.server()))
				.setDimensionType(BuiltinDimensionTypes.OVERWORLD)
				.setClockManagerConstructor(new PackedClockStates(Map.of(
						context.server().overworld().dimensionType().defaultClock().orElseThrow(), new ClockState(18000, 0, 0, true)
				)))
				.setDifficulty(Difficulty.NORMAL);

		return context.openWithLevel(worldConfig, (game, world) -> {
			WdWaiting waiting = new WdWaiting(game.getGameSpace(), map, config, world);
			GameWaitingLobby.addTo(game, context.config().playerConfig);

			game.setRule(GameRuleType.CRAFTING, EventResult.DENY);
			game.setRule(GameRuleType.PORTALS, EventResult.DENY);
			game.setRule(GameRuleType.PVP, EventResult.DENY);
			game.setRule(GameRuleType.FALL_DAMAGE, EventResult.DENY);
			game.setRule(GameRuleType.HUNGER, EventResult.DENY);

			game.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);

			game.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
			game.listen(GamePlayerEvents.ACCEPT, offer -> offer.teleport(world, new Vec3(0, world.getHeight(Heightmap.Types.MOTION_BLOCKING, 0, 0), 0)));
			game.listen(GamePlayerEvents.ADD, waiting::addPlayer);
			game.listen(PlayerDeathEvent.EVENT, waiting::onPlayerDeath);
			game.listen(PlayerAttackEntityEvent.EVENT, waiting::onAttackEntity);
			game.listen(BlockUseEvent.EVENT, waiting::onUseBlock);
			game.listen(ItemUseEvent.EVENT, waiting::onUseItem);
		});
	}

	private EventResult onAttackEntity(ServerPlayer attacker, InteractionHand hand, Entity attacked, EntityHitResult hitResult) {
		return EventResult.ALLOW;
	}

	private InteractionResult onUseBlock(ServerPlayer player, InteractionHand hand, BlockHitResult hitResult) {
		return InteractionResult.SUCCESS_SERVER;
	}

	private InteractionResult onUseItem(ServerPlayer player, InteractionHand hand) {
		return InteractionResult.SUCCESS_SERVER;
	}

	private GameResult requestStart() {
		WdActive.open(this.gameSpace, this.map, this.config, this.world);
		return GameResult.ok();
	}

	private void addPlayer(ServerPlayer player) {
		this.spawnPlayer(player);
	}

	private EventResult onPlayerDeath(ServerPlayer player, DamageSource source) {
		this.spawnPlayer(player);
		return EventResult.DENY;
	}

	private void spawnPlayer(ServerPlayer player) {
		this.spawnLogic.resetPlayer(player, GameType.ADVENTURE);
		this.spawnLogic.spawnPlayer(player);
	}
}
