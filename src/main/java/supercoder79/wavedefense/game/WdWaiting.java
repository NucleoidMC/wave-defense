package supercoder79.wavedefense.game;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import supercoder79.wavedefense.map.WdMap;
import supercoder79.wavedefense.map.WdMapGenerator;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.world.bubble.BubbleWorldConfig;
import xyz.nucleoid.plasmid.world.bubble.BubbleWorldSpawner;

import java.util.concurrent.CompletableFuture;

public final class WdWaiting {
	private final GameWorld world;
	private final WdMap map;
	private final WdConfig config;

	private final WdSpawnLogic spawnLogic;

	private WdWaiting(GameWorld world, WdMap map, WdConfig config) {
		this.world = world;
		this.map = map;
		this.config = config;

		this.spawnLogic = new WdSpawnLogic(world, config);
	}

	public static CompletableFuture<GameWorld> open(GameOpenContext<WdConfig> context) {
		WdMapGenerator generator = new WdMapGenerator();

		return generator.create(context.getConfig())
				.thenCompose(map -> {
					BubbleWorldConfig worldConfig = new BubbleWorldConfig()
							.setGenerator(map.chunkGenerator(context.getServer()))
							.setDefaultGameMode(GameMode.SPECTATOR)
							.setSpawner(BubbleWorldSpawner.atSurface(0, 0))
							.setTimeOfDay(18000)
							.setDifficulty(Difficulty.NORMAL);

					return context.openWorld(worldConfig).thenApply(gameWorld -> {
						WdWaiting waiting = new WdWaiting(gameWorld, map, context.getConfig());

						gameWorld.openGame(game -> {
							game.setRule(GameRule.CRAFTING, RuleResult.DENY);
							game.setRule(GameRule.PORTALS, RuleResult.DENY);
							game.setRule(GameRule.PVP, RuleResult.DENY);
							game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
							game.setRule(GameRule.HUNGER, RuleResult.DENY);

							game.on(RequestStartListener.EVENT, waiting::requestStart);
							game.on(OfferPlayerListener.EVENT, waiting::offerPlayer);

							game.on(PlayerAddListener.EVENT, waiting::addPlayer);
							game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
							game.on(AttackEntityListener.EVENT, waiting::onAttackEntity);
							game.on(UseBlockListener.EVENT, waiting::onUseBlock);
							game.on(UseItemListener.EVENT, waiting::onUseItem);
						});

						return gameWorld;
					});
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

	private JoinResult offerPlayer(ServerPlayerEntity player) {
		if (this.world.getPlayerCount() >= this.config.playerConfig.getMaxPlayers()) {
			return JoinResult.gameFull();
		}

		return JoinResult.ok();
	}

	private StartResult requestStart() {
		if (this.world.getPlayerCount() < this.config.playerConfig.getMinPlayers()) {
			return StartResult.NOT_ENOUGH_PLAYERS;
		}

		WdActive.open(this.world, this.map, this.config);

		return StartResult.OK;
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
