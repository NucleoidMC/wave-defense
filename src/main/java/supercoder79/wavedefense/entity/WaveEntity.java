package supercoder79.wavedefense.entity;
import supercoder79.wavedefense.entity.config.EnemyConfig;
import supercoder79.wavedefense.game.WdActive;
import net.minecraft.entity.mob.MobEntity;

public interface WaveEntity {
    EnemyConfig getEnemyConfig();

    WdActive getGame();

    default MobEntity asMob() {
        return (MobEntity) this;
    }
}
