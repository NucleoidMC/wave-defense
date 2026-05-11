package supercoder79.wavedefense.entity.projectile;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

public class WizardsSnowballEntity extends Snowball {
    public WizardsSnowballEntity(Level world, LivingEntity owner) {
        super(world, owner, new ItemStack(Items.SNOWBALL));
    }

    @Override
    public void tick() {
        super.tick();

        ((ServerLevel)this.level()).sendParticles(
                new DustParticleOptions(ARGB.colorFromFloat(1,0.4f, 0.5f, 1.0f), 1f),
                this.getX(), this.getY(), this.getZ(),
                1, 0.0, 0.0, 0.0, 0.1
        );
    }

    @Override
    protected void onHit(HitResult hitResult) {
        this.level().explode(this, this.damageSources().magic(), new ExplosionDamageCalculator(), hitResult.getLocation().x(), hitResult.getLocation().y() + 0.4f, hitResult.getLocation().z(), 0.32f, false, Level.ExplosionInteraction.NONE);
        AreaEffectCloud slownessAOE = new AreaEffectCloud(this.level(), hitResult.getLocation().x(), hitResult.getLocation().y() + 0.3, hitResult.getLocation().z());
        slownessAOE.setRadius(1.3f);
        slownessAOE.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 0));
        slownessAOE.setDuration(80);
        this.level().addFreshEntity(slownessAOE);
        super.onHit(hitResult);
    }

    @Override
    public void playerTouch(Player player) {
        player.knockback(1, this.getDeltaMovement().x(),  this.getDeltaMovement().z());
    }
}
