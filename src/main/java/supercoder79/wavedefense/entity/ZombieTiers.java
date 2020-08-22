package supercoder79.wavedefense.entity;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

public final class ZombieTiers {
    public static void apply(ZombieEntity entity, ZombieType type, int tier) {
        entity.setCustomName(new LiteralText(type.prefix + "T" + (tier + 1) + " Zombie"));

        if (tier == 1) {
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
            entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
            entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
        } else if (tier == 2) {
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
            entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
            entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
            entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
        } else if (tier == 3) {
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
            entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
        }
    }
}
