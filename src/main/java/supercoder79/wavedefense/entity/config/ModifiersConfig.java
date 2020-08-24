package supercoder79.wavedefense.entity.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;

public final class ModifiersConfig {
    public static final ModifiersConfig EMPTY = new ModifiersConfig(Collections.emptyList());

    private static final Codec<StatusEffectInstance> EFFECT_CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Registry.STATUS_EFFECT.fieldOf("type").forGetter(StatusEffectInstance::getEffectType),
                Codec.INT.fieldOf("duration").forGetter(StatusEffectInstance::getDuration),
                Codec.INT.optionalFieldOf("amplifier", 0).forGetter(StatusEffectInstance::getAmplifier),
                Codec.BOOL.optionalFieldOf("ambient", false).forGetter(StatusEffectInstance::isAmbient),
                Codec.BOOL.optionalFieldOf("visible", true).forGetter(StatusEffectInstance::shouldShowParticles)
        ).apply(instance, StatusEffectInstance::new);
    });

    public static final Codec<ModifiersConfig> CODEC = EFFECT_CODEC.listOf().xmap(
            ModifiersConfig::new,
            config -> config.effects
    );

    private final List<StatusEffectInstance> effects;

    public ModifiersConfig(List<StatusEffectInstance> effects) {
        this.effects = effects;
    }

    public void applyTo(LivingEntity entity) {
        for (StatusEffectInstance effect : effects) {
            entity.addStatusEffect(new StatusEffectInstance(effect));
        }
    }
}
