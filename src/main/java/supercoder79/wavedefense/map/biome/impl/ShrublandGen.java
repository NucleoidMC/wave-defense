package supercoder79.wavedefense.map.biome.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import supercoder79.wavedefense.map.biome.BiomeGen;

public final class ShrublandGen implements BiomeGen {
	public static final ShrublandGen INSTANCE = new ShrublandGen();

	@Override
	public double upperNoiseFactor() {
		return 8;
	}

	@Override
	public double lowerNoiseFactor() {
		return 8;
	}

	@Override
	public double detailFactor() {
		return 2.15;
	}

	@Override
	public int treeAmt(RandomSource random) {
		return random.nextInt(16) == 0 ? 1 : 0;
	}

	@Override
	public int grassAmt(RandomSource random) {
		return 8 + random.nextInt(8);
	}

	@Override
	public int shrubAmt(RandomSource random) {
		return random.nextInt(3) + 1;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.SAVANNA;
	}
}
