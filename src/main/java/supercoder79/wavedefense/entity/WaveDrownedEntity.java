package supercoder79.wavedefense.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.game.WdActive;

public final class WaveDrownedEntity extends DrownedEntity implements WaveEntity {
    private final WdActive game;
    private final int tier;

    public WaveDrownedEntity(World world, WdActive game, int tier) {
        super(EntityType.DROWNED, world);
        this.game = game;
        this.tier = tier;

        ZombieTiers.apply(this, tier);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new DrownedEntity.DrownedAttackGoal(this, 1.0, false));
        this.goalSelector.add(1, new DrownedEntity.TargetAboveWaterGoal(this, 1.0, 48));
        this.goalSelector.add(2, new MoveTowardGameCenterGoal<>(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::canDrownedAttackTarget));
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public WdActive getGame() {
        return game;
    }
}
