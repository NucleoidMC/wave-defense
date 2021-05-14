package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.goal.SummonGoal;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public class WaveSummonerEntity extends SkeletonEntity implements WaveEntity {
    private final WdActive game;
    private MonsterModifier mod;
    private MonsterClass monsterClass;

    public int summonTimer;
    public int handSwingTimer;

    public WaveSummonerEntity(World world, WdActive game, MonsterClass monsterClass) {
        super(EntityType.SKELETON, world);
        this.game = game;
        this.setMonsterClass(monsterClass);

        summonTimer = 0;
        handSwingTimer = 0;

        this.initializeGoals();
        this.setAttributes();
    }

    @Override
    public void tick() {
        super.tick();
        if (summonTimer > 0)
            summonTimer--;

        if (handSwingTimer > 0)
            handSwingTimer--;
        else
            this.setAttacking(false);
    }

    @Override
    protected void initGoals() {
    }

    protected void initializeGoals() {
        // TODO: custom attack goal
        this.goalSelector.add(1, new SummonGoal<>(this));
        this.goalSelector.add(2, new MoveTowardGameCenterGoal<>(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(4, new AttackGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, WaveSummonerEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
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
    public MonsterClass getMonsterClass() {
        return monsterClass;
    }

    public void setMonsterClass(MonsterClass monsterClass) {
        this.monsterClass = monsterClass;
    }

    public MonsterModifier getMod() {
        return mod;
    }

    @Override
    public void setMod(MonsterModifier mod) {
        this.mod = mod;
    }
}
