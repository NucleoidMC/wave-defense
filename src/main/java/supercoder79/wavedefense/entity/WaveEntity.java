package supercoder79.wavedefense.entity;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public interface WaveEntity {
    int ironCount(Random random);
    int goldCount(Random random);
    int monsterScore();

    MonsterClass getMonsterClass();

    void setMod(MonsterModifier monsterModifier);

    MonsterModifier getMod();
    
    Vec3d pos = Vec3d.ZERO;

    WdActive getGame();

    default boolean showHealth() {
        return false;
    }
}
