package supercoder79.wavedefense.map.biome.impl;

import java.util.Random;

import supercoder79.wavedefense.map.biome.BiomeGen;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;

public final class ForestGen implements BiomeGen {
	public static final ForestGen INSTANCE = new ForestGen();

	@Override
	public double detailFactor() {
		return 3.85;
	}

	@Override
	public int treeAmt(Random random) {
		return 3 + random.nextInt(4);
	}

	@Override
	public int grassAmt(Random random) {
		return 4 + random.nextInt(4);
	}

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BiomeKeys.FOREST;
	}
}
