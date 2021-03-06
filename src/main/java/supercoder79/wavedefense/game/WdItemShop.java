package supercoder79.wavedefense.game;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.shop.Cost;
import xyz.nucleoid.plasmid.shop.ShopEntry;
import xyz.nucleoid.plasmid.shop.ShopUi;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.ItemUtil;

import java.util.function.Predicate;

public final class WdItemShop {
    public static ShopUi create(ServerPlayerEntity player, WdActive game) {
        return ShopUi.create(new LiteralText("Item Shop"), shop -> {
            int sharpnessLevel = game.getEnchantmentLevel(game.sharpnessLevels, player);
            Cost sharpnessCost = sharpnessLevel >= 5 ? Cost.no() : Cost.ofIron((int) (Math.pow(2, sharpnessLevel) * 16));

            shop.add(ShopEntry.ofIcon(Items.IRON_SWORD)
                    .withName(new LiteralText("Sword Sharpness " + (sharpnessLevel + 1)))
                    .addLore(new LiteralText("Increases the sharpness level of your sword."))
                    .withCost(sharpnessCost)
                    .onBuy(p -> {
                        game.increaseEnchantment(game.sharpnessLevels, player);
                        applyEnchantments(player, stack -> stack.getItem().isIn(FabricToolTags.SWORDS), Enchantments.SHARPNESS, sharpnessLevel + 1);
                    })
            );

            int protectionLevel = game.getEnchantmentLevel(game.protectionLevels, player);
            Cost protectionCost = protectionLevel >= 5 ? Cost.no() : Cost.ofIron((int) (Math.pow(2, protectionLevel) * 16));

            shop.add(ShopEntry.ofIcon(Items.IRON_CHESTPLATE)
                    .withName(new LiteralText("Armor Protection " + (protectionLevel + 1)))
                    .addLore(new LiteralText("Increases the protection level of your armor."))
                    .withCost(protectionCost)
                    .onBuy(p -> {
                        game.increaseEnchantment(game.protectionLevels, player);
                        applyEnchantments(player, stack -> stack.getItem() instanceof ArmorItem, Enchantments.PROTECTION, protectionLevel + 1);
                    })
            );

            int powerLevel = game.getEnchantmentLevel(game.powerLevels, player);
            Cost powerCost = powerLevel >= 5 ? Cost.no() : Cost.ofIron((int) (Math.pow(2, powerLevel) * 16));

            shop.add(ShopEntry.ofIcon(Items.CROSSBOW)
                    .withName(new LiteralText("Bow Power " + (powerLevel + 1)))
                    .addLore(new LiteralText("Increases the power level of your bow."))
                    .withCost(powerCost)
                    .onBuy(p -> {
                        game.increaseEnchantment(game.powerLevels, player);
                        applyEnchantments(player, stack -> stack.getItem() instanceof BowItem, Enchantments.POWER, powerLevel + 1);
                    })
            );

            shop.addItem(ItemStackBuilder.of(Items.IRON_AXE).setUnbreakable().build(), Cost.ofIron(32));
            shop.addItem(ItemStackBuilder.of(Items.ARROW).setCount(4).build(), Cost.ofIron(2));

            shop.addItem(new ItemStack(Items.GOLDEN_APPLE), Cost.ofIron(8));
            shop.addItem(new ItemStack(Items.COOKED_BEEF, 2), Cost.ofIron(1));
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
}
