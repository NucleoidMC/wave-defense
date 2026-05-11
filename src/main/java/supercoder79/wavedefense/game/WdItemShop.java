package supercoder79.wavedefense.game;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import xyz.nucleoid.plasmid.api.shop.Cost;
import xyz.nucleoid.plasmid.api.shop.ShopEntry;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.api.util.PlayerRef;

import java.util.Map;
import java.util.function.Predicate;

public final class WdItemShop {
    public static void open(ServerPlayer player, WdActive game) {
        var shop = new SimpleGui(MenuType.GENERIC_9x5, player, false) {
            @Override
            public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
                var result = super.onClick(index, type, action, element);
                updateShop(player, game, this);
                return result;
            }
        };
        
        shop.setTitle(Component.literal("Item Shop"));

        updateShop(player, game, shop);

        shop.open();
    }

    public static void updateShop(ServerPlayer player, WdActive game, SimpleGui shop) {
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
                .withName(Component.literal("Upgrade Sword"))
                .addLore(Component.literal(swordText))
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
                .withName(Component.literal("Sword Sharpness " + (sharpness + 1)))
                .addLore(Component.literal("Increases the sharpness level of your sword"))
                .withCost(Cost.ofIron((sharpness + 1) * config.sharpness.base + 4 * (int) (Math.max(0, Math.pow(sharpness - 2, config.sharpness.scale)))))
                .onBuy(p -> {
                    properties.sharpness++;
                    applyEnchantments(player, stack -> stack.is(ItemTags.SWORDS), Enchantments.SHARPNESS, sharpness + 1);
                })
        );

        shop.setSlot(0 * 9 + 3, ShopEntry.ofIcon(Items.BOW)
                .withName(Component.literal("Bow Power " + (power + 1)))
                .addLore(Component.literal("Increases the power level of your bow"))
                .withCost(power >= 5 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.power.scale, power) * config.power.base)))
                .onBuy(p -> {
                    properties.power++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof BowItem, Enchantments.POWER, power + 1);
                })
        );

        shop.setSlot(0 * 9 + 4, ShopEntry.ofIcon(Items.CROSSBOW)
                .withName(Component.literal("Crossbow Piercing " + (piercing + 1)))
                .addLore(Component.literal("Increases the piercing level of your crossbow"))
                .withCost(piercing >= 5 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.piercing.scale, piercing) * config.piercing.base)))
                .onBuy(p -> {
                    properties.piercing++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof CrossbowItem, Enchantments.PIERCING, piercing + 1);
                })
        );

        shop.setSlot(0 * 9 + 4, ShopEntry.buyItem(new ItemStack(Items.ARROW, config.arrow.count), Cost.ofIron(config.arrow.cost)));

        shop.setSlot(1 * 9 + 0, ShopEntry.ofIcon(helmet)
                .withName(Component.literal("Upgrade Helmet"))
                .addLore(Component.literal(helmetText))
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
                .withName(Component.literal("Helmet Protection " + (helmetProtection + 1)))
                .addLore(Component.literal("Increases the protection level of your helmet"))
                .withCost(helmetProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, helmetProtection) * config.protection.base)))
                .onBuy(p -> {
                    properties.helmetProtection++;
                    applyEnchantments(player, stack -> stack.has(DataComponents.EQUIPPABLE) && stack.get(DataComponents.EQUIPPABLE).slot() == EquipmentSlot.HEAD, Enchantments.PROTECTION, helmetProtection + 1);
                })
        );

        shop.setSlot(1 * 9 + 3, ShopEntry.buyItem(new ItemStack(Items.BREAD, config.bread.count), Cost.ofIron(config.bread.cost)));
        shop.setSlot(1 * 9 + 4, ShopEntry.buyItem(new ItemStack(Items.COOKED_BEEF, config.steak.count), Cost.ofIron(config.steak.cost)));
        shop.setSlot(1 * 9 + 5, ShopEntry.buyItem(new ItemStack(Items.GOLDEN_CARROT, config.goldenCarrot.count), Cost.ofIron(config.goldenCarrot.cost)));
        shop.setSlot(1 * 9 + 6, ShopEntry.buyItem(new ItemStack(Items.GOLDEN_APPLE, config.goldenApple.count), Cost.ofIron(config.goldenApple.cost)));

        shop.setSlot(2 * 9 + 0, ShopEntry.ofIcon(chestplate)
                .withName(Component.literal("Upgrade Chestplate"))
                .addLore(Component.literal(chestplateText))
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
                .withName(Component.literal("Chestplate Protection " + (chestplateProtection + 1)))
                .addLore(Component.literal("Increases the protection level of your chestplate"))
                .withCost(chestplateProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, chestplateProtection) * config.protection.base)))
                .onBuy(p -> {
                    properties.chestplateProtection++;
                    applyEnchantments(player, stack -> stack.has(DataComponents.EQUIPPABLE) && stack.get(DataComponents.EQUIPPABLE).slot() == EquipmentSlot.CHEST, Enchantments.PROTECTION, chestplateProtection + 1);
                })
        );


        shop.setSlot(2 * 9 + 3, ShopEntry.buyItem(createPotion(
                Items.SPLASH_POTION, config.healingPotion.count,
                Potions.STRONG_HEALING),
                Cost.ofGold(config.healingPotion.cost)));
        shop.setSlot(2 * 9 + 4, ShopEntry.buyItem(createPotion(
                Items.SPLASH_POTION, config.harmingPotion.count,
                Potions.STRONG_HARMING),
                Cost.ofGold(config.harmingPotion.cost)));
        shop.setSlot(2 * 9 + 5, ShopEntry.buyItem(createPotion(
                Items.POTION, config.swiftnessPotion.count,
                Potions.SWIFTNESS),
                Cost.ofGold(config.swiftnessPotion.cost)));
        shop.setSlot(2 * 9 + 6, ShopEntry.buyItem(createPotion(
                Items.POTION, config.regenerationPotion.count,
                Potions.STRONG_REGENERATION),
                Cost.ofGold(config.regenerationPotion.cost)));

        shop.setSlot(3 * 9 + 0, ShopEntry.ofIcon(leggings)
                .withName(Component.literal("Upgrade Leggings"))
                .addLore(Component.literal(leggingsText))
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
                .withName(Component.literal("Leggings Protection " + (leggingsProtection + 1)))
                .addLore(Component.literal("Increases the protection level of your leggings"))
                .withCost(leggingsProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, leggingsProtection) * config.protection.base)))
                .onBuy(p -> {
                    properties.leggingsProtection++;
                    applyEnchantments(player, stack -> stack.has(DataComponents.EQUIPPABLE) && stack.get(DataComponents.EQUIPPABLE).slot() == EquipmentSlot.LEGS, Enchantments.PROTECTION, leggingsProtection + 1);
                })
        );

        shop.setSlot(4 * 9 + 0, ShopEntry.ofIcon(boots)
                .withName(Component.literal("Upgrade Boots"))
                .addLore(Component.literal(bootsText))
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
                .withName(Component.literal("Boots Protection " + (bootsProtection + 1)))
                .addLore(Component.literal("Increases the protection level of your boots"))
                .withCost(bootsProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, bootsProtection) * config.protection.base)))
                .onBuy(p -> {
                    properties.bootsProtection++;
                    applyEnchantments(player, stack -> stack.has(DataComponents.EQUIPPABLE) && stack.get(DataComponents.EQUIPPABLE).slot() == EquipmentSlot.FEET, Enchantments.PROTECTION, bootsProtection + 1);
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
                .withName(Component.literal("Crossbow Quick Charge " + (quickCharge + 1)))
                .addLore(Component.literal("Increases the quick charge level of your crossbow"))
                .withCost(quickCharge >= 3 ? Cost.no() : Cost.ofGold(quickChargeCost))
                .onBuy(p -> {
                    properties.quickChargeLevel++;
                    applyEnchantments(player, stack -> stack.getItem() instanceof CrossbowItem, Enchantments.QUICK_CHARGE, quickCharge + 1);
                })
        );
    }

    private static ItemStack createPotion(Item item, int count, Holder<Potion> potion) {
        var stack = item.getDefaultInstance();
        stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(potion));
        stack.set(DataComponents.MAX_STACK_SIZE, count);
        stack.setCount(count);
        return stack;
    }

    private static void applyEnchantments(ServerPlayer player, Predicate<ItemStack> predicate, ResourceKey<Enchantment> enchantment, int level) {
        if (level <= 0) return;

        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty() && predicate.test(stack)) {
                var entry = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
                int existingLevel = stack.getEnchantments().getLevel(entry);
                if (existingLevel != level) {
                    stack.enchant(entry, level);
                }
            }
        }
    }

    private static void replaceItem(ServerPlayer player, Predicate<ItemStack> predicate, ItemStack newItem) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty() && predicate.test(stack)) {
                for (var enchantments : stack.getEnchantments().entrySet()) {
                    newItem.enchant(enchantments.getKey(), enchantments.getIntValue());
                }
                inventory.setItem(slot, newItem);
            }
        }
    }
}
