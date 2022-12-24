package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.math.random.Random;
import supercoder79.wavedefense.entity.MonsterModifier;

public class PhantomClasses {
    public static final PhantomClass DEFAULT = new PhantomClass() {
        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {

        }

        @Override
        public int ironCount(Random random) {
            return 2;
        }

        @Override
        public int goldCount(Random random) {
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
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {

        }

        @Override
        public int ironCount(Random random) {
            return 4;
        }

        @Override
        public int goldCount(Random random) {
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
