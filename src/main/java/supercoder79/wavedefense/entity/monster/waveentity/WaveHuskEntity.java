package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public final class WaveHuskEntity extends Husk implements WaveEntity {
    private final WdActive game;
    private MonsterModifier mod;
    private MonsterClass monsterClass;

    public WaveHuskEntity(Level world, WdActive game, MonsterClass monsterClass) {
        super(EntityType.HUSK, world);
        this.game = game;
        this.setMonsterClass(monsterClass);

        this.initializeGoals();
        this.setAttributes();
    }

    @Override
    protected void registerGoals() {
    }

    protected void initializeGoals() {
        this.goalSelector.addGoal(1, new ZombieAttackGoal(this, this.getMonsterClass().speed(), false));
        this.goalSelector.addGoal(2, new MoveTowardGameCenterGoal<>(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
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
