package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.monster.classes.PhantomClass;
import supercoder79.wavedefense.game.WdActive;

public class WavePhantomEntity extends Phantom implements WaveEntity {
    private final WdActive game;
    private MonsterModifier mod;
    private PhantomClass phantomClass;

    public WavePhantomEntity(Level world, WdActive game, PhantomClass phantomClass) {
        super(EntityTypes.PHANTOM, world);

        this.game = game;
        this.setMonsterClass(phantomClass);

        this.setPhantomSize(phantomClass.size());

        this.setAttributes();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.game.guide.getCenterPos().distanceTo(this.position()) > 30)
            this.setHomeTo(BlockPos.containing(this.game.guide.getCenterPos()), 999);
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
