package supercoder79.wavedefense.game;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import supercoder79.wavedefense.map.WdMap;
import supercoder79.wavedefense.map.gen.WdPath;

import java.util.Collection;

public final class WdProgress {
    private final WdConfig config;
    private final WdMap map;

    private Vec3d centerPos = Vec3d.ZERO;
    private double progress;

    public WdProgress(WdConfig config, WdMap map) {
        this.config = config;
        this.map = map;
    }

    public void tick(long time, Collection<ServerPlayerEntity> players) {
        if (time % 20 == 0 || this.centerPos == null) {
            Vec3d meanPos = this.getMeanPos(players);
            WdPath.Progress meanProgress = this.map.path.getProgressAt(meanPos.x, meanPos.z);

            if (meanProgress.percent > this.progress) {
                this.progress = meanProgress.percent;
                this.centerPos = meanProgress.center;
            }

            this.damageFarPlayers(players);
        }
    }

    public Vec3d getCenterPos() {
        return this.centerPos;
    }

    public double getProgressBlocks() {
        return this.progress * this.map.path.getLength();
    }

    public double getProgress() {
        return progress;
    }

    private void damageFarPlayers(Collection<ServerPlayerEntity> players) {
        int maxDistance = this.config.spawnRadius + 5;
        double maxDistance2 = maxDistance * maxDistance;
        for (ServerPlayerEntity player : players) {
            double deltaX = player.getX() - this.centerPos.getX();
            double deltaZ = player.getZ() - this.centerPos.getZ();

            if (deltaX * deltaX + deltaZ * deltaZ > maxDistance2) {
                // Don't touch creative or spectator players
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }

                LiteralText message = new LiteralText("You are too far from your beacon!");
                player.sendMessage(message.formatted(Formatting.RED), true);

                player.damage(DamageSource.OUT_OF_WORLD, 0.5F);
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
