package supercoder79.wavedefense.game;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.shop.Cost;
import xyz.nucleoid.plasmid.shop.ShopEntry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.Map;
import java.util.function.Predicate;

public final class WdItemShop {
    public static void open(ServerPlayerEntity player, WdActive game) {
        var shop = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false) {
            @Override
            public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
                var result = super.onClick(index, type, action, element);
                updateShop(player, game, this);
                return result;
            }
        };
        
        shop.setTitle(Text.literal("Item Shop"));

        updateShop(player, game, shop);

        shop.open();
    }

    public static void updateShop(ServerPlayerEntity player, WdActive game, SimpleGui shop) {
        WdPlayerProperties properties = game.players.get(PlayerRef.of(player));
        WdConfig.Shop config = game.config.shop;

        String swordText = "Replaces your sword with a Diamond one";
        Item sword = Items.IRON_SWORD;
        Cost swordCost = Cost.ofGold(config.swordType.diamond);

        int sharpness = properties.sharpness;
        int power = properties.power;
        int piercing = properties.piercing;
        int helmetProtection = properties.helmetProtection;
        int chestplateProtection = properties.chestplateProtection;
        int leggingsProtection = properties.leggingsProtection;
        int bootsProtection = properties.bootsProtection;
        int helmetLevel = properties.helmetLevel;
        int chestplateLevel = properties.chestplateLevel;
        int leggingsLevel = properties.leggingsLevel;
        int bootsLevel = properties.bootsLevel;
        int swordLevel = properties.swordLevel;
        int quickCharge = properties.quickChargeLevel;

        if (swordLevel == 2) {
            swordText = "Replaces your sword with a Netherite one";
            sword = Items.DIAMOND_SWORD;
            swordCost = Cost.ofGold(config.swordType.netherite);
        } else if (swordLevel >= 3) {
            swordText = "MAX LEVEL REACHED";
            sword = Items.NETHERITE_SWORD;
            swordCost = Cost.no();
        }


        String helmetText = "Replaces your helmet with an Iron one";
        Item helmet = Items.CHAINMAIL_HELMET;
        Cost helmetCost = Cost.ofIron(4);

        if (helmetLevel == 1) {
            helmetText = "Replaces your helmet with a Diamond one";
            helmet = Items.IRON_HELMET;
            helmetCost = Cost.ofGold(config.armorType.diamond);
        } else if (helmetLevel == 2) {
            helmetText = "Replaces your helmet with a Netherite one";
            helmet = Items.DIAMOND_HELMET;
            helmetCost = Cost.ofGold(config.armorType.netherite);
        } else if (helmetLevel >= 3) {
            helmetText = "MAX LEVEL REACHED";
            helmet = Items.NETHERITE_HELMET;
            helmetCost = Cost.no();
        }


        String chestplateText = "Replaces your chestplate with an Iron one";
        Item chestplate = Items.CHAINMAIL_CHESTPLATE;
        Cost chestplateCost = Cost.ofGold(config.armorType.iron);

        if (chestplateLevel == 1) {
            chestplateText = "Replaces your chestplate with a Diamond one";
            chestplate = Items.IRON_CHESTPLATE;
            chestplateCost = Cost.ofGold(config.armorType.diamond);
        } else if (chestplateLevel == 2) {
            chestplateText = "Replaces your chestplate with a Netherite one";
            chestplate = Items.DIAMOND_CHESTPLATE;
            chestplateCost = Cost.ofGold(config.armorType.netherite);
        } else if (chestplateLevel >= 3) {
            chestplateText = "MAX LEVEL REACHED";
            chestplate = Items.NETHERITE_CHESTPLATE;
            chestplateCost = Cost.no();
        }


        String leggingsText = "Replaces your leggings with Iron ones";
        Item leggings = Items.CHAINMAIL_LEGGINGS;
        Cost leggingsCost = Cost.ofGold(config.armorType.iron);

        if (leggingsLevel == 1) {
            leggingsText = "Replaces your leggings with Diamond ones";
            leggings = Items.IRON_LEGGINGS;
            leggingsCost = Cost.ofGold(config.armorType.diamond);
        } else if (leggingsLevel == 2) {
            leggingsText = "Replaces your leggings with Netherite ones";
            leggings = Items.DIAMOND_LEGGINGS;
            leggingsCost = Cost.ofGold(config.armorType.netherite);
        } else if (leggingsLevel >= 3) {
            leggingsText = "MAX LEVEL REACHED";
            leggings = Items.NETHERITE_LEGGINGS;
            leggingsCost = Cost.no();
        }


        String bootsText = "Replaces your boots with Iron ones";
        Item boots = Items.CHAINMAIL_BOOTS;
        Cost bootsCost = Cost.ofGold(config.armorType.iron);

        if (bootsLevel == 1) {
            bootsText = "Replaces your boots with Diamond ones";
            boots = Items.IRON_BOOTS;
            bootsCost = Cost.ofGold(config.armorType.diamond);
        } else if (bootsLevel == 2) {
            bootsText = "Replaces your boots with Netherite ones";
            boots = Items.DIAMOND_BOOTS;
            bootsCost = Cost.ofGold(config.armorType.netherite);
        } else if (bootsLevel >= 3) {
            bootsText = "MAX LEVEL REACHED";
            boots = Items.NETHERITE_BOOTS;
            bootsCost = Cost.no();
        }

        shop.setSlot(0 * 9 + 0, ShopEntry.ofIcon(sword)
                .withName(Text.literal("Upgrade Sword"))
                .addLore(Text.literal(swordText))
                .withCost(swordCost)
                .onBuy(p -> {
                    properties.swordLevel++;
                    switch (swordLevel) {
                        case 1:
                            replaceItem(player, stack -> stack.getItem().equals(Items.IRON_SWORD), ItemStackBuilder.of(Items.DIAMOND_SWORD).setUnbreakable().build());
                            break;
                        case 2:
                            replaceItem(player, stack -> stack.getItem().equals(Items.DIAMOND_SWORD), ItemStackBuilder.of(Items.NETHERITE_SWORD).setUnbreakable().build());
                            break;
                    }
                })
        );

        shop.setSlot(0 * 9 + 1, ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                .withName(Text.literal("Sword Sharpness " + (sharpness + 1)))
                .addLore(Text.literal("Increases the sharpness level of your sword"))
                .withCost(Cost.ofIron((sharpness + 1) * config.sharpness.base + 4 * (int) (Math.max(0, Math.pow(sharpness - 2, config.sharpness.scale)))))
                .onBuy(p -> {
                    properties.sharpness++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof SwordItem, Enchantments.SHARPNESS, sharpness + 1);
                })
        );

        shop.setSlot(0 * 9 + 3, ShopEntry.ofIcon(Items.BOW)
                .withName(Text.literal("Bow Power " + (power + 1)))
                .addLore(Text.literal("Increases the power level of your bow"))
                .withCost(power >= 5 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.power.scale, power) * config.power.base)))
                .onBuy(p -> {
                    properties.power++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof BowItem, Enchantments.POWER, power + 1);
                })
        );

        shop.setSlot(0 * 9 + 4, ShopEntry.ofIcon(Items.CROSSBOW)
                .withName(Text.literal("Crossbow Piercing " + (piercing + 1)))
                .addLore(Text.literal("Increases the piercing level of your crossbow"))
                .withCost(piercing >= 5 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.piercing.scale, piercing) * config.piercing.base)))
                .onBuy(p -> {
                    properties.piercing++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof CrossbowItem, Enchantments.PIERCING, piercing + 1);
                })
        );

        shop.setSlot(0 * 9 + 4, ShopEntry.buyItem(new ItemStack(Items.ARROW, config.arrow.count), Cost.ofIron(config.arrow.cost)));

        shop.setSlot(1 * 9 + 0, ShopEntry.ofIcon(helmet)
                .withName(Text.literal("Upgrade Helmet"))
                .addLore(Text.literal(helmetText))
                .withCost(helmetCost)
                .onBuy(p -> {
                    properties.helmetLevel++;
                    switch (helmetLevel) {
                        case 0:
                            replaceItem(player, stack -> stack.getItem().equals(Items.CHAINMAIL_HELMET), ItemStackBuilder.of(Items.IRON_HELMET).setUnbreakable().build());
                            break;
                        case 1:
                            replaceItem(player, stack -> stack.getItem().equals(Items.IRON_HELMET), ItemStackBuilder.of(Items.DIAMOND_HELMET).setUnbreakable().build());
                            break;
                        case 2:
                            replaceItem(player, stack -> stack.getItem().equals(Items.DIAMOND_HELMET), ItemStackBuilder.of(Items.NETHERITE_HELMET).setUnbreakable().build());
                            break;
                    }
                })
        );

        shop.setSlot(1 * 9 + 1, ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                .withName(Text.literal("Helmet Protection " + (helmetProtection + 1)))
                .addLore(Text.literal("Increases the protection level of your helmet"))
                .withCost(helmetProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, helmetProtection) * config.protection.base)))
                .onBuy(p -> {
                    properties.helmetProtection++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlotType().equals(EquipmentSlot.HEAD), Enchantments.PROTECTION, helmetProtection + 1);
                })
        );

        shop.setSlot(1 * 9 + 3, ShopEntry.buyItem(new ItemStack(Items.BREAD, config.bread.count), Cost.ofIron(config.bread.cost)));
        shop.setSlot(1 * 9 + 4, ShopEntry.buyItem(new ItemStack(Items.COOKED_BEEF, config.steak.count), Cost.ofIron(config.steak.cost)));
        shop.setSlot(1 * 9 + 5, ShopEntry.buyItem(new ItemStack(Items.GOLDEN_CARROT, config.goldenCarrot.count), Cost.ofIron(config.goldenCarrot.cost)));
        shop.setSlot(1 * 9 + 6, ShopEntry.buyItem(new ItemStack(Items.GOLDEN_APPLE, config.goldenApple.count), Cost.ofIron(config.goldenApple.cost)));

        shop.setSlot(2 * 9 + 0, ShopEntry.ofIcon(chestplate)
                .withName(Text.literal("Upgrade Chestplate"))
                .addLore(Text.literal(chestplateText))
                .withCost(chestplateCost)
                .onBuy(p -> {
                    properties.chestplateLevel++;
                    switch (chestplateLevel) {
                        case 0:
                            replaceItem(player, stack -> stack.getItem().equals(Items.CHAINMAIL_CHESTPLATE), ItemStackBuilder.of(Items.IRON_CHESTPLATE).setUnbreakable().build());
                            break;
                        case 1:
                            replaceItem(player, stack -> stack.getItem().equals(Items.IRON_CHESTPLATE), ItemStackBuilder.of(Items.DIAMOND_CHESTPLATE).setUnbreakable().build());
                            break;
                        case 2:
                            replaceItem(player, stack -> stack.getItem().equals(Items.DIAMOND_CHESTPLATE), ItemStackBuilder.of(Items.NETHERITE_CHESTPLATE).setUnbreakable().build());
                            break;
                    }
                })
        );

        shop.setSlot(2 * 9 + 1, ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                .withName(Text.literal("Chestplate Protection " + (chestplateProtection + 1)))
                .addLore(Text.literal("Increases the protection level of your chestplate"))
                .withCost(chestplateProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, chestplateProtection) * config.protection.base)))
                .onBuy(p -> {
                    properties.chestplateProtection++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlotType().equals(EquipmentSlot.CHEST), Enchantments.PROTECTION, chestplateProtection + 1);
                })
        );


        shop.setSlot(2 * 9 + 3, ShopEntry.buyItem(PotionUtil.setPotion(
                new ItemStack(Items.SPLASH_POTION, config.healingPotion.count),
                Potions.STRONG_HEALING),
                Cost.ofGold(config.healingPotion.cost)));
        shop.setSlot(2 * 9 + 4, ShopEntry.buyItem(PotionUtil.setPotion(
                new ItemStack(Items.SPLASH_POTION, config.harmingPotion.count),
                Potions.STRONG_HARMING),
                Cost.ofGold(config.harmingPotion.cost)));
        shop.setSlot(2 * 9 + 5, ShopEntry.buyItem(PotionUtil.setPotion(
                new ItemStack(Items.POTION, config.swiftnessPotion.count),
                Potions.SWIFTNESS),
                Cost.ofGold(config.swiftnessPotion.cost)));
        shop.setSlot(2 * 9 + 6, ShopEntry.buyItem(PotionUtil.setPotion(
                new ItemStack(Items.POTION, config.regenerationPotion.count),
                Potions.STRONG_REGENERATION),
                Cost.ofGold(config.regenerationPotion.cost)));

        shop.setSlot(3 * 9 + 0, ShopEntry.ofIcon(leggings)
                .withName(Text.literal("Upgrade Leggings"))
                .addLore(Text.literal(leggingsText))
                .withCost(leggingsCost)
                .onBuy(p -> {
                    properties.leggingsLevel++;
                    switch (leggingsLevel) {
                        case 0:
                            replaceItem(player, stack -> stack.getItem().equals(Items.CHAINMAIL_LEGGINGS), ItemStackBuilder.of(Items.IRON_LEGGINGS).setUnbreakable().build());
                            break;
                        case 1:
                            replaceItem(player, stack -> stack.getItem().equals(Items.IRON_LEGGINGS), ItemStackBuilder.of(Items.DIAMOND_LEGGINGS).setUnbreakable().build());
                            break;
                        case 2:
                            replaceItem(player, stack -> stack.getItem().equals(Items.DIAMOND_LEGGINGS), ItemStackBuilder.of(Items.NETHERITE_LEGGINGS).setUnbreakable().build());
                            break;
                    }
                })
        );

        shop.setSlot(3 * 9 + 1, ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                .withName(Text.literal("Leggings Protection " + (leggingsProtection + 1)))
                .addLore(Text.literal("Increases the protection level of your leggings"))
                .withCost(leggingsProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, leggingsProtection) * config.protection.base)))
                .onBuy(p -> {
                    properties.leggingsProtection++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlotType().equals(EquipmentSlot.LEGS), Enchantments.PROTECTION, leggingsProtection + 1);
                })
        );

        shop.setSlot(4 * 9 + 0, ShopEntry.ofIcon(boots)
                .withName(Text.literal("Upgrade Boots"))
                .addLore(Text.literal(bootsText))
                .withCost(bootsCost)
                .onBuy(p -> {
                    properties.bootsLevel++;
                    switch (bootsLevel) {
                        case 0:
                            replaceItem(player, stack -> stack.getItem().equals(Items.CHAINMAIL_BOOTS), ItemStackBuilder.of(Items.IRON_BOOTS).setUnbreakable().build());
                            break;
                        case 1:
                            replaceItem(player, stack -> stack.getItem().equals(Items.IRON_BOOTS), ItemStackBuilder.of(Items.DIAMOND_BOOTS).setUnbreakable().build());
                            break;
                        case 2:
                            replaceItem(player, stack -> stack.getItem().equals(Items.DIAMOND_BOOTS), ItemStackBuilder.of(Items.NETHERITE_BOOTS).setUnbreakable().build());
                            break;
                    }
                })
        );

        shop.setSlot(4 * 9 + 1, ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                .withName(Text.literal("Boots Protection " + (bootsProtection + 1)))
                .addLore(Text.literal("Increases the protection level of your boots"))
                .withCost(bootsProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, bootsProtection) * config.protection.base)))
                .onBuy(p -> {
                    properties.bootsProtection++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlotType().equals(EquipmentSlot.FEET), Enchantments.PROTECTION, bootsProtection + 1);
                })
        );

        int quickChargeCost = config.quickCharge.lvl1;
        if (properties.quickChargeLevel == 1) {
            quickChargeCost = config.quickCharge.lvl2;
        }
        if (properties.quickChargeLevel == 2) {
            quickChargeCost = config.quickCharge.lvl3;
        }

        shop.setSlot(4 * 9 + 3, ShopEntry.ofIcon(Items.CROSSBOW)
                .withName(Text.literal("Crossbow Quick Charge " + (quickCharge + 1)))
                .addLore(Text.literal("Increases the quick charge level of your crossbow"))
                .withCost(quickCharge >= 3 ? Cost.no() : Cost.ofGold(quickChargeCost))
                .onBuy(p -> {
                    properties.quickChargeLevel++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof CrossbowItem, Enchantments.QUICK_CHARGE, quickCharge + 1);
                })
        );
    }

    private static void applyEnchantments(ServerPlayerEntity player, Predicate<ItemStack> predicate, Enchantment enchantment, int level) {
        if (level <= 0) return;

        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!stack.isEmpty() && predicate.test(stack)) {
                int existingLevel = EnchantmentHelper.getLevel(enchantment, stack);
                if (existingLevel != level) {
                    var list = EnchantmentHelper.get(stack);
                    list.put(enchantment, level);
                    EnchantmentHelper.set(list, stack);
                }
            }
        }
    }

    private static void replaceItem(ServerPlayerEntity player, Predicate<ItemStack> predicate, ItemStack newItem) {
        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!stack.isEmpty() && predicate.test(stack)) {
                for (Map.Entry<Enchantment, Integer> enchantments : EnchantmentHelper.get(stack).entrySet()) {
                    newItem.addEnchantment(enchantments.getKey(), enchantments.getValue());
                }
                inventory.setStack(slot, newItem);
            }
        }
    }
}
