package supercoder79.wavedefense.game;

import supercoder79.wavedefense.entity.GuideVillagerEntity;
import supercoder79.wavedefense.map.gen.WdPath;
import xyz.nucleoid.plasmid.api.game.player.PlayerSet;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public final class WdGuide {
    private static final float PAUSE_CHANCE = 1.0F / (20.0F * 15.0F);
    private static final long PAUSE_DURATION = 20 * 3;

    private final WdActive game;
    private final ServerLevel world;

    private final RandomSource random;

    private GuideVillagerEntity entity;

    private Vec3 centerPos = Vec3.ZERO;
    private double progressPercent;

    private long pauseTime = -1;

    private int currentTargetIndex;

    public WdGuide(WdActive game) {
        this.game = game;
        this.world = game.world;
        this.random = game.world.getRandom();

        PlayerSet players = game.space.getPlayers();

        for (ServerPlayer player : players) {
            this.onAddPlayer(player);
        }
    }

    public void tick(long time, boolean waveActive) {
        if (entity == null || entity.isRemoved()) {
            entity = spawnEntity(centerPos.x, centerPos.z);
        }

        // TODO: pause before wave also?

        if (!waveActive) {
            this.tickTraveling(time);
        } else {
            entity.setPaused(true);
        }

        if (time % 10 == 0) {
            WdPath.Progress progress = game.map.path().getProgressAt(entity.getX(), entity.getZ());
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

        if (time % 10 == 0 || !entity.isPathFinding()) {
            List<BlockPos> points = game.map.path().getPoints();
            if (currentTargetIndex >= points.size()) {
                return;
            }

            BlockPos targetPos = points.get(currentTargetIndex);
            targetPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetPos);

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

    public void onAddPlayer(ServerPlayer player) {
        WorldBorder worldBorder = getWorldBorder();
        player.connection.send(new ClientboundInitializeBorderPacket(worldBorder));
    }

    private void updateWorldBorder() {
        WorldBorder worldBorder = getWorldBorder();
        double size = worldBorder.getSize();

        for (ServerPlayer player : game.space.getPlayers()) {
            double deltaX = player.getX() - worldBorder.getCenterX();
            double deltaZ = player.getZ() - worldBorder.getCenterZ();

            boolean hidden = deltaX * deltaX + deltaZ * deltaZ < 1.5 * 1.5;
            worldBorder.setSize(hidden ? 20000.0 : size);

            player.connection.send(new ClientboundSetBorderCenterPacket(worldBorder));
            player.connection.send(new ClientboundSetBorderSizePacket(worldBorder));
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
        worldBorder.setSize(0.25);
        worldBorder.setDamagePerBlock(0.0);
        worldBorder.setWarningBlocks(-100000);
        worldBorder.setWarningTime(-100000);

        return worldBorder;
    }

    private GuideVillagerEntity spawnEntity(double x, double z) {
        BlockPos surfacePos = this.world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.containing(x, 0, z));

        GuideVillagerEntity entity = new GuideVillagerEntity(this.world);
        entity.snapTo(surfacePos.getX() + 0.5, surfacePos.getY(), surfacePos.getZ() + 0.5, 0.0F, 0.0F);

        this.world.addFreshEntity(entity);

        return entity;
    }

    public Vec3 getCenterPos() {
        return centerPos;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public double getProgressBlocks() {
        return progressPercent * game.map.path().getLength();
    }
}
