package supercoder79.wavedefense.game;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.monster.*;
import supercoder79.wavedefense.entity.monster.classes.*;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.monster.waveentity.*;
import supercoder79.wavedefense.util.RandomCollection;

import java.util.ArrayList;
import java.util.Random;

public final class WdWaveSpawner {
    // Magic values for finding faraway players
    // sqrt2/2 works better with larger numbers... perhaps we need a smarter way of calculating these?
    private static final double SQRT3_2 = Math.sqrt(3) / 2.0;
    private static final double SQRT2_2 = Math.sqrt(2) / 2.0;

    private final WdActive game;
    private final WdWave wave;

    private final long startTime;

    private final ArrayList<WaveEntity> mobsToSpawn = new ArrayList<>();

    WdWaveSpawner(WdActive game, WdWave wave) {
        this.game = game;
        this.wave = wave;

        this.startTime = game.space.getWorld().getTime();

        int currentScore = wave.totalMonsterScore;

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

        while (currentScore >= 0) {
            BlockPos pos = randomMonsterSpawnPos(centerPos, random, validCenters);
            WaveEntity entity = createMob(this.wave.ordinal, world,
                    world.containsFluid(new Box(pos).expand(1.0)),
                    world.getBlockState(pos).getBlock().equals(Blocks.SNOW),
                    world.getBlockState(pos.down()).getBlock().equals(Blocks.SAND) || world.getBlockState(pos.down()).getBlock().equals(Blocks.SMOOTH_RED_SANDSTONE));

            MonsterModifier mod = MonsterModifier.next(this.wave.ordinal, entity);
            entity.setMod(mod);
            entity.getMonsterClass().apply((MobEntity) entity, mod, world.getRandom(), game.waveManager.getWaveOrdinal());

            if (entity instanceof WavePhantomEntity) {
                pos = WdSpawnLogic.findSurfaceAt((int) centerPos.getX() + random.nextInt(11) - 5, (int) centerPos.getZ() + random.nextInt(11) - 5, 12, game.space.getWorld())
                        .add(0, 16, 0);
            }

            if (entity.getMonsterClass().equals(StrayClasses.WIZARD)) {
                currentScore -= 20;
                wave.monsterCount++;
                wave.onMonsterAdded(20);
            }

            if (entity instanceof WaveCaveSpiderEntity)
                pos = pos.up();

            ((MobEntity) entity).refreshPositionAndAngles(pos, 0, 0);
            ((MobEntity) entity).setPersistent();

            mobsToSpawn.add(entity);
            currentScore -= entity.monsterScore();
            wave.monsterCount++;
            wave.onMonsterAdded(entity.monsterScore());
        }
    }

    public boolean tick(long time) {
        long timeSinceStart = time - this.startTime - 1;
        int mobTick = (int) (timeSinceStart / 5) + 1;

        if (mobTick <= mobsToSpawn.size() && timeSinceStart % 5 == 0) {
            if (spawnMonster(game.space.getWorld(), mobTick - 1)) {
                WaveEntity entity = mobsToSpawn.get(mobTick - 1);
                this.wave.onMonsterSpawned(entity.monsterScore());

                if (entity.getMonsterClass().equals(StrayClasses.WIZARD))
                    this.wave.onMonsterSpawned(20);
            }
        }

        return mobTick >= mobsToSpawn.size();
    }

    private BlockPos randomMonsterSpawnPos(Vec3d centerPos, Random random, WeightedList<Position> validCenters) {
        Position chosenPos = validCenters.pickRandom(random);

        // Spawn monsters closer to faraway players
        // TODO: some randomization here
        double distance = chosenPos == centerPos ? this.game.config.spawnRadius : 4;

        double theta = random.nextDouble() * 2 * Math.PI;
        int x = (int) (chosenPos.getX() + (Math.cos(theta) * distance));
        int z = (int) (chosenPos.getZ() + (Math.sin(theta) * distance));

        return WdSpawnLogic.findSurfaceAt(x, z, 12, game.space.getWorld());
    }

