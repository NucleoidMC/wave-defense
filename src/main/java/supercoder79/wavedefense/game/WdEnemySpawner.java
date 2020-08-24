package supercoder79.wavedefense.game;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.config.EnemyConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class WdEnemySpawner {
    private static final long SPAWN_INTERVAL = 20;

    private final WdActive game;
    private final Random random = new Random();

    private long lastSpawnTime;

    WdEnemySpawner(WdActive game) {
        this.game = game;
    }

    public void tick(long time) {
        if (time - lastSpawnTime >= SPAWN_INTERVAL) {
            trySpawnEnemies();
            lastSpawnTime = time;
        }
    }

    private void trySpawnEnemies() {
        DangerField dangerField = game.dangerField;
        double spawnRadius = game.config.spawnRadius;

        for (ServerPlayerEntity participant : game.participants) {
            double deltaX = (random.nextDouble() - random.nextDouble()) * spawnRadius;
            double deltaZ = (random.nextDouble() - random.nextDouble()) * spawnRadius;

            BlockPos pos = new BlockPos(participant.getX() + deltaX, 0.0, participant.getZ() + deltaZ);
            trySpawnEnemyAt(dangerField, pos);
        }
    }

    private void trySpawnEnemyAt(DangerField dangerField, BlockPos pos) {
        double error = dangerField.getDangerErrorAt(pos.getX() + 0.5, pos.getZ() + 0.5);
        if (error > 0.0) {
            EnemyConfig enemy = selectEnemyFor(pos, error);
            if (enemy != null) {
                WaveEntity entity = spawnEnemy(pos, enemy);
                if (entity != null) {
                    game.dangerField.addEntity(entity);
                }
            }
        } else if (error < 0.0) {
            // TODO: handle reducing the danger level?
            //       this should probably only happen if far away from players
        }
    }

    @Nullable
    private EnemyConfig selectEnemyFor(BlockPos pos, double dangerCapacity) {
        List<EnemyConfig> validEnemies = new ArrayList<>();
        for (EnemyConfig enemy : game.config.enemies) {
            if (enemy.danger <= dangerCapacity) {
                if (enemy.type.canSpawnAt(game.world.getWorld(), pos)) {
                    validEnemies.add(enemy);
                }
            }
        }

        if (validEnemies.isEmpty()) {
            return null;
        }

        return validEnemies.get(random.nextInt(validEnemies.size()));
    }

    @Nullable
    private WaveEntity spawnEnemy(BlockPos pos, EnemyConfig enemy) {
        ServerWorld world = game.world.getWorld();
        BlockPos surface = WdSpawnLogic.findSurfaceAt(world, pos);
        if (surface == null) {
            return null;
        }

        WaveEntity entity = enemy.type.create(game, enemy);
        MobEntity mob = entity.asMob();

        mob.refreshPositionAndAngles(surface, 0, 0);
        mob.setPersistent();

        if (world.spawnEntity(mob)) {
            return entity;
        } else {
            return null;
        }
    }
}
