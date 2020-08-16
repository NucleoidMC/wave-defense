package supercoder79.wavedefense.game;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import supercoder79.wavedefense.map.WaveDefenseMap;

import java.util.Collection;
import java.util.List;

public final class WaveDefenseProgress {
    private final WaveDefenseConfig config;
    private final WaveDefenseMap map;

    private Vec3d centerPos = Vec3d.ZERO;
    private double progress;

    public WaveDefenseProgress(WaveDefenseConfig config, WaveDefenseMap map) {
        this.config = config;
        this.map = map;
    }

    public void tick(ServerWorld world, long time) {
        if (time % 20 == 0 || this.centerPos == null) {
            Vec3d meanPos = this.getMeanPos(world.getPlayers());
            double meanProgress = this.map.path.distanceAlongPath(meanPos.x, meanPos.z);

            if (meanProgress > this.progress) {
                this.progress = meanProgress;
            }

            this.centerPos = this.map.path.getPointAlong(this.progress);

            this.damageFarPlayers(world);
        }
    }

    public Vec3d getCenterPos() {
        return this.centerPos;
    }

    private void damageFarPlayers(ServerWorld world) {
        List<ServerPlayerEntity> players = world.getPlayers();

        double maxDistance2 = this.config.spawnRadius * this.config.spawnRadius;
        for (ServerPlayerEntity player : players) {
            double deltaX = player.getX() - this.centerPos.getX();
            double deltaZ = player.getZ() - this.centerPos.getZ();

            if (deltaX * deltaX + deltaZ * deltaZ > maxDistance2) {
                LiteralText message = new LiteralText("You are too far from your fellow players!");
                player.sendMessage(message.formatted(Formatting.RED), true);

                player.damage(DamageSource.OUT_OF_WORLD, 2.0F);
            }
        }
    }

    private Vec3d getMeanPos(Collection<ServerPlayerEntity> players) {
        double meanX = 0.0;
        double meanZ = 0.0;

        double weightPerPlayer = 1.0 / players.size();

        for (ServerPlayerEntity player : players) {
            meanX += player.getX() * weightPerPlayer;
            meanZ += player.getZ() * weightPerPlayer;
        }

        return new Vec3d(meanX, 0.0, meanZ);
    }
}
