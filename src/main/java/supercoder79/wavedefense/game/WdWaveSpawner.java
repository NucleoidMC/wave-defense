package supercoder79.wavedefense.game;

import java.util.Random;

import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import supercoder79.wavedefense.entity.SillyZombieEntity;

public final class WdWaveSpawner {
    private static final long SPAWN_TICKS = 20 * 5;

    private final WdActive game;
    private final WdWave wave;

    private final long startTime;
    private int spawnedZombies;

    WdWaveSpawner(WdActive game, WdWave wave) {
        this.game = game;
        this.wave = wave;

        this.startTime = game.world.getWorld().getTime();
    }

    public boolean tick(long time) {
        long timeSinceStart = time - startTime;
        int targetZombies = Math.min((int) (timeSinceStart * wave.totalZombies / SPAWN_TICKS), wave.totalZombies);

        if (targetZombies > spawnedZombies) {
            ServerWorld world = game.world.getWorld();
            Vec3d centerPos = game.guide.getCenterPos();

            for (int i = spawnedZombies; i < targetZombies; i++) {
                BlockPos pos = WdSpawnLogic.findSurfaceAround(centerPos, world, game.config);
                if (spawnZombie(world, pos)) {
                    wave.onZombieAdded();
                }
            }

            spawnedZombies = targetZombies;
        }

        return spawnedZombies >= wave.totalZombies;
    }

    private boolean spawnZombie(ServerWorld world, BlockPos pos) {
        ZombieEntity zombie = new SillyZombieEntity(world, game, getRandomTier(world.getRandom(), wave.ordinal));
        zombie.setPersistent();

        zombie.refreshPositionAndAngles(pos, 0, 0);

        return world.spawnEntity(zombie);
    }

    private int getRandomTier(Random random, int waveOrdinal) {
        // T2: waves 10 - 20
        double t2Chance = MathHelper.clamp((0.1 * waveOrdinal) - 1, 0, 1);
        // T3: waves 20 - 30
        double t3Chance = MathHelper.clamp((0.1 * waveOrdinal) - 2, 0, 1);

        if (Math.random() < t3Chance) {
            // Instead of T3, T4s will have an increasing chance to spawn.
            double t4Chance = 500.0 / (waveOrdinal - 15);

            if (random.nextInt((int) t4Chance) == 0) {
                return 3;
            }

            return 2;
        } else if (Math.random() < t2Chance) {
            return 1;
        } else {
            return 0;
        }
    }
}
