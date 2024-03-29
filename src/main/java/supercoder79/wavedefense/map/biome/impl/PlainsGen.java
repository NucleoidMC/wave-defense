package supercoder79.wavedefense.map.biome.impl;

import supercoder79.wavedefense.map.biome.BiomeGen;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;

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
	public int treeAmt(Random random) {
		return random.nextInt(2);
	}

	@Override
	public int grassAmt(Random random) {
		return 8 + random.nextInt(4);
	}

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BiomeKeys.PLAINS;
	}
}
