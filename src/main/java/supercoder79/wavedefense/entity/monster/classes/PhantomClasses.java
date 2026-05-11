package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import supercoder79.wavedefense.entity.MonsterModifier;

public class PhantomClasses {
    public static final PhantomClass DEFAULT = new PhantomClass() {
        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {

        }

        @Override
        public int ironCount(RandomSource random) {
            return 2;
        }

        @Override
        public int goldCount(RandomSource random) {
            return 0;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int monsterPoints() {
            return 3;
        }

        @Override
        public String name() {
            return "Phantom";
        }
    };

    public static final PhantomClass LARGE = new PhantomClass() {
        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {

        }

        @Override
        public int ironCount(RandomSource random) {
            return 4;
        }

        @Override
        public int goldCount(RandomSource random) {
            return random.nextInt(6) == 0 ? 1 : 0;
        }

        @Override
        public int size() {
            return 8;
        }

        @Override
        public int monsterPoints() {
            return 5;
        }

        @Override
        public String name() {
            return "Nightmare";
        }
    };
}
