package supercoder79.wavedefense.entity.goal;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.monster.SummonedSilverfishEntity;
import supercoder79.wavedefense.entity.monster.waveentity.WaveSummonerEntity;

import java.util.EnumSet;

public final class SummonGoal<T extends WaveSummonerEntity & WaveEntity> extends Goal {
    private final T entity;

    private int handSwingTimer = 0;

    public SummonGoal(T entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return entity.summonTimer == 0;
    }

    @Override
    public void start() {
        entity.summonTimer = 80;
        entity.handSwingTimer = 10;
        SilverfishEntity silverfish = new SummonedSilverfishEntity(EntityType.SILVERFISH, entity.getEntityWorld());
        BlockPos pos = entity.getBlockPos();
        Random random = entity.getRandom();
        silverfish.refreshPositionAndAngles(pos.add(random.nextInt(5) - 2, 2, random.nextInt(5) - 2), 0, 0);
        silverfish.setPersistent();
        silverfish.setCustomName(Text.literal("Silverfish"));
        entity.setAttacking(true);
        entity.getEntityWorld().spawnEntity(silverfish);
    }


    @Override
    public boolean shouldContinue() {
        return false;
    }
}
