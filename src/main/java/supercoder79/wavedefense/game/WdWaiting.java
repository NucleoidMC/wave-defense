package supercoder79.wavedefense.game;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import supercoder79.wavedefense.map.WdMap;
import supercoder79.wavedefense.map.WdMapGenerator;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
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

		WdMap map = generator.build(config);
		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
				.setGenerator(map.chunkGenerator(context.server()))
				.setTimeOfDay(18000)
				.setDifficulty(Difficulty.NORMAL);

		return context.openWithWorld(worldConfig, (game, world) -> {
			WdWaiting waiting = new WdWaiting(game.getGameSpace(), map, config, world);
			GameWaitingLobby.addTo(game, context.config().playerConfig);

			game.setRule(GameRuleType.CRAFTING, ActionResult.FAIL);
			game.setRule(GameRuleType.PORTALS, ActionResult.FAIL);
			game.setRule(GameRuleType.PVP, ActionResult.FAIL);
			game.setRule(GameRuleType.FALL_DAMAGE, ActionResult.FAIL);
			game.setRule(GameRuleType.HUNGER, ActionResult.FAIL);

			game.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);

			game.listen(GamePlayerEvents.OFFER, offer -> offer.accept(world, new Vec3d(0, world.getTopY(Heightmap.Type.MOTION_BLOCKING, 0, 0), 0)));
			game.listen(GamePlayerEvents.ADD, waiting::addPlayer);
			game.listen(PlayerDeathEvent.EVENT, waiting::onPlayerDeath);
			game.listen(PlayerAttackEntityEvent.EVENT, waiting::onAttackEntity);
			game.listen(BlockUseEvent.EVENT, waiting::onUseBlock);
			game.listen(ItemUseEvent.EVENT, waiting::onUseItem);
		});
	}

	private ActionResult onAttackEntity(ServerPlayerEntity attacker, Hand hand, Entity attacked, EntityHitResult hitResult) {
		return ActionResult.SUCCESS;
	}

	private ActionResult onUseBlock(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
		return ActionResult.SUCCESS;
	}

	private TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand) {
		return TypedActionResult.success(player.getStackInHand(hand));
	}

	private GameResult requestStart() {
		WdActive.open(this.gameSpace, this.map, this.config, this.world);
		return GameResult.ok();
	}

	private void addPlayer(ServerPlayerEntity player) {
		this.spawnPlayer(player);
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		this.spawnPlayer(player);
		return ActionResult.FAIL;
	}

	private void spawnPlayer(ServerPlayerEntity player) {
		this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
		this.spawnLogic.spawnPlayer(player);
	}
}
