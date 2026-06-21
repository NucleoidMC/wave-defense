package supercoder79.wavedefense.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.entity.monster.classes.PhantomClasses;
import supercoder79.wavedefense.game.WdActive;

public class WizardsPhantomEntity extends Phantom implements WaveEntity {
    private final WdActive game;

    public WizardsPhantomEntity(Level world, WdActive game) {
        super(EntityTypes.PHANTOM, world);
        this.xpReward = 0;
        this.game = game;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30);
        this.setHealth((float) this.getMonsterClass().maxHealth());
    }

    private int growingTimer = 8;
    private int glowingTimer = 16;

    @Override
    public void tick() {
        super.tick();
        if (!this.isVehicle() && this.getPhantomSize() < 12) {
            growingTimer--;
            glowingTimer--;

            if (growingTimer <= 0) {
                this.push(0, 0.5d, 0);
                this.setPhantomSize(this.getPhantomSize() + 1);
                growingTimer = 8;
            }

            if (glowingTimer <= 0) {
                this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 8, 0));
                glowingTimer = 16;
            }
        }

        if (this.game.guide.getCenterPos().distanceTo(this.position()) > 30)
            this.setHomeTo(BlockPos.containing(this.game.guide.getCenterPos()), 999);

        this.setCustomName(Component.empty());
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel world, DamageSource damageSource) {
        if (this.isVehicle())
            return true;
        return super.isInvulnerableTo(world, damageSource);
    }

    @Override
    protected boolean shouldDropLoot(ServerLevel level) {
        return false;
    }

    @Override
    public int ironCount(RandomSource random) {
        return 20;
    }

    @Override
    public int goldCount(RandomSource random) {
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
        return !this.isVehicle();
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
