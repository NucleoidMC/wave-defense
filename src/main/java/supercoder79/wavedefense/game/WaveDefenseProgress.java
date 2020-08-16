package supercoder79.wavedefense.game;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import supercoder79.wavedefense.map.WaveDefenseMap;

public final class WaveDefenseProgress {
    private final WaveDefenseConfig config;
    private final WaveDefenseMap map;

    private Vec3d lastSendCenter = Vec3d.ZERO;

    private final long totalTicks;
    private long startTime;

    public WaveDefenseProgress(WaveDefenseConfig config, WaveDefenseMap map) {
        this.config = config;
        this.map = map;

        this.totalTicks = map.path.getLength() * config.ticksPerBlock;
    }

    public void start(long time) {
        this.startTime = time;
    }

    // TODO: damage players who go out of range
    public void tick(ServerWorld world, long time) {
        Vec3d center = this.getCenterFor(time);

        if (!center.isInRange(this.lastSendCenter, 1.0)) {
            this.lastSendCenter = center;
        }
    }

    private Vec3d getCenterFor(long time) {
        double progress = (double) (time - this.startTime) / this.totalTicks;
        return this.map.path.getPointAlong(progress);
    }
}
