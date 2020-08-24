package supercoder79.wavedefense.entity.goal;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.game.WdActive;

import java.util.EnumSet;

public final class MoveTowardGameCenterGoal<T extends PathAwareEntity & WaveEntity> extends Goal {
    private final T entity;

    public MoveTowardGameCenterGoal(T entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (entity.getNavigation().isIdle()) {
            WdActive game = entity.getGame();
            double distance2 = entity.squaredDistanceTo(game.guide.getCenterPos());
            return distance2 > game.config.playRadius * game.config.playRadius;
        }
        return false;
    }

    @Override
    public void start() {
        WdActive game = entity.getGame();
        Vec3d center = game.guide.getCenterPos();
        Vec3d target = TargetFinder.findTargetTowards(entity, 15, 15, center);

        if (target != null) {
            entity.getNavigation().startMovingTo(target.x, target.y, target.z, 1.0);
        }
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }
}
