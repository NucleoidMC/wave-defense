package supercoder79.wavedefense.map.biome.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import supercoder79.wavedefense.map.biome.BiomeGen;

public final class ForestGen implements BiomeGen {
	public static final ForestGen INSTANCE = new ForestGen();

	@Override
	public double detailFactor() {
		return 3.85;
	}

	@Override
	public int treeAmt(RandomSource random) {
		return 3 + random.nextInt(4);
	}

	@Override
	public int grassAmt(RandomSource random) {
		return 4 + random.nextInt(4);
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.FOREST;
	}
}
