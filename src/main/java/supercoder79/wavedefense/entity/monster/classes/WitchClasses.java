package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.entity.mob.MobEntity;
import supercoder79.wavedefense.entity.MonsterModifier;

import java.util.Random;

public class WitchClasses {
    public static final MonsterClass DEFAULT = new MonsterClass() {
        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {

        }

        @Override
        public int ironCount() {
            return 6;
        }

        @Override
        public int goldCount() {
            return new Random().nextInt(6) == 0 ? 1 : 0;
        }

        @Override
        public int monsterPoints() {
            return 10;
        }

        @Override
        public String name() {
            return "Witch";
        }

        @Override
        public double maxHealth() {
            return 36;
        }
    };
}
