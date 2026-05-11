package supercoder79.wavedefense.entity.goal;

import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.game.WdActive;

import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public final class MoveTowardGameCenterGoal<T extends PathfinderMob & WaveEntity> extends Goal {
    private final T entity;

    public MoveTowardGameCenterGoal(T entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.getNavigation().isDone()) {
            WdActive game = entity.getGame();
            double distance2 = entity.distanceToSqr(game.guide.getCenterPos());
            return distance2 > game.config.spawnRadius * game.config.spawnRadius;
        }
        return false;
    }

    @Override
    public void start() {
        WdActive game = entity.getGame();
        Vec3 center = game.guide.getCenterPos();
        Vec3 target = LandRandomPos.getPosTowards(entity, 15, 15, center);

        if (target != null) {
            entity.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}
