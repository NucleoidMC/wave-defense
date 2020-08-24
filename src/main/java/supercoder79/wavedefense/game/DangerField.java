package supercoder79.wavedefense.game;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import supercoder79.wavedefense.entity.WaveEntity;

import java.util.ArrayList;
import java.util.List;

// TODO: if danger is low and target is high, spawn
//       if danger is high and target is low, teleport
//        for far away entities, we can tick them all and check the danger levels where they are
public final class DangerField {
    private Vec3d center = Vec3d.ZERO;
    private final double safeRadius2;

    private final double strayDangerScale;
    private double idleDanger;

    private final List<WaveEntity> entities = new ArrayList<>();

    public DangerField(WdConfig config) {
        this.safeRadius2 = config.playRadius * config.playRadius;
        this.strayDangerScale = config.danger.strayScale;
    }

    public void update(Vec3d center, double idleDanger) {
        this.center = center;
        this.idleDanger = idleDanger;
    }

    public void addEntity(WaveEntity entity) {
        this.entities.add(entity);
    }

    public void removeEntity(WaveEntity entity) {
        this.entities.remove(entity);
    }

    public double getDangerErrorAt(double x, double z) {
        return this.getTargetDangerAt(x, z) - this.getDangerAt(x, z);
    }

    public double getTargetDangerAt(double x, double z) {
        double dx = center.x - x;
        double dz = center.z - z;
        double r2 = dx * dx + dz * dz;

        double strayDanger = strayDangerScale * (Math.max(r2 / safeRadius2, 1.0) - 1.0);
        return strayDanger + idleDanger;
    }

    public double getDangerAt(double x, double z) {
        double danger = 0.0;

        for (WaveEntity entity : this.entities) {
            MobEntity mob = entity.asMob();
            double dx = x - mob.getX();
            double dz = z - mob.getZ();
            double r2 = dx * dx + dz * dz;
            danger += entity.getEnemyConfig().danger / r2;
        }

        return danger;
    }
}
