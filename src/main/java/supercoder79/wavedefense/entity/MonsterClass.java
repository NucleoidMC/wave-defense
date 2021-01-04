package supercoder79.wavedefense.entity;

import net.minecraft.entity.mob.ZombieEntity;

public interface MonsterClass {
	void apply(ZombieEntity entity, MonsterModifier mod);

	int ironCount();

	default double maxHealth() {
		return 20.0;
	}

	default double speed() {
		return 1.0;
	}
}
