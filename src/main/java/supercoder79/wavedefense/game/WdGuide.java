package supercoder79.wavedefense.game;

import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.border.WorldBorder;
import supercoder79.wavedefense.entity.GuideVillagerEntity;
import supercoder79.wavedefense.map.gen.WdPath;
import xyz.nucleoid.plasmid.game.player.PlayerSet;

import java.util.List;
import java.util.Random;

public final class WdGuide implements PlayerSet.Listener {
    private static final float PAUSE_CHANCE = 1.0F / (20.0F * 15.0F);
    private static final long PAUSE_DURATION = 20 * 3;

    private final WdActive game;
    private final ServerWorld world;

    private final Random random = new Random();

    private GuideVillagerEntity entity;

    private Vec3d centerPos = Vec3d.ZERO;
    private double progressPercent;

    private double lastBorderX;
    private double lastBorderZ;

    private long pauseTime = -1;

    private int currentTargetIndex;

    public WdGuide(WdActive game) {
        this.game = game;
        this.world = game.world.getWorld();

        PlayerSet players = game.world.getPlayerSet();
        players.addListener(this);

        for (ServerPlayerEntity player : players) {
            this.onAddPlayer(player);
        }
    }

    public void tick(long time, boolean waveActive) {
        if (entity == null || entity.removed) {
            entity = spawnEntity(centerPos.x, centerPos.z);
        }

        // TODO: pause before wave also?

        if (!waveActive) {
            this.tickTraveling(time);
        } else {
            entity.setPaused(true);
        }

        if (time % 10 == 0) {
            WdPath.Progress progress = game.map.path.getProgressAt(entity.getX(), entity.getZ());
            this.centerPos = progress.center;
            this.progressPercent = progress.percent;

            this.updateWorldBorder();
        }
    }

    private void tickTraveling(long time) {
        boolean paused = tickPause(time);
        entity.setPaused(paused);

        if (paused) {
            return;
        }

        if (time % 10 == 0 || !entity.isNavigating()) {
            List<BlockPos> points = game.map.path.getPoints();
            if (currentTargetIndex >= points.size()) {
                return;
            }

            BlockPos targetPos = points.get(currentTargetIndex);
            targetPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, targetPos);

            entity.setTargetPos(targetPos);

            // when we get close to the target pos, update our target to the next one
            double deltaX = targetPos.getX() + 0.5 - entity.getX();
            double deltaZ = targetPos.getZ() + 0.5 - entity.getZ();
            if (deltaX * deltaX + deltaZ * deltaZ < 2.0 * 2.0) {
                currentTargetIndex++;
            }
        }
    }

    private boolean tickPause(long time) {
        if (pauseTime != -1) {
            if (time >= pauseTime) {
                pauseTime = -1;
                return false;
            }
            return true;
        } else if (random.nextFloat() < PAUSE_CHANCE) {
            pauseTime = time + PAUSE_DURATION;
            return true;
        }

        return false;
    }

    @Override
    public void onAddPlayer(ServerPlayerEntity player) {
        WorldBorder worldBorder = getWorldBorder();
        player.networkHandler.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INITIALIZE));
    }

    private void updateWorldBorder() {
        WorldBorder worldBorder = getWorldBorder();
        double deltaX = worldBorder.getCenterX() - lastBorderX;
        double deltaZ = worldBorder.getCenterZ() - lastBorderZ;

        if (deltaX * deltaX + deltaZ * deltaZ >= 0.25 * 0.25) {
            PlayerSet players = game.world.getPlayerSet();
            players.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_CENTER));

            lastBorderX = worldBorder.getCenterX();
            lastBorderZ = worldBorder.getCenterZ();
        }
    }

    private WorldBorder getWorldBorder() {
        WorldBorder worldBorder = new WorldBorder();

        double x = 0.5;
        double z = 0.5;
        if (entity != null) {
            x = entity.getX();
            z = entity.getZ();
        }

        worldBorder.setCenter(x, z);
        worldBorder.setSize(0.5);
        worldBorder.setDamagePerBlock(0.0);
        worldBorder.setWarningBlocks(-100000);
        worldBorder.setWarningTime(-100000);

        return worldBorder;
    }

    private GuideVillagerEntity spawnEntity(double x, double z) {
        BlockPos surfacePos = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(x, 0, z));

        GuideVillagerEntity entity = new GuideVillagerEntity(this.world);
        entity.refreshPositionAndAngles(surfacePos.getX() + 0.5, surfacePos.getY(), surfacePos.getZ() + 0.5, 0.0F, 0.0F);

        this.world.spawnEntity(entity);

        return entity;
    }

    public Vec3d getCenterPos() {
        return centerPos;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public double getProgressBlocks() {
        return progressPercent * game.map.path.getLength();
    }
}
