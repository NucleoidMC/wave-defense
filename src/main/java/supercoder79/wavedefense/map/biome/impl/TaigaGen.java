package supercoder79.wavedefense.map.biome.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.feature.SprucePoplarTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;

public final class TaigaGen implements BiomeGen {
	public static final TaigaGen INSTANCE = new TaigaGen();

	@Override
	public double detailFactor() {
		return 3.85;
	}

	@Override
	public int treeAmt(RandomSource random) {
		return 2 + random.nextInt(2);
	}

	@Override
	public int grassAmt(RandomSource random) {
		return 4 + random.nextInt(4);
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.TAIGA;
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return SprucePoplarTreeGen.INSTANCE;
	}
}
