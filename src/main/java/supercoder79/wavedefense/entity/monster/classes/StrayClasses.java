package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import supercoder79.wavedefense.entity.EquipmentHelper;
import supercoder79.wavedefense.entity.MonsterModifier;

import java.util.ArrayList;

public final class StrayClasses {
    public static final SkeletonClass DEFAULT = new SkeletonClass() {
        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }

        @Override
        public int ironCount(Random random) {
            return 2;
        }

        @Override
        public int goldCount(Random random) {
            return 0;
        }

        @Override
        public int monsterPoints() {
            return 3;
        }

        @Override
        public String name() {
            return "Stray";
        }
    };

    public static final SkeletonClass WIZARD = new SkeletonClass() {
        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
            ItemStack blazeRod = new ItemStack(Items.BLAZE_ROD);
            blazeRod.addEnchantment(Enchantments.FIRE_ASPECT, 2);
            blazeRod.addEnchantment(Enchantments.SHARPNESS, 10);
            entity.equipStack(EquipmentSlot.MAINHAND, blazeRod);
        }

        @Override
        public int ironCount(Random random) {
            return 16;
        }

        @Override
        public int goldCount(Random random) {
            return 1;
        }

        @Override
        public int monsterPoints() {
            return 20;
        }

        @Override
        public double maxHealth() {
            return 50;
        }

        @Override
        public String name() {
            return "Wizard";
        }
    };
}
