package supercoder79.wavedefense.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;

public final class GuideVillagerEntity extends Villager {
    private BlockPos targetPos;
    private boolean paused;

    public GuideVillagerEntity(Level world) {
        super(EntityTypes.VILLAGER, world);

        this.setInvulnerable(true);
    }

    public void setTargetPos(BlockPos pos) {
        this.targetPos = pos;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }


    @Override
    protected void customServerAiStep(ServerLevel world) {
        if (this.paused) {
            this.navigation.stop();
        } else if (this.targetPos != null && this.navigation.isDone()) {
            this.navigation.moveTo(this.targetPos.getX() + 0.5, this.targetPos.getY(), this.targetPos.getZ() + 0.5, 0.5);
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
    public boolean isPersistenceRequired() {
        return true;
    }
}
