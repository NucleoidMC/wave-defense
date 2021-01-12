package supercoder79.wavedefense.entity.monster.classes;

import java.util.Random;

import supercoder79.wavedefense.entity.MonsterModifier;

import net.minecraft.entity.mob.MobEntity;

public interface MonsterClass {
	void apply(MobEntity entity, MonsterModifier mod, Random random);

	int ironCount();

	default double maxHealth() {
		return 20.0;
	}

	default double speed() {
		return 1.0;
	}
}
