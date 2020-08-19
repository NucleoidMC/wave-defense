package supercoder79.wavedefense.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public final class WaveDefenseConfig {
	public static final Codec<WaveDefenseConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
			Path.CODEC.fieldOf("path").forGetter(config -> config.path),
			Codec.INT.fieldOf("spawn_radius").forGetter(config -> config.spawnRadius),
			Codec.DOUBLE.fieldOf("min_wave_spacing").forGetter(config -> config.minWaveSpacing),
			Codec.DOUBLE.fieldOf("max_wave_spacing").forGetter(config -> config.maxWaveSpacing)
			).apply(instance, WaveDefenseConfig::new));

	public final PlayerConfig playerConfig;
	public final Path path;
	public final int spawnRadius;

	public final double minWaveSpacing;
	public final double maxWaveSpacing;

	public WaveDefenseConfig(PlayerConfig playerConfig, Path path, int spawnRadius, double minWaveSpacing, double maxWaveSpacing) {
		this.playerConfig = playerConfig;
		this.path = path;
		this.spawnRadius = spawnRadius;
		this.minWaveSpacing = minWaveSpacing;
		this.maxWaveSpacing = maxWaveSpacing;
	}

	public static final class Path {
		public static final Codec<Path> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.fieldOf("length").forGetter(config -> config.length),
				Codec.INT.fieldOf("segment_length").forGetter(config -> config.segmentLength),
				Codec.DOUBLE.fieldOf("radius").forGetter(config -> config.radius)
		).apply(instance, Path::new));

		public final int length;
		public final int segmentLength;
		public final double radius;

		public Path(int length, int segmentLength, double radius) {
			this.length = length;
			this.segmentLength = segmentLength;
			this.radius = radius;
		}
	}
}
