package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.goal.SummonGoal;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public class WaveSummonerEntity extends Skeleton implements WaveEntity {
    private final WdActive game;
    private MonsterModifier mod;
    private MonsterClass monsterClass;

    public int summonTimer;
    public int handSwingTimer;

    public WaveSummonerEntity(Level world, WdActive game, MonsterClass monsterClass) {
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
            this.setAggressive(false);
    }

    @Override
    protected void registerGoals() {
    }

    protected void initializeGoals() {
        // TODO: custom attack goal
        this.goalSelector.addGoal(1, new SummonGoal<>(this));
        this.goalSelector.addGoal(2, new MoveTowardGameCenterGoal<>(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new OcelotAttackGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, WaveSummonerEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        boolean didAttack = super.doHurtTarget(world, target);

        if (didAttack) {
            if (target instanceof LivingEntity && getMod().effect != null) {
                ((LivingEntity)target).addEffect(getMod().effect.get());
            }
        }

        return didAttack;
    }

    public void setAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMonsterClass().maxHealth());
        this.setHealth((float) this.getMonsterClass().maxHealth());
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(64d);
    }

    @Override
    public int ironCount(RandomSource random) {
        return this.getMonsterClass().ironCount(random) + this.getMod().ironBonus;
    }

    @Override
    public int goldCount(RandomSource random) {
        return this.getMonsterClass().goldCount(random);
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
