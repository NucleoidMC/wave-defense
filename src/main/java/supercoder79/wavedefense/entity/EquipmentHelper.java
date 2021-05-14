package supercoder79.wavedefense.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import supercoder79.wavedefense.util.RandomCollection;

import java.util.Random;

public class EquipmentHelper {
    public static ItemStack enchant(Item item, int waveOrdinal, int difficulty, Random random) {
        if (random.nextDouble() > Math.max(0, 1 - (difficulty / 80d + waveOrdinal / 40d)))
            return EnchantmentHelper.enchant(random, new ItemStack(item), (int) (Math.ceil((waveOrdinal - 10) / 5d) * (Math.max(difficulty, 2) / 4d)), true);
        return new ItemStack(item);
    }

    public static int equipHelmet(int waveOrdinal, Random random, int iron, MobEntity entity, int difficulty) {
        RandomCollection<Item> helmet = new RandomCollection<>();
        helmet
                .add(Math.max(0, 10 - difficulty - waveOrdinal / 8d), Items.LEATHER_HELMET)
                .add(Math.min(waveOrdinal / 2d - 3 + difficulty, 20), Items.CHAINMAIL_HELMET)
                .add(Math.min(waveOrdinal / 3d - 6 + difficulty / 1.25d, 30), Items.IRON_HELMET)
                .add(waveOrdinal / 4d - 9 + difficulty / 1.5d, Items.DIAMOND_HELMET);

        Item selectedHelmet = helmet.next();

        if (Items.LEATHER_HELMET.equals(selectedHelmet)) {
            if (random.nextInt(3) == 0) iron++;
        } else if (Items.CHAINMAIL_HELMET.equals(selectedHelmet)) {
            if (random.nextInt(2) == 0) iron++;
        } else if (Items.IRON_HELMET.equals(selectedHelmet)) {
            iron++;
        } else if (Items.DIAMOND_HELMET.equals(selectedHelmet)) {
            iron++;
            if (random.nextInt(2) == 0) iron++;
        }

        entity.equipStack(EquipmentSlot.HEAD, enchant(selectedHelmet, waveOrdinal, difficulty, random));
        return iron;
    }

    public static int equipChestplate(int waveOrdinal, Random random, int iron, MobEntity entity, int difficulty) {
        RandomCollection<Item> chestplate = new RandomCollection<>();
        chestplate
                .add(Math.max(0, 10 - difficulty), Items.LEATHER_CHESTPLATE)
                .add(Math.min(waveOrdinal / 2d - 3 + difficulty, 20), Items.CHAINMAIL_CHESTPLATE)
                .add(Math.min(waveOrdinal / 3d - 6 + difficulty / 1.25d, 30), Items.IRON_CHESTPLATE)
                .add(waveOrdinal / 4d - 9 + difficulty / 1.5d, Items.DIAMOND_CHESTPLATE);

        Item selectedChestplate = chestplate.next();

        if (Items.LEATHER_CHESTPLATE.equals(selectedChestplate)) {
            if (random.nextInt(3) == 0) iron++;
        } else if (Items.CHAINMAIL_CHESTPLATE.equals(selectedChestplate)) {
            if (random.nextInt(2) == 0) iron++;
        } else if (Items.IRON_CHESTPLATE.equals(selectedChestplate)) {
            iron++;
        } else if (Items.DIAMOND_CHESTPLATE.equals(selectedChestplate)) {
            iron++;
            if (random.nextInt(2) == 0) iron++;
        }

        entity.equipStack(EquipmentSlot.CHEST, enchant(selectedChestplate, waveOrdinal, difficulty, random));
        return iron;
    }

