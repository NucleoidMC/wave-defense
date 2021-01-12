package supercoder79.wavedefense.entity.monster.classes;

import java.util.Random;

import supercoder79.wavedefense.entity.MonsterModifier;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

public final class SkeletonClasses {
	public static final SkeletonClass SKELETON = new SkeletonClass() {
		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Skeleton"));
			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
		}

		@Override
		public int ironCount() {
			return 2;
		}
	};

	public static final SkeletonClass RAPIDSHOOTER = new SkeletonClass() {
		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Rapid Shooter"));
			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

			if (random.nextBoolean()) {
				entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
			}

			if (random.nextBoolean()) {
				entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
			}
		}

		@Override
		public double speed() {
			return 1.25;
		}

		@Override
		public int attackInterval() {
			return 8;
		}

		@Override
		public float arrowDivergence() {
			return 11.5f;
		}

		@Override
		public double damageScale() {
			return 0.75;
		}

		@Override
		public double maxHealth() {
			return 15.0;
		}

		@Override
		public int ironCount() {
			return 4;
		}
	};

	public static final SkeletonClass SNIPER = new SkeletonClass() {
		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random) {
			entity.setCustomName(new LiteralText(mod.prefix + "Sniper"));
			entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

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
		public double speed() {
			return 0.95;
		}

		@Override
		public int attackInterval() {
			return 50;
		}

		@Override
		public float arrowDivergence() {
			return 1.5f;
		}

		@Override
		public double damageScale() {
			return 1.75;
		}

		@Override
		public float range() {
			return 35.0f;
		}

		@Override
		public double maxHealth() {
			return 25.0;
		}

		@Override
		public float arrowSpeed() {
			return 2.2f;
		}

		@Override
		public int ironCount() {
			return 6;
		}
	};
}
