package supercoder79.wavedefense.entity.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public final class EquipmentConfig {
    public static final EquipmentConfig EMPTY = new EquipmentConfig(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR);

    public static final Codec<EquipmentConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Registry.ITEM.optionalFieldOf("hand", Items.AIR).forGetter(config -> config.hand),
                Registry.ITEM.optionalFieldOf("head", Items.AIR).forGetter(config -> config.head),
                Registry.ITEM.optionalFieldOf("chest", Items.AIR).forGetter(config -> config.chest),
                Registry.ITEM.optionalFieldOf("legs", Items.AIR).forGetter(config -> config.legs),
                Registry.ITEM.optionalFieldOf("feet", Items.AIR).forGetter(config -> config.feet)
        ).apply(instance, EquipmentConfig::new);
    });

    public final Item hand;
    public final Item head;
    public final Item chest;
    public final Item legs;
    public final Item feet;

    public EquipmentConfig(Item hand, Item head, Item chest, Item legs, Item feet) {
        this.hand = hand;
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
    }

    public void applyTo(LivingEntity entity) {
        entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(this.hand));
        entity.equipStack(EquipmentSlot.HEAD, new ItemStack(this.head));
        entity.equipStack(EquipmentSlot.CHEST, new ItemStack(this.chest));
        entity.equipStack(EquipmentSlot.LEGS, new ItemStack(this.legs));
        entity.equipStack(EquipmentSlot.FEET, new ItemStack(this.feet));
    }
}
