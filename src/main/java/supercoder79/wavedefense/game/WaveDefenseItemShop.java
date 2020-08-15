package supercoder79.wavedefense.game;

import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.shop.Cost;
import xyz.nucleoid.plasmid.shop.ShopUi;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public final class WaveDefenseItemShop {
    public static ShopUi create(ServerPlayerEntity player, WaveDefenseActive game) {
        return ShopUi.create(new LiteralText("Item Shop"), shop -> {
            shop.addItem(ItemStackBuilder.of(Items.IRON_AXE).setUnbreakable().build(), Cost.ofIron(32));
            shop.addItem(ItemStackBuilder.of(Items.BOW).setUnbreakable().build(), Cost.ofIron(32));
            shop.addItem(ItemStackBuilder.of(Items.ARROW).setCount(8).build(), Cost.ofIron(2));
        });
    }
}
