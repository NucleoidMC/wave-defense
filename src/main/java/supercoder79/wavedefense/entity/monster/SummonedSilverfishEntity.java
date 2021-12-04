package supercoder79.wavedefense.entity.monster;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.world.World;

public class SummonedSilverfishEntity extends SilverfishEntity {
    private int lifeTicks = 400;

    public SummonedSilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.lifeTicks <= 0) {
            this.lifeTicks = 40;
            this.damage(DamageSource.STARVE, 1.0F);
        }
    }
}
