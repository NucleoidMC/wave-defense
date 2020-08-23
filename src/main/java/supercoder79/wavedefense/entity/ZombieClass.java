package supercoder79.wavedefense.entity;

import net.minecraft.entity.mob.ZombieEntity;

public interface ZombieClass {
	void apply(ZombieEntity entity, ZombieModifier mod);

	int ironCount();
}
