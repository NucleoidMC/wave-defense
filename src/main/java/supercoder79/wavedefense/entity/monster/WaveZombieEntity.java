package supercoder79.wavedefense.entity.monster;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.game.WdActive;

public final class WaveZombieEntity extends ZombieEntity implements WaveEntity {
    private final WdActive game;
    private final MonsterModifier mod;
    private final MonsterClass monsterClass;

    public WaveZombieEntity(World world, WdActive game, MonsterModifier mod, MonsterClass monsterClass) {
        super(world);
        this.game = game;
        this.mod = mod;
        this.monsterClass = monsterClass;

        monsterClass.apply(this, mod, world.getRandom());

        this.initializeGoals();
        this.setAttributes();
    }

    @Override
    protected void initGoals() {
    }

    protected void initializeGoals() {
        // TODO: custom attack goal
        this.goalSelector.add(1, new ZombieAttackGoal(this, this.monsterClass.speed(), false));
        this.goalSelector.add(2, new MoveTowardGameCenterGoal<>(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, WaveSkeletonEntity.class).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean didAttack = super.tryAttack(target);

        if (didAttack) {
            if (target instanceof LivingEntity && mod.effect != null) {
                ((LivingEntity)target).addStatusEffect(mod.effect.get());
            }
        }

        return didAttack;
    }

    public void setAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.monsterClass.maxHealth());
        this.setHealth((float) this.monsterClass.maxHealth());
    }

    @Override
    public int ironCount() {
        return this.monsterClass.ironCount() + this.mod.ironBonus;
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
