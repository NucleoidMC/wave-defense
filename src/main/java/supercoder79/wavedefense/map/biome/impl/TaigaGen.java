package supercoder79.wavedefense.map.biome.impl;

import java.util.Random;

import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.feature.SprucePoplarTreeGen;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import xyz.nucleoid.substrate.gen.MapGen;

public final class TaigaGen implements BiomeGen {
	public static final TaigaGen INSTANCE = new TaigaGen();

	@Override
	public double detailFactor() {
		return 3.85;
	}

	@Override
	public int treeAmt(Random random) {
		return 2 + random.nextInt(2);
	}

	@Override
	public int grassAmt(Random random) {
		return 4 + random.nextInt(4);
	}

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BiomeKeys.TAIGA;
	}

	@Override
	public MapGen tree(int x, int z, Random random) {
		return SprucePoplarTreeGen.INSTANCE;
	}
}
