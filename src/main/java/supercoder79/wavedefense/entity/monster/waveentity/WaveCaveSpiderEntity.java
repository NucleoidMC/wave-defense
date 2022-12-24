package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.monster.classes.CaveSpiderClasses;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public class WaveCaveSpiderEntity extends CaveSpiderEntity implements WaveEntity {
    private final WdActive game;
    private MonsterModifier mod;

    public WaveCaveSpiderEntity(World world, WdActive game, MonsterClass monsterClass) {
        super(EntityType.CAVE_SPIDER, world);

        this.game = game;
        this.setMod(mod);

        this.goalSelector.add(2, new MoveTowardGameCenterGoal<>(this));

        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(8);
        this.setHealth((float) this.getMonsterClass().maxHealth());
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

    @Override
    public int ironCount(Random random) {
        return this.getMonsterClass().ironCount(random) + this.getMod().ironBonus;
    }

    @Override
    public int goldCount(Random random) {
        return this.getMonsterClass().goldCount(random);
    }

    @Override
    public int monsterScore() {
        return this.getMonsterClass().monsterPoints();
    }

    @Override
    public MonsterClass getMonsterClass() {
        return CaveSpiderClasses.DEFAULT;
    }

    @Override
    public WdActive getGame() {
        return game;
    }

    public MonsterModifier getMod() {
        return mod;
    }

    @Override
    public void setMod(MonsterModifier mod) {
        this.mod = mod;
    }
}
