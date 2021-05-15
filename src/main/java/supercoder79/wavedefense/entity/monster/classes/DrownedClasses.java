package supercoder79.wavedefense.entity.monster.classes;

import java.util.Random;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import supercoder79.wavedefense.entity.MonsterModifier;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.LiteralText;

public final class DrownedClasses {
	public static final MonsterClass DEFAULT = new MonsterClass() {
		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {

		}

		@Override
		public int ironCount() {
			return 1;
		}

		@Override
		public int goldCount() {
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
		public int ironCount() {
			return 8;
		}

		@Override
		public int goldCount() {
			return new Random().nextInt(5) == 0 ? 1 : 0;
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
