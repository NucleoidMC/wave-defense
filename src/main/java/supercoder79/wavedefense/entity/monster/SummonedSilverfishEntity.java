package supercoder79.wavedefense.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;

public class SummonedSilverfishEntity extends Silverfish {
    private int lifeTicks = 400;

    public SummonedSilverfishEntity(EntityType<? extends Silverfish> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.lifeTicks <= 0) {
            this.lifeTicks = 40;
            this.hurtServer((ServerLevel) this.level(), this.damageSources().starve(), 1.0F);
        }
    }
}
