package supercoder79.wavedefense.entity.monster.classes;

import supercoder79.wavedefense.entity.EquipmentHelper;
import supercoder79.wavedefense.entity.MonsterModifier;

import java.util.ArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public final class StrayClasses {
    public static final SkeletonClass DEFAULT = new SkeletonClass() {
        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {
            entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }

        @Override
        public int ironCount(RandomSource random) {
            return 2;
        }

        @Override
        public int goldCount(RandomSource random) {
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
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {
            ItemStack blazeRod = new ItemStack(Items.BLAZE_ROD);
            var reg = entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            blazeRod.enchant(reg.getOrThrow(Enchantments.FIRE_ASPECT), 2);
            blazeRod.enchant(reg.getOrThrow(Enchantments.SHARPNESS), 10);
            entity.setItemSlot(EquipmentSlot.MAINHAND, blazeRod);
        }

        @Override
        public int ironCount(RandomSource random) {
            return 16;
        }

        @Override
        public int goldCount(RandomSource random) {
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
