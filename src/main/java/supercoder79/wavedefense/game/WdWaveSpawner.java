package supercoder79.wavedefense.game;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import supercoder79.wavedefense.entity.ZombieClass;
import supercoder79.wavedefense.entity.ZombieClasses;
import supercoder79.wavedefense.entity.ZombieModifier;
import supercoder79.wavedefense.entity.WaveDrownedEntity;
import supercoder79.wavedefense.entity.WaveZombieEntity;

import java.util.Random;

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
        ZombieClass zombieClass = getZombieClass(world.getRandom(), wave.ordinal);
        ZombieModifier mod = getZombieModifier(world.getRandom()); //TODO: scale based on ordinal

        MobEntity zombie;
        if (world.containsFluid(new Box(pos).expand(1.0))) {
            zombie = new WaveDrownedEntity(world, game, mod, ZombieClasses.DROWNED);
        } else {
            zombie = new WaveZombieEntity(world, game, mod, zombieClass);
        }

        zombie.refreshPositionAndAngles(pos, 0, 0);
        zombie.setPersistent();

        return world.spawnEntity(zombie);
    }

    private ZombieClass getZombieClass(Random random, int waveOrdinal) {
        if (waveOrdinal > 5 && random.nextInt((int) (500.0 / (waveOrdinal - 5))) == 0) {
            return ZombieClasses.KNIGHT;
        }

        return ZombieClasses.DEFAULT;
    }

    private ZombieModifier getZombieModifier(Random random) {
        int r = random.nextInt(50);

        if (r <= 1) { // 4% chance of withering
            return ZombieModifier.WITHER;
        } else if (r <= 5) { // 8% chance of poison
            return ZombieModifier.POISON;
        } else if (r <= 10) { // 10% chance of weakness
            return ZombieModifier.WEAKNESS;
        }

        return ZombieModifier.NORMAL;
    }
}
