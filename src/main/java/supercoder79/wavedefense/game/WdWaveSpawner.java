package supercoder79.wavedefense.game;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

import supercoder79.wavedefense.entity.monster.classes.*;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.monster.WaveDrownedEntity;
import supercoder79.wavedefense.entity.monster.WaveSkeletonEntity;
import supercoder79.wavedefense.entity.monster.WaveZombieEntity;

import java.util.Random;

public final class WdWaveSpawner {
    // Magic values for finding faraway players
    // sqrt2/2 works better with larger numbers... perhaps we need a smarter way of calculating these?
    private static final double SQRT3_2 = Math.sqrt(3) / 2.0;
    private static final double SQRT2_2 = Math.sqrt(2) / 2.0;
    private static final long SPAWN_TICKS = 20 * 5;

    private final WdActive game;
    private final WdWave wave;

    private final long startTime;
    private int spawnedMonsters;

    WdWaveSpawner(WdActive game, WdWave wave) {
        this.game = game;
        this.wave = wave;

        this.startTime = game.space.getWorld().getTime();
    }

    public boolean tick(long time) {
        long timeSinceStart = time - this.startTime;
        int targetMonsters = Math.min((int) (timeSinceStart * this.wave.totalMonsters / SPAWN_TICKS), this.wave.totalMonsters);

        if (targetMonsters > this.spawnedMonsters) {
            ServerWorld world = this.game.space.getWorld();
            Vec3d centerPos = this.game.guide.getCenterPos();
            Random random = new Random();

            WeightedList<Position> validCenters = new WeightedList<>();
            validCenters.add(centerPos, this.game.getParticipants().size() * 100);

            for (ServerPlayerEntity participant : this.game.getParticipants()) {
                BlockPos pos = participant.getBlockPos();
                double aX = pos.getX() - centerPos.getX();
                double aZ = pos.getZ() - centerPos.getZ();
                double dist = (aX * aX) + (aZ * aZ);

                double threshold = this.game.config.spawnRadius * SQRT2_2;

                if (dist * dist >= threshold * threshold) {
                    validCenters.add(participant.getPos(), (int) (getDistWeight(dist - threshold) * 100));
                }
            }

            for (int i = spawnedMonsters; i < targetMonsters; i++) {
                Position chosenPos = validCenters.pickRandom(random);

                // Spawn monsters closer to faraway players
                // TODO: some randomization here
                double distance = chosenPos == centerPos ? this.game.config.spawnRadius : 4;

                double theta = random.nextDouble() * 2 * Math.PI;
                int x = (int) (chosenPos.getX() + (Math.cos(theta) * distance));
                int z = (int) (chosenPos.getZ() + (Math.sin(theta) * distance));

                BlockPos pos = WdSpawnLogic.findSurfaceAt(x, z, 12, world);
                if (spawnMonster(world, pos)) {
                    this.wave.onMonsterAdded();
                }
            }

            this.spawnedMonsters = targetMonsters;
        }

        return this.spawnedMonsters >= this.wave.totalMonsters;
    }

    private boolean spawnMonster(ServerWorld world, BlockPos pos) {
        MonsterModifier mod = getMonsterModifier(world.getRandom(), this.wave.ordinal);

        MobEntity monster;
        if (world.containsFluid(new Box(pos).expand(1.0))) {
            monster = new WaveDrownedEntity(world, this.game, mod, DrownedClasses.DROWNED);
        } else {
            monster = createMob(world.getRandom(), this.wave.ordinal, world, mod);
        }

        monster.refreshPositionAndAngles(pos, 0, 0);
        monster.setPersistent();

        return world.spawnEntity(monster);
    }

    private MobEntity createMob(Random random, int waveOrdinal, ServerWorld world, MonsterModifier mod) {
        if (random.nextInt((int) Math.max(10.0, 75.0 / waveOrdinal)) == 0) {
            SkeletonClass skeletonClass = getSkeletonClass(random, waveOrdinal);

            return new WaveSkeletonEntity(world, this.game, mod, skeletonClass);
        } else {
            MonsterClass zombieClass = getZombieClass(random, waveOrdinal);

            return new WaveZombieEntity(world, this.game, mod, zombieClass);
        }
    }

    private MonsterClass getZombieClass(Random random, int waveOrdinal) {
        if (waveOrdinal > 10) {
            if (random.nextInt((int) (150.0 / (waveOrdinal - 10))) == 0) {
                return ZombieClasses.TANK;
            }

            if (random.nextInt((int) (100.0 / (waveOrdinal - 10))) == 0) {
                return ZombieClasses.SCOUT;
            }
        }

        if (waveOrdinal > 5 && random.nextInt((int) (100.0 / (waveOrdinal - 5))) == 0) {
            return ZombieClasses.KNIGHT;
        }

        if (random.nextInt((int) (75.0 / waveOrdinal)) == 0) {
            return ZombieClasses.FIGHTER;
        }

        if (random.nextInt((int) (75.0 / waveOrdinal)) == 0) {
            return ZombieClasses.RUNNER;
        }

        return ZombieClasses.DEFAULT;
    }

    private SkeletonClass getSkeletonClass(Random random, int waveOrdinal) {
        if (waveOrdinal > 5) {
            if (random.nextInt((int) (100.0 / (waveOrdinal - 5))) == 0) {
                return SkeletonClasses.SNIPER;
            }

            if (random.nextInt((int) (75.0 / (waveOrdinal - 5))) == 0) {
                return SkeletonClasses.RAPIDSHOOTER;
            }
        }

        return SkeletonClasses.SKELETON;
    }

    private MonsterModifier getMonsterModifier(Random random, int ordinal) {
        int r = random.nextInt((int) getModBound(ordinal));

        if (r <= 1) {
            return MonsterModifier.WITHER;
        } else if (r <= 3) {
            return MonsterModifier.POISON;
        } else if (r <= 6) {
            return MonsterModifier.WEAKNESS;
        } else if (r <= 8) {
            return MonsterModifier.HUNGER;
        } else if (r <= 11) {
            return MonsterModifier.SLOWNESS;
        } else if (r <= 14) {
            return MonsterModifier.BLINDNESS;
        } else if (r <= 16) {
            return MonsterModifier.NAUSEA;
        }

        return MonsterModifier.NORMAL;
    }

    // The bound for the random.nextInt used to get the modifier, starts at 50 and gets lower
    // -5\ln x+50
    private static double getModBound(int ordinal) {
        return (-5 * Math.log(ordinal)) + 50;
    }

    // Weights go from 0.083ish to 0.5
    // 0.5\left(\frac{-1}{\left(x+1.2\right)}+1\right)
    private static double getDistWeight(double distance) {
        return 0.5 * ((-1.0 / (distance + 1.2)) + 1.0);
    }
}
