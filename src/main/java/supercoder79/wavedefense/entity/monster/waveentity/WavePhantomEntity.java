package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.monster.classes.PhantomClass;
import supercoder79.wavedefense.game.WdActive;

public class WavePhantomEntity extends PhantomEntity implements WaveEntity {
    private final WdActive game;
    private MonsterModifier mod;
    private PhantomClass phantomClass;

    public WavePhantomEntity(World world, WdActive game, PhantomClass phantomClass) {
        super(EntityType.PHANTOM, world);

        this.game = game;
        this.setMonsterClass(phantomClass);

        this.setPhantomSize(phantomClass.size());

        this.setAttributes();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.game.guide.getCenterPos().distanceTo(this.getPos()) > 30)
            this.setPositionTarget(BlockPos.ofFloored(this.game.guide.getCenterPos()), 999);
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
    public WdActive getGame() {
        return game;
    }

    public PhantomClass getMonsterClass() {
        return phantomClass;
    }

    public void setMonsterClass(PhantomClass phantomClass) {
        this.phantomClass = phantomClass;
    }

    public MonsterModifier getMod() {
        return mod;
    }

    @Override
    public void setMod(MonsterModifier mod) {
        this.mod = mod;
    }
}
