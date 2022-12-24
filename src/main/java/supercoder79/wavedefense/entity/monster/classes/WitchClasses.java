package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.random.Random;
import supercoder79.wavedefense.entity.MonsterModifier;

public class WitchClasses {
    public static final MonsterClass DEFAULT = new MonsterClass() {
        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {

        }

        @Override
        public int ironCount(Random random) {
            return 6;
        }

        @Override
        public int goldCount(Random random) {
            return random.nextInt(6) == 0 ? 1 : 0;
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
