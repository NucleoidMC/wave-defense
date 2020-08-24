package supercoder79.wavedefense.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import supercoder79.wavedefense.entity.config.DrownedClass;
import supercoder79.wavedefense.entity.config.EnemyConfig;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.game.WdActive;

public final class WaveDrownedEntity extends DrownedEntity implements WaveEntity {
    private final WdActive game;
    private final EnemyConfig enemyConfig;
    private final DrownedClass drownedClass;

    public WaveDrownedEntity(World world, WdActive game, EnemyConfig enemyConfig, DrownedClass drownedClass) {
        super(EntityType.DROWNED, world);
        this.game = game;
        this.enemyConfig = enemyConfig;
        this.drownedClass = drownedClass;

        this.drownedClass.equipment.applyTo(this);
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
    public boolean tryAttack(Entity target) {
        boolean didAttack = super.tryAttack(target);
        if (didAttack && target instanceof LivingEntity) {
            this.drownedClass.modifiers.applyTo((LivingEntity) target);
        }

        return didAttack;
    }

    @Override
    public EnemyConfig getEnemyConfig() {
        return enemyConfig;
    }

    @Override
    public WdActive getGame() {
        return game;
    }
}
