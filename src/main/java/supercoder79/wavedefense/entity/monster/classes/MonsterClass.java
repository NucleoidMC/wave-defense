package supercoder79.wavedefense.entity.monster.classes;

import java.util.Random;

import supercoder79.wavedefense.entity.MonsterModifier;

import net.minecraft.entity.mob.MobEntity;
import supercoder79.wavedefense.util.RandomCollection;

public interface MonsterClass {
	void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal);

	int ironCount();

	int goldCount();

	int monsterPoints();

	default double maxHealth() {
		return 20.0;
	}

	default double speed() {
		return 1.0;
	}

	String name();

	static MonsterClass nextZombie(int waveOrdinal) {
		RandomCollection<MonsterClass> monsterClasses = new RandomCollection<>();
		monsterClasses
				.add(25, MonsterClasses.DEFAULT)
				.add(Math.min(10, (waveOrdinal - 1) / 2d), MonsterClasses.RUNNER)
				.add(Math.min(10, (waveOrdinal - 2) / 2d), MonsterClasses.FIGHTER)
				.add(Math.min(7, (waveOrdinal - 5) / 3d), MonsterClasses.KNIGHT)
				.add(Math.min(4, (waveOrdinal - 7) / 4d), MonsterClasses.TANK)
				.add(Math.min(4, (waveOrdinal - 10) / 4d), MonsterClasses.SCOUT);

		return monsterClasses.next();
	}

	static MonsterClass nextDrowned(int waveOrdinal) {
		RandomCollection<MonsterClass> monsterClasses = new RandomCollection<>();
		monsterClasses
				.add(10, DrownedClasses.DEFAULT)
				.add(Math.min(2, waveOrdinal / 5d - 10), DrownedClasses.TRIDENT);

		return monsterClasses.next();
	}

	static MonsterClass nextHusk(int waveOrdinal) {
		RandomCollection<MonsterClass> monsterClasses = new RandomCollection<>();
		monsterClasses
				.add(10, HuskClasses.DEFAULT)
				.add(Math.min(2, waveOrdinal / 5d - 10), HuskClasses.MUMMY);

		return monsterClasses.next();
	}
}
