package supercoder79.wavedefense.entity.monster;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.entity.monster.classes.PhantomClasses;
import supercoder79.wavedefense.entity.monster.classes.StrayClasses;
import supercoder79.wavedefense.game.WdActive;

public class WizardsPhantomEntity extends PhantomEntity implements WaveEntity {
    private final WdActive game;

    public WizardsPhantomEntity(World world, WdActive game) {
        super(EntityType.PHANTOM, world);
        this.experiencePoints = 0;

        this.game = game;

        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(30);
        this.setHealth((float) this.getMonsterClass().maxHealth());
    }

    private int growingTimer = 8;
    private int glowingTimer = 16;

    @Override
    public void tick() {
        super.tick();
        if (!this.hasPassengers() && this.getPhantomSize() < 12) {
            growingTimer--;
            glowingTimer--;

            if (growingTimer <= 0) {
                this.addVelocity(0, 0.5d, 0);
                this.setPhantomSize(this.getPhantomSize() + 1);
                growingTimer = 8;
            }

            if (glowingTimer <= 0) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 8, 0));
                glowingTimer = 16;
            }
        }

        if (this.game.guide.getCenterPos().distanceTo(this.getPos()) > 30)
            this.setPositionTarget(new BlockPos(this.game.guide.getCenterPos()), 999);

        this.setCustomName(new LiteralText(""));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (this.hasPassengers())
            return true;
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    protected boolean canDropLootAndXp() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean canBeControlledByRider() {
        return true;
    }

    @Override
    public int ironCount() {
        return 20;
    }

    @Override
    public int goldCount() {
        return 1;
    }

    @Override
    public int monsterScore() {
        return 20;
    }

    @Override
    public MonsterClass getMonsterClass() {
        return PhantomClasses.LARGE;
    }

    @Override
    public void setMod(MonsterModifier monsterModifier) {

    }

    @Override
    public boolean showHealth() {
        return !this.hasPassengers();
    }

    @Override
    public MonsterModifier getMod() {
        return MonsterModifier.NORMAL;
    }

    @Override
    public WdActive getGame() {
        return game;
    }
}
