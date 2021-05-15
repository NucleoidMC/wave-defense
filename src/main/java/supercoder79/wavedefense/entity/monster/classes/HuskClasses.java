package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import supercoder79.wavedefense.entity.EquipmentHelper;
import supercoder79.wavedefense.entity.MonsterModifier;

import java.util.ArrayList;
import java.util.Random;

public final class HuskClasses {
    public static final MonsterClass DEFAULT = new MonsterClass() {
        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {

        }

        @Override
        public int ironCount() {
            return 1 + new Random().nextInt(2);
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
            return "Husk";
        }
    };

    public static final MonsterClass MUMMY = new MonsterClass() {
        @Override
        public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
            ItemStack enchantedSword = EquipmentHelper.enchant(Items.GOLDEN_SWORD, waveOrdinal, 30, random);
            if (random.nextBoolean())
                entity.equipStack(EquipmentSlot.MAINHAND, enchantedSword);

            ArrayList<DyeItem> dyeItems = new ArrayList<>();
            dyeItems.add((DyeItem) Items.WHITE_DYE);
            dyeItems.add((DyeItem) Items.WHITE_DYE);
            dyeItems.add((DyeItem) Items.WHITE_DYE);
            dyeItems.add((DyeItem) Items.YELLOW_DYE);

            ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
            if (random.nextBoolean())
                helmet = EquipmentHelper.enchant(helmet.getItem(), waveOrdinal, 10, random);
            helmet = DyeableItem.blendAndSetColor(helmet, dyeItems);

            ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
            if (random.nextBoolean())
                chestplate = EquipmentHelper.enchant(chestplate.getItem(), waveOrdinal, 10, random);
            chestplate = DyeableItem.blendAndSetColor(chestplate, dyeItems);

            ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
            if (random.nextBoolean())
                leggings = EquipmentHelper.enchant(leggings.getItem(), waveOrdinal, 10, random);
            leggings = DyeableItem.blendAndSetColor(leggings, dyeItems);

            ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
            if (random.nextBoolean())
                boots = EquipmentHelper.enchant(boots.getItem(), waveOrdinal, 10, random);
            boots = DyeableItem.blendAndSetColor(boots, dyeItems);

            entity.equipStack(EquipmentSlot.HEAD, helmet);
            entity.equipStack(EquipmentSlot.CHEST, chestplate);
            entity.equipStack(EquipmentSlot.LEGS, leggings);
            entity.equipStack(EquipmentSlot.FEET, boots);

        }

        @Override
        public int ironCount() {
            return 5;
        }

        @Override
        public int goldCount() {
            return new Random().nextInt(6) == 0 ? 1 : 0;
        }

        @Override
        public int monsterPoints() {
            return 6;
        }

        @Override
        public String name() {
            return "Mummy";
        }
    };
}
