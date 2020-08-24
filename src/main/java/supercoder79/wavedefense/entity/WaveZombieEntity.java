package supercoder79.wavedefense.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import supercoder79.wavedefense.entity.config.EnemyConfig;
import supercoder79.wavedefense.entity.config.ZombieClass;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.game.WdActive;

public final class WaveZombieEntity extends ZombieEntity implements WaveEntity {
    private final WdActive game;
    private final EnemyConfig enemyConfig;
    private final ZombieClass zombieClass;

    public WaveZombieEntity(World world, WdActive game, EnemyConfig enemyConfig, ZombieClass zombieClass) {
        super(world);
        this.game = game;
        this.enemyConfig = enemyConfig;
        this.zombieClass = zombieClass;

        this.zombieClass.equipment.applyTo(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(2, new MoveTowardGameCenterGoal<>(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean didAttack = super.tryAttack(target);
        if (didAttack && target instanceof LivingEntity) {
            this.zombieClass.modifiers.applyTo((LivingEntity) target);
        }

        return didAttack;
    }

    @Override
    public EnemyConfig getEnemyConfig() {
        return this.enemyConfig;
    }

    @Override
    public WdActive getGame() {
        return game;
    }

    @Override
    protected void convertInWater() {
    }

    @Override
    protected void convertTo(EntityType<? extends ZombieEntity> entityType) {
    }
}
