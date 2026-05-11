package supercoder79.wavedefense.map.biome.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.feature.SwampTreeGen;
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
	public int treeAmt(RandomSource random) {
		return 1 + random.nextInt(2);
	}

	@Override
	public int grassAmt(RandomSource random) {
		return 3 + random.nextInt(8);
	}

	@Override
	public int shrubAmt(RandomSource random) {
		return 2 + random.nextInt(3);
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.SWAMP;
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return SwampTreeGen.INSTANCE;
	}
}
