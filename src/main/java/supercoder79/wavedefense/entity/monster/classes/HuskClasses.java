package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.world.item.DyeColor;
import supercoder79.wavedefense.entity.EquipmentHelper;
import supercoder79.wavedefense.entity.MonsterModifier;

import java.util.ArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;

public final class HuskClasses {
    public static final MonsterClass DEFAULT = new MonsterClass() {
        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {

        }

        @Override
        public int ironCount(RandomSource random) {
            return 1 + random.nextInt(2);
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
            return "Husk";
        }
    };

    public static final MonsterClass MUMMY = new MonsterClass() {
        @Override
        public void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal) {
            ItemStack enchantedSword = EquipmentHelper.enchant(Items.GOLDEN_SWORD, waveOrdinal, 30, random, entity.registryAccess());
            if (random.nextBoolean())
                entity.setItemSlot(EquipmentSlot.MAINHAND, enchantedSword);

            ArrayList<DyeColor> dyeItems = new ArrayList<>();
            dyeItems.add(DyeColor.WHITE);
            dyeItems.add(DyeColor.WHITE);
            dyeItems.add(DyeColor.WHITE);
            dyeItems.add(DyeColor.YELLOW);

            ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
            if (random.nextBoolean())
                helmet = EquipmentHelper.enchant(helmet.getItem(), waveOrdinal, 10, random, entity.registryAccess());
            helmet = DyedItemColor.applyDyes(helmet, dyeItems);

            ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
            if (random.nextBoolean())
                chestplate = EquipmentHelper.enchant(chestplate.getItem(), waveOrdinal, 10, random, entity.registryAccess());
            chestplate = DyedItemColor.applyDyes(chestplate, dyeItems);

            ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
            if (random.nextBoolean())
                leggings = EquipmentHelper.enchant(leggings.getItem(), waveOrdinal, 10, random, entity.registryAccess());
            leggings = DyedItemColor.applyDyes(leggings, dyeItems);

            ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
            if (random.nextBoolean())
                boots = EquipmentHelper.enchant(boots.getItem(), waveOrdinal, 10, random, entity.registryAccess());
            boots = DyedItemColor.applyDyes(boots, dyeItems);

            entity.setItemSlot(EquipmentSlot.HEAD, helmet);
            entity.setItemSlot(EquipmentSlot.CHEST, chestplate);
            entity.setItemSlot(EquipmentSlot.LEGS, leggings);
            entity.setItemSlot(EquipmentSlot.FEET, boots);

        }

        @Override
        public int ironCount(RandomSource random) {
            return 5;
        }

        @Override
        public int goldCount(RandomSource random) {
            return random.nextInt(6) == 0 ? 1 : 0;
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
