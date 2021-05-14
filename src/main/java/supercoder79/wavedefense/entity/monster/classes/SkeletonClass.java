package supercoder79.wavedefense.entity.monster.classes;

import supercoder79.wavedefense.util.RandomCollection;

public interface SkeletonClass extends MonsterClass {
	default int attackInterval() {
		return 20;
	}

	default float range() {
		return 15.0f;
	}

	default float arrowSpeed() {
		return 1.6f;
	}

	default float arrowDivergence() {
		return 8.0f;
	}

	default double damageScale() {
		return 1.0;
	}

	static SkeletonClass nextSkeleton(int waveOrdinal) {
		RandomCollection<SkeletonClass> skeletonClasses = new RandomCollection<>();
		skeletonClasses
				.add(15, SkeletonClasses.DEFAULT)
				.add(Math.min(5, waveOrdinal / 1.5d - 5), SkeletonClasses.SNIPER)
				.add(Math.min(5, waveOrdinal / 2d - 5), SkeletonClasses.RAPIDSHOOTER);

		return skeletonClasses.next();
	}

	static SkeletonClass nextStray(int waveOrdinal) {
		RandomCollection<SkeletonClass> skeletonClasses = new RandomCollection<>();
		skeletonClasses
				.add(15, StrayClasses.DEFAULT)
				.add(Math.min(1, waveOrdinal / 20d - 10), StrayClasses.WIZARD);

		return skeletonClasses.next();
	}
}
