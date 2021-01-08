package supercoder79.wavedefense.entity;

import java.util.Random;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

public class MonsterClasses {
	public static final MonsterClass DEFAULT = new MonsterClass() {
		@Override
		public void apply(ZombieEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Zombie"));
		}

		@Override
		public int ironCount() {
			return 1;
		}
	};

	public static final MonsterClass DROWNED = new MonsterClass() {
		@Override
		public void apply(ZombieEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Drowned"));
		}

		@Override
		public int ironCount() {
			return 1;
		}
	};

	public static final MonsterClass KNIGHT = new MonsterClass() {
		@Override
		public void apply(ZombieEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Knight"));
			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
			entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
			entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
			entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
			entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
		}

		@Override
		public double maxHealth() {
			return 25.0;
		}

		@Override
		public double speed() {
			return 1.05;
		}

		@Override
		public int ironCount() {
			return 3;
		}
	};

	public static final MonsterClass TANK = new MonsterClass() {
		@Override
		public void apply(ZombieEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Tank"));
			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
			entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
			entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
			entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
			entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
		}

		@Override
		public double maxHealth() {
			return 40.0;
		}

		@Override
		public double speed() {
			return 0.75;
		}

		@Override
		public int ironCount() {
			return 8;
		}
	};

	public static final MonsterClass SCOUT = new MonsterClass() {
		@Override
		public void apply(ZombieEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Scout"));
			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
			entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
			entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
			entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
			entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
		}

		@Override
		public double maxHealth() {
			return 25.0;
		}

		@Override
		public double speed() {
			return 1.3;
		}

		@Override
		public int ironCount() {
			return 4;
		}
	};

	public static final MonsterClass RUNNER = new MonsterClass() {
		@Override
		public void apply(ZombieEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Runner"));
			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
		}

		@Override
		public double maxHealth() {
			return 15.0;
		}

		@Override
		public double speed() {
			return 1.4;
		}

		@Override
		public int ironCount() {
			return 2;
		}
	};

	public static final MonsterClass FIGHTER = new MonsterClass() {
		@Override
		public void apply(ZombieEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Fighter"));

			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
			if (random.nextBoolean()) {
				entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
			}
			if (random.nextBoolean()) {
				entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
			}
			if (random.nextBoolean()) {
				entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
			}
			if (random.nextBoolean()) {
				entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
			}
		}

		@Override
		public double maxHealth() {
			return 25.0;
		}

		@Override
		public double speed() {
			return 1.0;
		}

		@Override
		public int ironCount() {
			return 3;
		}
	};
}
