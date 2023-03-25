package supercoder79.wavedefense.map.biome;

import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import supercoder79.wavedefense.map.biome.impl.*;

import net.minecraft.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public final class FakeBiomeSource extends BiomeSource {
	public static final Codec<FakeBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryCodecs.createRegistryCodec(RegistryKeys.BIOME, Lifecycle.stable(), Biome.CODEC).fieldOf("biomes").forGetter(source -> source.biomeRegistry),
			Codec.LONG.fieldOf("seed").stable().forGetter(source -> source.seed))
			.apply(instance, instance.stable(FakeBiomeSource::new)));

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
	protected Stream<RegistryEntry<Biome>> biomeStream() {
		return this.biomeRegistry.streamEntries().map(Function.identity());
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
		return biomeRegistry.getEntry(getRealBiome(x << 2,z << 2).getFakingBiome()).get();
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
