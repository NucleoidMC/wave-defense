package supercoder79.wavedefense.game;

import java.util.concurrent.CompletableFuture;

import supercoder79.wavedefense.map.WaveDefenseMap;
import supercoder79.wavedefense.map.WaveDefenseMapGenerator;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.AttackEntityListener;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.event.UseBlockListener;
import xyz.nucleoid.plasmid.game.event.UseItemListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.game.world.bubble.BubbleWorldConfig;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.GameMode;

public final class WaveDefenseWaiting {
	private final GameWorld world;
	private final WaveDefenseMap map;
	private final WaveDefenseConfig config;

	private final WaveDefenseSpawnLogic spawnLogic;

	private WaveDefenseWaiting(GameWorld world, WaveDefenseMap map, WaveDefenseConfig config) {
		this.world = world;
		this.map = map;
		this.config = config;

		this.spawnLogic = new WaveDefenseSpawnLogic(world, config);
	}

	public static CompletableFuture<Void> open(GameOpenContext<WaveDefenseConfig> context) {
		WaveDefenseMapGenerator generator = new WaveDefenseMapGenerator();

		return generator.create(context.getConfig()).thenAccept(map -> {
			BubbleWorldConfig worldConfig = new BubbleWorldConfig()
					.setGenerator(map.chunkGenerator(context.getServer()))
					.setDefaultGameMode(GameMode.SPECTATOR);

			GameWorld world = context.openWorld(worldConfig);

			WaveDefenseWaiting waiting = new WaveDefenseWaiting(world, map, context.getConfig());

			world.openGame(game -> {
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
			return StartResult.notEnoughPlayers();
		}

		WaveDefenseActive.open(this.world, this.map, this.config);

		return StartResult.ok();
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
