package supercoder79.wavedefense.game;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import supercoder79.wavedefense.map.WdMap;
import supercoder79.wavedefense.map.WdMapGenerator;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
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

public final class WdWaiting {
	private final GameSpace gameSpace;
	private final WdMap map;
	private final WdConfig config;

	private final WdSpawnLogic spawnLogic;
	private final ServerWorld world;

	private WdWaiting(GameSpace gameSpace, WdMap map, WdConfig config, ServerWorld world) {
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
		this.world = world;

		this.spawnLogic = new WdSpawnLogic(world, config);
	}

	public static GameOpenProcedure open(GameOpenContext<WdConfig> context) {
		WdMapGenerator generator = new WdMapGenerator();
		WdConfig config = context.config();

		WdMap map = generator.build(config, Random.createLocal());
		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
				.setGenerator(map.chunkGenerator(context.server()))
				.setTimeOfDay(18000)
				.setDifficulty(Difficulty.NORMAL);

		return context.openWithWorld(worldConfig, (game, world) -> {
			WdWaiting waiting = new WdWaiting(game.getGameSpace(), map, config, world);
			GameWaitingLobby.addTo(game, context.config().playerConfig);

			game.setRule(GameRuleType.CRAFTING, EventResult.DENY);
			game.setRule(GameRuleType.PORTALS, EventResult.DENY);
			game.setRule(GameRuleType.PVP, EventResult.DENY);
			game.setRule(GameRuleType.FALL_DAMAGE, EventResult.DENY);
			game.setRule(GameRuleType.HUNGER, EventResult.DENY);

			game.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);

			game.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
			game.listen(GamePlayerEvents.ACCEPT, offer -> offer.teleport(world, new Vec3d(0, world.getTopY(Heightmap.Type.MOTION_BLOCKING, 0, 0), 0)));
			game.listen(GamePlayerEvents.ADD, waiting::addPlayer);
			game.listen(PlayerDeathEvent.EVENT, waiting::onPlayerDeath);
			game.listen(PlayerAttackEntityEvent.EVENT, waiting::onAttackEntity);
			game.listen(BlockUseEvent.EVENT, waiting::onUseBlock);
			game.listen(ItemUseEvent.EVENT, waiting::onUseItem);
		});
	}

	private EventResult onAttackEntity(ServerPlayerEntity attacker, Hand hand, Entity attacked, EntityHitResult hitResult) {
		return EventResult.ALLOW;
	}

	private ActionResult onUseBlock(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
		return ActionResult.SUCCESS_SERVER;
	}

	private ActionResult onUseItem(ServerPlayerEntity player, Hand hand) {
		return ActionResult.SUCCESS_SERVER;
	}

	private GameResult requestStart() {
		WdActive.open(this.gameSpace, this.map, this.config, this.world);
		return GameResult.ok();
	}

	private void addPlayer(ServerPlayerEntity player) {
		this.spawnPlayer(player);
	}

	private EventResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		this.spawnPlayer(player);
		return EventResult.DENY;
	}

	private void spawnPlayer(ServerPlayerEntity player) {
		this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
		this.spawnLogic.spawnPlayer(player);
	}
}