    private boolean spawnMonster(ServerWorld world, int order) {
        MobEntity monster;

        monster = (MobEntity) mobsToSpawn.get(order);

        if (monster instanceof WaveSummonerEntity) {
            world.spawnEntity(monster);
            SummonersSpiderEntity spider = new SummonersSpiderEntity(EntityType.SPIDER, this.game.space.getWorld());
            spider.refreshPositionAndAngles(monster.getBlockPos(), 0, 0);
            spider.setPersistent();
            this.game.space.getWorld().spawnEntity(spider);
            return monster.startRiding(spider);
        }

        if (monster instanceof WaveStrayEntity && ((WaveStrayEntity) monster).getMonsterClass().equals(StrayClasses.WIZARD)) {
            world.spawnEntity(monster);
            WizardsPhantomEntity phantom = new WizardsPhantomEntity(this.game.space.getWorld(), this.game);
            phantom.refreshPositionAndAngles(monster.getBlockPos(), 0, 0);
            phantom.setPersistent();
            phantom.setPhantomSize(3);
            this.game.space.getWorld().spawnEntity(phantom);
            return monster.startRiding(phantom);
        }

        monster.setCustomName(new LiteralText(mobsToSpawn.get(order).getMod().prefix + " " + mobsToSpawn.get(order).getMonsterClass().name()));

        return world.spawnEntity(monster);
    }

    private WaveEntity createMob(int waveOrdinal, ServerWorld world, boolean aquatic, boolean snow, boolean sand) {
        RandomCollection<WaveEntity> mobChoices = new RandomCollection<>();

        WaveEntity zombieType = new WaveZombieEntity(world, this.game, MonsterClass.nextZombie(waveOrdinal));
        WaveEntity skeletonType = new WaveSkeletonEntity(world, this.game, SkeletonClass.nextSkeleton(waveOrdinal));

        if (sand)
            zombieType = new WaveHuskEntity(world, this.game, MonsterClass.nextHusk(waveOrdinal));

        if (aquatic)
            zombieType = new WaveDrownedEntity(world, this.game, MonsterClass.nextDrowned(waveOrdinal));

        if (snow)
            skeletonType = new WaveStrayEntity(world, this.game, SkeletonClass.nextStray(waveOrdinal));

        mobChoices
                .add(wave.isSummonerWave ? 5 : 0,
                        new WaveSummonerEntity(
                                world,
                                game,
                                SkeletonClasses.SUMMONER))

                .add(Math.min(1.3, (waveOrdinal - 12) / 6d) * game.config.monsterSpawnChoices.witch,
                        new WaveWitchEntity(
                                world,
                                game,
                                WitchClasses.DEFAULT))

                .add(15 * game.config.monsterSpawnChoices.zombie,
                        zombieType)

                .add(Math.min(Math.max(0, (waveOrdinal - 2) / 6d), 4) * game.config.monsterSpawnChoices.skeleton,
                        skeletonType)

                .add(Math.min(Math.max(0, (waveOrdinal - 8) / 9d), 2) * game.config.monsterSpawnChoices.phantom,
                        new WavePhantomEntity(
                                world,
                                this.game,
                                PhantomClass.next(waveOrdinal)))

                .add(Math.min(Math.max(0, (waveOrdinal - 10) / 6d), 1) * game.config.monsterSpawnChoices.caveSpider + (wave.isSpiderWave ? 20 : 0),
                        new WaveCaveSpiderEntity(
                                world,
                                this.game,
                                CaveSpiderClasses.DEFAULT));

        return mobChoices.next();
    }

    // Weights go from 0.083ish to 0.5
    // 0.5\left(\frac{-1}{\left(x+1.2\right)}+1\right)
    private static double getDistWeight(double distance) {
        return 0.5 * ((-1.0 / (distance + 1.2)) + 1.0);
    }
}
