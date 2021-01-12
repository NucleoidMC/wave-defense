package supercoder79.wavedefense.entity.monster.classes;

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
}
