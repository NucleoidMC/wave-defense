package supercoder79.wavedefense.entity.monster;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SpiderEntity;
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
            this.damage(DamageSource.STARVE, 100);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (this.hasPassengers())
            return true;
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }
}
