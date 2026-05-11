package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import supercoder79.wavedefense.entity.MonsterModifier;

public class WitchClasses {
    public static final MonsterClass DEFAULT = new MonsterClass() {
        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {

        }

        @Override
        public int ironCount(RandomSource random) {
            return 6;
        }

        @Override
        public int goldCount(RandomSource random) {
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
