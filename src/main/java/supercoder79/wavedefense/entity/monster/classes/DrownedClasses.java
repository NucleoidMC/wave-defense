package supercoder79.wavedefense.entity.monster.classes;

import java.util.Random;

import supercoder79.wavedefense.entity.MonsterModifier;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.LiteralText;

public final class DrownedClasses {
	public static final MonsterClass DROWNED = new MonsterClass() {
		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Drowned"));
		}

		@Override
		public int ironCount() {
			return 1;
		}
	};
}
