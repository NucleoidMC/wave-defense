package supercoder79.wavedefense.map.biome.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.feature.DeadTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;

public final class DesertGen implements BiomeGen {
	public static final DesertGen INSTANCE = new DesertGen();

	@Override
	public double upperNoiseFactor() {
		return 10;
	}

	@Override
	public double lowerNoiseFactor() {
		return 12;
	}

	@Override
	public double detailFactor() {
		return 4.25;
	}

	@Override
	public int treeAmt(RandomSource random) {
		if (random.nextInt(6) == 0) {
			return 1;
		}
		return 0;
	}

	@Override
	public int grassAmt(RandomSource random) {
		return random.nextInt(2);
	}

	@Override
	public int cactusAmt(RandomSource random) {
		return 1 + random.nextInt(3);
	}

	@Override
	public BlockState topState(RandomSource random) {
		if (random.nextInt(16) == 0) {
			return Blocks.GRASS_BLOCK.defaultBlockState();
		}

		return Blocks.SAND.defaultBlockState();
	}

	@Override
	public BlockState pathState() {
		return Blocks.RED_SANDSTONE.defaultBlockState();
	}

	@Override
	public BlockState underState() {
		return Blocks.SANDSTONE.defaultBlockState();
	}

	@Override
	public BlockState underWaterState() {
		return Blocks.SAND.defaultBlockState();
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return DeadTreeGen.INSTANCE;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.DESERT;
	}
}
