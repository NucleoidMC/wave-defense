package supercoder79.wavedefense.entity;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

public class ZombieClasses {
	public static final ZombieClass DEFAULT = new ZombieClass() {
		@Override
		public void apply(ZombieEntity entity, ZombieModifier mod) {
			entity.setCustomName(new LiteralText(mod.prefix + "Zombie"));
		}

		@Override
		public int ironCount() {
			return 1;
		}
	};

	public static final ZombieClass DROWNED = new ZombieClass() {
		@Override
		public void apply(ZombieEntity entity, ZombieModifier mod) {
			entity.setCustomName(new LiteralText(mod.prefix + "Drowned"));
		}

		@Override
		public int ironCount() {
			return 1;
		}
	};

	public static final ZombieClass KNIGHT = new ZombieClass() {
		@Override
		public void apply(ZombieEntity entity, ZombieModifier mod) {
			entity.setCustomName(new LiteralText(mod.prefix + "Knight"));
			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
			entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
			entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
			entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
			entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
		}

		@Override
		public int ironCount() {
			return 3;
		}
	};
}
