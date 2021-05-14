package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.LiteralText;
import supercoder79.wavedefense.entity.MonsterModifier;

import java.util.Random;

public class CaveSpiderClasses {
    public static final MonsterClass DEFAULT = new MonsterClass() {
        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {

        }

        @Override
        public int ironCount() {
            return 1 + new Random().nextInt(2);
        }

        @Override
        public int goldCount() {
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
