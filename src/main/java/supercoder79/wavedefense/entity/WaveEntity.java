package supercoder79.wavedefense.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public interface WaveEntity {
    int ironCount(RandomSource random);
    int goldCount(RandomSource random);
    int monsterScore();

    MonsterClass getMonsterClass();

    void setMod(MonsterModifier monsterModifier);

    MonsterModifier getMod();
    
    Vec3 pos = Vec3.ZERO;

    WdActive getGame();

    default boolean showHealth() {
        return false;
    }
}
