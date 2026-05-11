package supercoder79.wavedefense.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.level.Level;

public class SummonersSpiderEntity extends Spider {
    public SummonersSpiderEntity(EntityType<? extends Spider> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isVehicle())
            this.hurtServer((ServerLevel) this.level(),this.damageSources().starve(), 100);
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
}
