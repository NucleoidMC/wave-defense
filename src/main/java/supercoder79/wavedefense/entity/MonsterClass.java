package supercoder79.wavedefense.entity;

import java.util.Random;

import net.minecraft.entity.mob.ZombieEntity;

public interface MonsterClass {
	void apply(ZombieEntity entity, MonsterModifier mod, Random random);

	int ironCount();

	default double maxHealth() {
		return 20.0;
	}

	default double speed() {
		return 1.0;
	}
}
