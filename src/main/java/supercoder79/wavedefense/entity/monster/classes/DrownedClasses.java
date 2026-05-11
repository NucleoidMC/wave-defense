package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import supercoder79.wavedefense.entity.MonsterModifier;

public final class DrownedClasses {
	public static final MonsterClass DEFAULT = new MonsterClass() {
		@Override
		public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {

		}

		@Override
		public int ironCount(RandomSource random) {
			return 1;
		}

		@Override
		public int goldCount(RandomSource random) {
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
		public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
		}

		@Override
		public int ironCount(RandomSource random) {
			return 8;
		}

		@Override
		public int goldCount(RandomSource random) {
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
