package supercoder79.wavedefense.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.MonsterClass;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.game.WdActive;

public final class WaveDrownedEntity extends DrownedEntity implements WaveEntity {
    private final WdActive game;
    private final MonsterModifier mod;
    private final MonsterClass monsterClass;

    public WaveDrownedEntity(World world, WdActive game, MonsterModifier mod, MonsterClass monsterClass) {
        super(EntityType.DROWNED, world);
        this.game = game;
        this.mod = mod;
        this.monsterClass = monsterClass;

        monsterClass.apply(this, mod);

        this.initializeGoals();
        this.setAttributes();
    }

    @Override
    protected void initGoals() {
    }

    protected void initializeGoals() {
        this.goalSelector.add(1, new DrownedEntity.DrownedAttackGoal(this, this.monsterClass.speed(), false));
        this.goalSelector.add(1, new DrownedEntity.TargetAboveWaterGoal(this, 1.0, 48));
        this.goalSelector.add(2, new MoveTowardGameCenterGoal<>(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::canDrownedAttackTarget));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean didAttack = super.tryAttack(target);

        if (didAttack) {
            if (target instanceof LivingEntity && mod.effect != null) {
                ((LivingEntity)target).addStatusEffect(mod.effect);
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
}
