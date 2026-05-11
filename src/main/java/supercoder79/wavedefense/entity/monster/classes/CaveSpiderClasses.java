package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import supercoder79.wavedefense.entity.MonsterModifier;

public class CaveSpiderClasses {
    public static final MonsterClass DEFAULT = new MonsterClass() {
        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {

        }

        @Override
        public int ironCount(RandomSource random) {
            return 1 + random.nextInt(2);
        }

        @Override
        public int goldCount(RandomSource random) {
            return 0;
        }

        @Override
        public int monsterPoints() {
            return 2;
        }

        @Override
        public String name() {
            return "Cave Spider";
        }

        @Override
        public double maxHealth() {
            return 7.5d;
        }
    };
}
