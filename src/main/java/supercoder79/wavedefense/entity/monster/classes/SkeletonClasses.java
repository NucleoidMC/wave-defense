package supercoder79.wavedefense.entity.monster.classes;

import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import supercoder79.wavedefense.entity.EquipmentHelper;
import supercoder79.wavedefense.entity.MonsterModifier;

public final class SkeletonClasses {
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
            return "Skeleton";
        }
    };

    public static final SkeletonClass RAPIDSHOOTER = new SkeletonClass() {
        private int iron;

        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {
            entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

            iron = 3;

            if (random.nextBoolean())
                iron = EquipmentHelper.equipLeggings(waveOrdinal, random, iron, entity, -1);
            if (random.nextBoolean())
                iron = EquipmentHelper.equipBoots(waveOrdinal, random, iron, entity, -1);

        }

        @Override
        public double speed() {
            return 1.25;
        }

        @Override
        public String name() {
            return "Rapid Shooter";
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
        public int ironCount(RandomSource random) {
            return iron;
        }

        @Override
        public int goldCount(RandomSource random) {
            return random.nextInt(15) == 0 ? 1 : 0;
        }

        @Override
        public int monsterPoints() {
            return 5;
        }
    };

    public static final SkeletonClass SNIPER = new SkeletonClass() {
        private int iron;

        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {
            entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

            iron = 4;

            if (random.nextBoolean())
                iron = EquipmentHelper.equipHelmet(waveOrdinal, random, iron, entity, -2);
            if (random.nextBoolean())
                iron = EquipmentHelper.equipChestplate(waveOrdinal, random, iron, entity, -2);
            if (random.nextBoolean())
                iron = EquipmentHelper.equipLeggings(waveOrdinal, random, iron, entity, -2);
            if (random.nextBoolean())
                iron = EquipmentHelper.equipBoots(waveOrdinal, random, iron, entity, -2);
        }

        @Override
        public double speed() {
            return 0.95;
        }

        @Override
        public String name() {
            return "Sniper";
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
        public int ironCount(RandomSource random) {
            return iron;
        }

        @Override
        public int goldCount(RandomSource random) {
            return random.nextInt(8) == 0 ? 1 : 0;
        }

        @Override
        public int monsterPoints() {
            return 5;
        }
    };


    public static final SkeletonClass SUMMONER = new SkeletonClass() {
        private int iron;

        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {
            iron = 12;

            ItemStack sword = new ItemStack(Items.GOLDEN_SWORD);
            var reg = entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            sword.enchant(reg.getOrThrow(Enchantments.SHARPNESS), 6);
            sword.enchant(reg.getOrThrow(Enchantments.KNOCKBACK), 3);
            entity.setItemSlot(EquipmentSlot.MAINHAND, sword);

            entity.setItemSlot(EquipmentSlot.HEAD, EnchantmentHelper.enchantItem(random, new ItemStack(Items.GOLDEN_HELMET), 40, entity.registryAccess(), Optional.empty()));

            ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
            ArrayList<DyeColor> dyeItems = new ArrayList<>();
            dyeItems.add(DyeColor.RED);
            dyeItems.add(DyeColor.RED);
            dyeItems.add(DyeColor.BLACK);
            chestplate = DyedItemColor.applyDyes(chestplate, dyeItems);

            entity.setItemSlot(EquipmentSlot.CHEST, chestplate);

            EquipmentHelper.equipLeggings(waveOrdinal, random, iron, entity, 10);
        }

        @Override
        public double speed() {
            return 0.7;
        }

        @Override
        public String name() {
            return "King";
        }

        @Override
        public int attackInterval() {
            return 0;
        }

        @Override
        public float arrowDivergence() {
            return 0.0f;
        }

        @Override
        public double damageScale() {
            return 0.0;
        }

        @Override
        public float range() {
            return 0.0f;
        }

        @Override
        public double maxHealth() {
            return 50.0;
        }

        @Override
        public float arrowSpeed() {
            return 0.0f;
        }

        @Override
        public int ironCount(RandomSource random) {
            return iron;
        }

        @Override
        public int goldCount(RandomSource random) {
            return random.nextInt(2) + random.nextInt(2);
        }

        @Override
        public int monsterPoints() {
            return 30;
        }
    };
}
