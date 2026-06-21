package supercoder79.wavedefense.entity.goal;

import net.minecraft.world.entity.EntityTypes;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.monster.SummonedSilverfishEntity;
import supercoder79.wavedefense.entity.monster.waveentity.WaveSummonerEntity;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Silverfish;

public final class SummonGoal<T extends WaveSummonerEntity & WaveEntity> extends Goal {
    private final T entity;

    private int handSwingTimer = 0;

    public SummonGoal(T entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return entity.summonTimer == 0;
    }

    @Override
    public void start() {
        entity.summonTimer = 80;
        entity.handSwingTimer = 10;
        Silverfish silverfish = new SummonedSilverfishEntity(EntityTypes.SILVERFISH, entity.level());
        BlockPos pos = entity.blockPosition();
        RandomSource random = entity.getRandom();
        silverfish.snapTo(pos.offset(random.nextInt(5) - 2, 2, random.nextInt(5) - 2), 0, 0);
        silverfish.setPersistenceRequired();
        silverfish.setCustomName(Component.literal("Silverfish"));
        entity.setAggressive(true);
        entity.level().addFreshEntity(silverfish);
    }


    @Override
    public boolean canContinueToUse() {
        return false;
    }
}