    public static int equipLeggings(int waveOrdinal, Random random, int iron, MobEntity entity, int difficulty) {
        RandomCollection<Item> leggings = new RandomCollection<>();
        leggings
                .add(Math.max(0, 10 - difficulty), Items.LEATHER_LEGGINGS)
                .add(Math.min(waveOrdinal / 2d - 3 + difficulty, 20), Items.CHAINMAIL_LEGGINGS)
                .add(Math.min(waveOrdinal / 3d - 6 + difficulty / 1.25d, 30), Items.IRON_LEGGINGS)
                .add(waveOrdinal / 4d - 9 + difficulty + 1.5d, Items.DIAMOND_LEGGINGS);

        Item selectedLeggings = leggings.next();

        if (Items.LEATHER_LEGGINGS.equals(selectedLeggings)) {
            if (random.nextInt(3) == 0) iron++;
        } else if (Items.CHAINMAIL_LEGGINGS.equals(selectedLeggings)) {
            if (random.nextInt(2) == 0) iron++;
        } else if (Items.IRON_LEGGINGS.equals(selectedLeggings)) {
            iron++;
        } else if (Items.DIAMOND_LEGGINGS.equals(selectedLeggings)) {
            iron++;
            if (random.nextInt(2) == 0) iron++;
        }

        entity.equipStack(EquipmentSlot.LEGS, enchant(selectedLeggings, waveOrdinal, difficulty, random));
        return iron;
    }

    public static int equipBoots(int waveOrdinal, Random random, int iron, MobEntity entity, int difficulty) {
        RandomCollection<Item> boots = new RandomCollection<>();
        boots
                .add(Math.max(0, 10 - difficulty), Items.LEATHER_BOOTS)
                .add(Math.min(waveOrdinal / 2d - 3 + difficulty, 20), Items.CHAINMAIL_BOOTS)
                .add(Math.min(waveOrdinal / 3d - 6 + difficulty / 1.25d, 30), Items.IRON_BOOTS)
                .add(waveOrdinal / 4d - 9 + difficulty / 1.5d, Items.DIAMOND_BOOTS);

        Item selectedBoots = boots.next();

        if (Items.LEATHER_BOOTS.equals(selectedBoots)) {
            if (random.nextInt(3) == 0) iron++;
        } else if (Items.CHAINMAIL_BOOTS.equals(selectedBoots)) {
            if (random.nextInt(2) == 0) iron++;
        } else if (Items.IRON_BOOTS.equals(selectedBoots)) {
            iron++;
        } else if (Items.DIAMOND_BOOTS.equals(selectedBoots)) {
            iron++;
            if (random.nextInt(2) == 0) iron++;
        }

        entity.equipStack(EquipmentSlot.FEET, enchant(selectedBoots, waveOrdinal, difficulty, random));
        return iron;
    }

    public static int equipSword(int waveOrdinal, Random random, int iron, MobEntity entity, int difficulty) {
        RandomCollection<Item> sword = new RandomCollection<>();
        sword
                .add(Math.max(0, 10 - difficulty), Items.WOODEN_SWORD)
                .add(Math.min(waveOrdinal / 1.5d - 2.5 + difficulty, 15), Items.STONE_SWORD)
                .add(Math.min(waveOrdinal / 2d - 3 + difficulty, 20), Items.IRON_SWORD)
                .add(Math.min(waveOrdinal / 3d - 6 + difficulty / 1.25d, 30), Items.DIAMOND_SWORD);

        Item selectedSword = sword.next();

        if (Items.WOODEN_SWORD.equals(selectedSword)) {
            if (random.nextInt(3) == 0) iron++;
        } else if (Items.STONE_SWORD.equals(selectedSword)) {
            if (random.nextInt(2) == 0) iron++;
        } else if (Items.IRON_SWORD.equals(selectedSword)) {
            iron++;
        } else if (Items.DIAMOND_SWORD.equals(selectedSword)) {
            iron++;
            if (random.nextInt(2) == 0) iron++;
        }

        entity.equipStack(EquipmentSlot.MAINHAND, enchant(selectedSword, waveOrdinal, difficulty, random));
        return iron;
    }
}
