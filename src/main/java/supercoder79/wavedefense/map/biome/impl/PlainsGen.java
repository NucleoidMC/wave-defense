package supercoder79.wavedefense.map.biome.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import supercoder79.wavedefense.map.biome.BiomeGen;

public final class PlainsGen implements BiomeGen {
	public static final PlainsGen INSTANCE = new PlainsGen();

	@Override
	public double upperNoiseFactor() {
		return 10;
	}

	@Override
	public double lowerNoiseFactor() {
		return 12;
	}

	@Override
	public int treeAmt(RandomSource random) {
		return random.nextInt(2);
	}

	@Override
	public int grassAmt(RandomSource random) {
		return 8 + random.nextInt(4);
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.PLAINS;
	}
}
