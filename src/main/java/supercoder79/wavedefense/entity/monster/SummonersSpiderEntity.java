package supercoder79.wavedefense.entity.monster;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class SummonersSpiderEntity extends SpiderEntity {
    public SummonersSpiderEntity(EntityType<? extends SpiderEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hasPassengers())
            this.damage((ServerWorld) this.getWorld(),this.getDamageSources().starve(), 100);
    }

    @Override
    public boolean isInvulnerableTo(ServerWorld world, DamageSource damageSource) {
        if (this.hasPassengers())
            return true;
        return super.isInvulnerableTo(world, damageSource);
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }
}
