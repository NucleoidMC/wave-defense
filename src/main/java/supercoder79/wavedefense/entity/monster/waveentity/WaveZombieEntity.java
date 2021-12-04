package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.monster.classes.MonsterClasses;
import supercoder79.wavedefense.game.WdActive;

public final class WaveZombieEntity extends ZombieEntity implements WaveEntity {
    private final WdActive game;
    private MonsterModifier mod;
    private MonsterClass monsterClass;

    public WaveZombieEntity(World world, WdActive game, MonsterClass monsterClass) {
        super(world);
        this.game = game;
        this.setMonsterClass(monsterClass);

        this.initializeGoals();
        this.setAttributes();
    }

    @Override
    protected void initGoals() {
    }

    protected void initializeGoals() {
        // TODO: custom attack goal
        this.goalSelector.add(1, new ZombieAttackGoal(this, this.getMonsterClass().speed(), false));
        this.goalSelector.add(2, new MoveTowardGameCenterGoal<>(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, WaveSkeletonEntity.class).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean didAttack = super.tryAttack(target);

        if (didAttack) {
            if (target instanceof LivingEntity && getMod().effect != null) {
                ((LivingEntity)target).addStatusEffect(getMod().effect.get());
            }
        }

        return didAttack;
    }

    public void setAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getMonsterClass().maxHealth());
        this.setHealth((float) this.getMonsterClass().maxHealth());
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(64d);
    }

    @Override
    public boolean showHealth() {
        return this.getMonsterClass().equals(MonsterClasses.TANK);
    }

    @Override
    public int ironCount() {
        return this.getMonsterClass().ironCount() + this.getMod().ironBonus;
    }

    @Override
    public int goldCount() {
        return this.getMonsterClass().goldCount();
    }

    @Override
    public int monsterScore() {
        return this.getMonsterClass().monsterPoints();
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

    @Override
    public MonsterClass getMonsterClass() {
        return monsterClass;
    }

    public void setMonsterClass(MonsterClass monsterClass) {
        this.monsterClass = monsterClass;
    }

    public MonsterModifier getMod() {
        return mod;
    }

    public void setMod(MonsterModifier mod) {
        this.mod = mod;
    }
}
