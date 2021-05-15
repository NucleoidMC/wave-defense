package supercoder79.wavedefense.entity;

import net.minecraft.util.math.Vec3d;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public interface WaveEntity {
    int ironCount();
    int goldCount();
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
