package supercoder79.wavedefense.entity.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class EnemyConfig {
    public static final Codec<EnemyConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                EnemyClass.CODEC.fieldOf("class").forGetter(config -> config.type),
                Codec.STRING.fieldOf("name").forGetter(config -> config.name),
                Codec.INT.fieldOf("reward").forGetter(config -> config.reward),
                Codec.DOUBLE.fieldOf("danger").forGetter(config -> config.danger)
        ).apply(instance, EnemyConfig::new);
    });

    public final EnemyClass type;
    public final String name;
    public final int reward;
    public final double danger;

    public EnemyConfig(EnemyClass type, String name, int reward, double danger) {
        this.name = name;
        this.type = type;
        this.reward = reward;
        this.danger = danger;
    }
}
