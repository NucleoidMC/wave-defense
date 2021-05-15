package supercoder79.wavedefense.game;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.shop.Cost;
import xyz.nucleoid.plasmid.shop.ShopEntry;
import xyz.nucleoid.plasmid.shop.ShopUi;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.ItemUtil;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.Map;
import java.util.function.Predicate;

public final class WdItemShop {
    public static ShopUi create(ServerPlayerEntity player, WdActive game) {
        WdPlayerProperties properties = game.players.get(PlayerRef.of(player));
        WdConfig.Shop config = game.config.shop;

        return ShopUi.create(new LiteralText("Item Shop"), shop -> {
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

            shop.add(ShopEntry.ofIcon(sword)
                    .withName(new LiteralText("Upgrade Sword"))
                    .addLore(new LiteralText(swordText))
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

            shop.add(ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                    .withName(new LiteralText("Sword Sharpness " + (sharpness + 1)))
                    .addLore(new LiteralText("Increases the sharpness level of your sword"))
                    .withCost(Cost.ofIron((sharpness + 1) * config.sharpness.base + 4 * (int) (Math.max(0, Math.pow(sharpness - 2, config.sharpness.scale)))))
                    .onBuy(p -> {
                        properties.sharpness++;
                        applyEnchantments(player, stack -> stack.getItem() instanceof SwordItem, Enchantments.SHARPNESS, sharpness + 1);
                    })
            );

            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));

            shop.add(ShopEntry.ofIcon(Items.BOW)
                    .withName(new LiteralText("Bow Power " + (power + 1)))
                    .addLore(new LiteralText("Increases the power level of your bow"))
                    .withCost(power >= 5 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.power.scale, power) * config.power.base)))
                    .onBuy(p -> {
                        properties.power++;
                        applyEnchantments(player, stack -> stack.getItem() instanceof BowItem, Enchantments.POWER, power + 1);
                    })
            );

            shop.add(ShopEntry.ofIcon(Items.CROSSBOW)
                    .withName(new LiteralText("Crossbow Piercing " + (piercing + 1)))
                    .addLore(new LiteralText("Increases the piercing level of your crossbow"))
                    .withCost(piercing >= 5 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.piercing.scale, piercing) * config.piercing.base)))
                    .onBuy(p -> {
                        properties.piercing++;
                        applyEnchantments(player, stack -> stack.getItem() instanceof CrossbowItem, Enchantments.PIERCING, piercing + 1);
                    })
            );

            shop.addItem(ItemStackBuilder.of(Items.ARROW).setCount(config.arrow.count).build(), Cost.ofIron(config.arrow.cost));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));

            shop.add(ShopEntry.ofIcon(helmet)
                    .withName(new LiteralText("Upgrade Helmet"))
                    .addLore(new LiteralText(helmetText))
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

            shop.add(ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                    .withName(new LiteralText("Helmet Protection " + (helmetProtection + 1)))
                    .addLore(new LiteralText("Increases the protection level of your helmet"))
                    .withCost(helmetProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, helmetProtection) * config.protection.base)))
                    .onBuy(p -> {
                        properties.helmetProtection++;
                        applyEnchantments(player, stack -> stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlotType().equals(EquipmentSlot.HEAD), Enchantments.PROTECTION, helmetProtection + 1);
                    })
            );

            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));

            shop.addItem(new ItemStack(Items.BREAD, config.bread.count), Cost.ofIron(config.bread.cost));
            shop.addItem(new ItemStack(Items.COOKED_BEEF, config.steak.count), Cost.ofIron(config.steak.cost));
            shop.addItem(new ItemStack(Items.GOLDEN_CARROT, config.goldenCarrot.count), Cost.ofIron(config.goldenCarrot.cost));
            shop.addItem(new ItemStack(Items.GOLDEN_APPLE, config.goldenApple.count), Cost.ofIron(config.goldenApple.cost));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));

            shop.add(ShopEntry.ofIcon(chestplate)
                    .withName(new LiteralText("Upgrade Chestplate"))
                    .addLore(new LiteralText(chestplateText))
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

            shop.add(ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                    .withName(new LiteralText("Chestplate Protection " + (chestplateProtection + 1)))
                    .addLore(new LiteralText("Increases the protection level of your chestplate"))
                    .withCost(chestplateProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, chestplateProtection) * config.protection.base)))
                    .onBuy(p -> {
                        properties.chestplateProtection++;
                        applyEnchantments(player, stack -> stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlotType().equals(EquipmentSlot.CHEST), Enchantments.PROTECTION, chestplateProtection + 1);
                    })
            );

            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));


            shop.addItem(PotionUtil.setPotion(
                    new ItemStack(Items.SPLASH_POTION, config.healingPotion.count),
                    Potions.STRONG_HEALING),
                    Cost.ofGold(config.healingPotion.cost));
            shop.addItem(PotionUtil.setPotion(
                    new ItemStack(Items.SPLASH_POTION, config.harmingPotion.count),
                    Potions.STRONG_HARMING),
                    Cost.ofGold(config.harmingPotion.cost));
            shop.addItem(PotionUtil.setPotion(
                    new ItemStack(Items.POTION, config.swiftnessPotion.count),
                    Potions.SWIFTNESS),
                    Cost.ofGold(config.swiftnessPotion.cost));
            shop.addItem(PotionUtil.setPotion(
                    new ItemStack(Items.POTION, config.regenerationPotion.count),
                    Potions.STRONG_REGENERATION),
                    Cost.ofGold(config.regenerationPotion.cost));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));

            shop.add(ShopEntry.ofIcon(leggings)
                    .withName(new LiteralText("Upgrade Leggings"))
                    .addLore(new LiteralText(leggingsText))
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

            shop.add(ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                    .withName(new LiteralText("Leggings Protection " + (leggingsProtection + 1)))
                    .addLore(new LiteralText("Increases the protection level of your leggings"))
                    .withCost(leggingsProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, leggingsProtection) * config.protection.base)))
                    .onBuy(p -> {
                        properties.leggingsProtection++;
                        applyEnchantments(player, stack -> stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlotType().equals(EquipmentSlot.LEGS), Enchantments.PROTECTION, leggingsProtection + 1);
                    })
            );

            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));

            shop.add(ShopEntry.ofIcon(boots)
                    .withName(new LiteralText("Upgrade Boots"))
                    .addLore(new LiteralText(bootsText))
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

            shop.add(ShopEntry.ofIcon(Items.ENCHANTED_BOOK)
                    .withName(new LiteralText("Boots Protection " + (bootsProtection + 1)))
                    .addLore(new LiteralText("Increases the protection level of your boots"))
                    .withCost(bootsProtection >= 4 ? Cost.no() : Cost.ofIron((int) (Math.pow(config.protection.scale, bootsProtection) * config.protection.base)))
                    .onBuy(p -> {
                        properties.bootsProtection++;
                        applyEnchantments(player, stack -> stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlotType().equals(EquipmentSlot.FEET), Enchantments.PROTECTION, bootsProtection + 1);
                    })
            );

            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));

            int quickChargeCost = config.quickCharge.lvl1;
            if (properties.quickChargeLevel == 1) {
                quickChargeCost = config.quickCharge.lvl2;
            }
            if (properties.quickChargeLevel == 2) {
                quickChargeCost = config.quickCharge.lvl3;
            }

            shop.add(ShopEntry.ofIcon(Items.CROSSBOW)
                    .withName(new LiteralText("Crossbow Quick Charge " + (quickCharge + 1)))
                    .addLore(new LiteralText("Increases the quick charge level of your crossbow"))
                    .withCost(quickCharge >= 3 ? Cost.no() : Cost.ofGold(quickChargeCost))
                    .onBuy(p -> {
                        properties.quickChargeLevel++;
                        applyEnchantments(player, stack -> stack.getItem() instanceof CrossbowItem, Enchantments.QUICK_CHARGE, quickCharge + 1);
                    })
            );

            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
            shop.add(ShopEntry.ofIcon(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .withCost(Cost.no()));
        });
    }

    private static void applyEnchantments(ServerPlayerEntity player, Predicate<ItemStack> predicate, Enchantment enchantment, int level) {
        if (level <= 0) return;

        PlayerInventory inventory = player.inventory;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!stack.isEmpty() && predicate.test(stack)) {
                int existingLevel = ItemUtil.getEnchantLevel(stack, enchantment);
                if (existingLevel != level) {
                    ItemUtil.removeEnchant(stack, enchantment);
                    stack.addEnchantment(enchantment, level);
                }
            }
        }
    }

    private static void replaceItem(ServerPlayerEntity player, Predicate<ItemStack> predicate, ItemStack newItem) {
        PlayerInventory inventory = player.inventory;
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
