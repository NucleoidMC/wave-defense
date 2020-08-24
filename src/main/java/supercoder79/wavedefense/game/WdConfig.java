package supercoder79.wavedefense.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import supercoder79.wavedefense.entity.config.EnemyConfig;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

import java.util.List;

public final class WdConfig {
	public static final Codec<WdConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
			Path.CODEC.fieldOf("path").forGetter(config -> config.path),
			Codec.INT.fieldOf("play_radius").forGetter(config -> config.playRadius),
			Danger.CODEC.fieldOf("danger").forGetter(config -> config.danger),
			Codec.DOUBLE.fieldOf("min_wave_spacing").forGetter(config -> config.minWaveSpacing),
			Codec.DOUBLE.fieldOf("max_wave_spacing").forGetter(config -> config.maxWaveSpacing),
			Codec.DOUBLE.fieldOf("spawn_radius").forGetter(config -> config.spawnRadius),
			EnemyConfig.CODEC.listOf().fieldOf("enemies").forGetter(config -> config.enemies)
	).apply(instance, WdConfig::new));

	public final PlayerConfig playerConfig;
	public final Path path;
	public final int playRadius;
	public final Danger danger;

	public final double minWaveSpacing;
	public final double maxWaveSpacing;

	public final double spawnRadius;

	public final List<EnemyConfig> enemies;

	public WdConfig(
			PlayerConfig playerConfig, Path path,
			int playRadius,
			Danger danger,
			double minWaveSpacing, double maxWaveSpacing,
			double spawnRadius,
			List<EnemyConfig> enemies
	) {
		this.playerConfig = playerConfig;
		this.path = path;
		this.playRadius = playRadius;
		this.danger = danger;
		this.minWaveSpacing = minWaveSpacing;
		this.maxWaveSpacing = maxWaveSpacing;
		this.spawnRadius = spawnRadius;
		this.enemies = enemies;
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


	public static final class Danger {
		public static final Codec<Danger> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.DOUBLE.fieldOf("idle_start").forGetter(config -> config.idleStart),
				Codec.DOUBLE.fieldOf("idle_end").forGetter(config -> config.idleEnd),
				Codec.DOUBLE.fieldOf("stray_scale").forGetter(config -> config.strayScale)
		).apply(instance, Danger::new));

		public final double idleStart;
		public final double idleEnd;
		public final double strayScale;

		public Danger(double idleStart, double idleEnd, double strayScale) {
			this.idleStart = idleStart;
			this.idleEnd = idleEnd;
			this.strayScale = strayScale;
		}
	}
}
