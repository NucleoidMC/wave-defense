package supercoder79.wavedefense.entity.projectile;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class WizardsSnowballEntity extends SnowballEntity {
    public WizardsSnowballEntity(World world, LivingEntity owner) {
        super(world, owner);
    }

    @Override
    public void tick() {
        super.tick();

        ((ServerWorld)world).spawnParticles(
                new DustParticleEffect(0.4f, 0.5f, 1.0f, 1f),
                this.getX(), this.getY(), this.getZ(),
                1, 0.0, 0.0, 0.0, 0.1
        );
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        world.createExplosion(this, DamageSource.MAGIC, new ExplosionBehavior(), hitResult.getPos().getX(), hitResult.getPos().getY() + 0.4f, hitResult.getPos().getZ(), 0.32f, false, Explosion.DestructionType.NONE);
        AreaEffectCloudEntity slownessAOE = new AreaEffectCloudEntity(world, hitResult.getPos().getX(), hitResult.getPos().getY() + 0.3, hitResult.getPos().getZ());
        slownessAOE.setRadius(1.3f);
        slownessAOE.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 0));
        slownessAOE.setDuration(80);
        world.spawnEntity(slownessAOE);
        super.onCollision(hitResult);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        player.takeKnockback(1, this.getVelocity().getX(),  this.getVelocity().getZ());
    }
}
