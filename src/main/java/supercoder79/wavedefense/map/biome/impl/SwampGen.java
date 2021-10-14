package supercoder79.wavedefense.map.biome.impl;

import java.util.Random;

import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.feature.SwampTreeGen;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import xyz.nucleoid.substrate.gen.MapGen;

public final class SwampGen implements BiomeGen {
	public static final SwampGen INSTANCE = new SwampGen();

	@Override
	public double lowerNoiseFactor() {
		return 18;
	}

	@Override
	public double detailFactor() {
		return 1.25;
	}

	@Override
	public int treeAmt(Random random) {
		return 1 + random.nextInt(2);
	}

	@Override
	public int grassAmt(Random random) {
		return 3 + random.nextInt(8);
	}

	@Override
	public int shrubAmt(Random random) {
		return 2 + random.nextInt(3);
	}

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BiomeKeys.SWAMP;
	}

	@Override
	public MapGen tree(int x, int z, Random random) {
		return SwampTreeGen.INSTANCE;
	}
}
