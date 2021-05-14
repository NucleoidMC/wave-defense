package supercoder79.wavedefense.entity.monster.classes;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import supercoder79.wavedefense.entity.EquipmentHelper;
import supercoder79.wavedefense.entity.MonsterModifier;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.LiteralText;

public final class SkeletonClasses {
    public static final SkeletonClass DEFAULT = new SkeletonClass() {
        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }

        @Override
        public int ironCount() {
            return 2;
        }

        @Override
        public int goldCount() {
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
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

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
        public int ironCount() {
            return iron;
        }

        @Override
        public int goldCount() {
            return new Random().nextInt(15) == 0 ? 1 : 0;
        }

        @Override
        public int monsterPoints() {
            return 5;
        }
    };

    public static final SkeletonClass SNIPER = new SkeletonClass() {
        private int iron;

        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

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
        public int ironCount() {
            return iron;
        }

        @Override
        public int goldCount() {
            return new Random().nextInt(8) == 0 ? 1 : 0;
        }

        @Override
        public int monsterPoints() {
            return 5;
        }
    };


    public static final SkeletonClass SUMMONER = new SkeletonClass() {
        private int iron;

        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
            iron = 12;

            ItemStack sword = new ItemStack(Items.GOLDEN_SWORD);
            sword.addEnchantment(Enchantments.SHARPNESS, 6);
            sword.addEnchantment(Enchantments.KNOCKBACK, 3);
            entity.equipStack(EquipmentSlot.MAINHAND, sword);

            entity.equipStack(EquipmentSlot.HEAD, EnchantmentHelper.enchant(random, new ItemStack(Items.GOLDEN_HELMET), 40, true));

            ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
            ArrayList<DyeItem> dyeItems = new ArrayList<>();
            dyeItems.add((DyeItem) Items.RED_DYE);
            dyeItems.add((DyeItem) Items.RED_DYE);
            dyeItems.add((DyeItem) Items.BLACK_DYE);
            chestplate = DyeableItem.blendAndSetColor(chestplate, dyeItems);

            entity.equipStack(EquipmentSlot.CHEST, chestplate);

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
        public int ironCount() {
            return iron;
        }

        @Override
        public int goldCount() {
            return 1 + new Random().nextInt(2);
        }

        @Override
        public int monsterPoints() {
            return 30;
        }
    };
}
