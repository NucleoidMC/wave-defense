package supercoder79.wavedefense.map.biome;

import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import supercoder79.wavedefense.map.biome.impl.*;

public final class FakeBiomeSource extends BiomeSource {
	public static final MapCodec<FakeBiomeSource> CODEC = MapCodec.unit(() -> null);

	private final Registry<Biome> biomeRegistry;
	private final long seed;

	private final OpenSimplexNoise temperatureNoise;
	private final OpenSimplexNoise rainfallNoise;
	private final OpenSimplexNoise roughnessNoise;

	public FakeBiomeSource(Registry<Biome> biomeRegistry, long seed) {
		this.biomeRegistry = biomeRegistry;
		this.seed = seed;

		temperatureNoise = new OpenSimplexNoise(seed + 79);
		rainfallNoise = new OpenSimplexNoise(seed - 79);
		roughnessNoise = new OpenSimplexNoise(seed);
	}

	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return this.biomeRegistry.listElements().map(Function.identity());
	}

	@Override
	protected MapCodec<? extends BiomeSource> codec() {
		return CODEC;
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler noise) {
		return biomeRegistry.getOrThrow(getRealBiome(x << 2,z << 2).getFakingBiome());
	}

	public BiomeGen getRealBiome(int x, int z) {
		double temperature = (temperatureNoise.eval(x / 320.0, z / 320.0) + 1) / 2;
		temperature = temperature * 0.9 + (((roughnessNoise.eval(x / 72.0, z / 72.0) + 1) / 2) * 0.1);

		double rainfall = (rainfallNoise.eval(x / 320.0, z / 320.0) + 1) / 2;

		if (temperature > 0.85) {
			return DesertGen.INSTANCE;
		} else if (temperature > 0.575) {
			if (rainfall < 0.35) {
				return DesertGen.INSTANCE;
			} else if (rainfall < 0.5) {
				return ShrublandGen.INSTANCE;
			} else if (rainfall > 0.6) {
				return SwampGen.INSTANCE;
			} else {
				return PlainsGen.INSTANCE;
			}
		} else if (temperature > 0.4) {
			if (rainfall > 0.575) {
				return TaigaGen.INSTANCE;
			} else {
				return ForestGen.INSTANCE;
			}
		} else {
			return TundraGen.INSTANCE;
		}
	}
}
