package supercoder79.wavedefense.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class GuideVillagerEntity extends VillagerEntity {
    private BlockPos targetPos;
    private boolean paused;

    public GuideVillagerEntity(World world) {
        super(EntityType.VILLAGER, world);

        this.setInvulnerable(true);
    }

    public void setTargetPos(BlockPos pos) {
        this.targetPos = pos;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
    }

    @Override
    protected void mobTick() {
        if (this.paused) {
            this.navigation.stop();
        } else if (this.targetPos != null && this.navigation.isIdle()) {
            this.navigation.startMovingTo(this.targetPos.getX() + 0.5, this.targetPos.getY(), this.targetPos.getZ() + 0.5, 0.5);
        }
    }

    /*@Override
    public void pushAwayFrom(Entity entity) {
    }

    @Override
    protected void pushAway(Entity entity) {
    }

    @Override
    public void takeKnockback(float x, double y, double z) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }*/

    @Override
    public boolean isPersistent() {
        return true;
    }
}
