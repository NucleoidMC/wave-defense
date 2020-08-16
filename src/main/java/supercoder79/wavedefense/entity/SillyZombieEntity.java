package supercoder79.wavedefense.entity;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import supercoder79.wavedefense.game.WaveDefenseActive;

import java.util.EnumSet;

public final class SillyZombieEntity extends ZombieEntity {
    private final WaveDefenseActive game;

    public SillyZombieEntity(World world, WaveDefenseActive game) {
        super(world);
        this.game = game;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(2, new MoveTowardGameCenterGoal());
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
    }

    private class MoveTowardGameCenterGoal extends Goal {
        public MoveTowardGameCenterGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (SillyZombieEntity.this.navigation.isIdle()) {
                WaveDefenseActive game = SillyZombieEntity.this.game;
                double distance2 = SillyZombieEntity.this.squaredDistanceTo(game.progress.getCenterPos());
                return distance2 > game.config.spawnRadius * game.config.spawnRadius;
            }
            return false;
        }

        @Override
        public void start() {
            Vec3d center = SillyZombieEntity.this.game.progress.getCenterPos();
            Vec3d target = TargetFinder.findTargetTowards(SillyZombieEntity.this, 15, 15, center);

            if (target != null) {
                SillyZombieEntity.this.navigation.startMovingTo(target.x, target.y, target.z, 1.0);
            }
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }
    }
}
