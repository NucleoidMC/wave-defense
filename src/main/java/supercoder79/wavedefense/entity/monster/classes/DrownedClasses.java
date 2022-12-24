package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import supercoder79.wavedefense.entity.MonsterModifier;

import net.minecraft.entity.mob.MobEntity;

public final class DrownedClasses {
	public static final MonsterClass DEFAULT = new MonsterClass() {
		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {

		}

		@Override
		public int ironCount(Random random) {
			return 1;
		}

		@Override
		public int goldCount(Random random) {
			return 0;
		}

		@Override
		public int monsterPoints() {
			return 2;
		}

		@Override
		public String name() {
			return "Drowned";
		}
	};

	public static final MonsterClass TRIDENT = new MonsterClass() {
		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
		}

		@Override
		public int ironCount(Random random) {
			return 8;
		}

		@Override
		public int goldCount(Random random) {
			return random.nextInt(5) == 0 ? 1 : 0;
		}

		@Override
		public int monsterPoints() {
			return 6;
		}

		@Override
		public String name() {
			return "Drowned";
		}
	};
}
