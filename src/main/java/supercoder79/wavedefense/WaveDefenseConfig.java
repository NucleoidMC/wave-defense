package supercoder79.wavedefense;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public class WaveDefenseConfig {
	public static final Codec<WaveDefenseConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
			Codec.INT.fieldOf("border_size").forGetter(config -> config.borderSize)
	).apply(instance, WaveDefenseConfig::new));

	public final PlayerConfig playerConfig;
	public final int borderSize;

	public WaveDefenseConfig(PlayerConfig playerConfig, int borderSize) {
		this.playerConfig = playerConfig;
		this.borderSize = borderSize;
	}
}