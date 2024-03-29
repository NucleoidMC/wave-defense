package supercoder79.wavedefense.map.biome.impl;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
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
	public int treeAmt(Random random) {
		if (random.nextInt(6) == 0) {
			return 1;
		}
		return 0;
	}

	@Override
	public int grassAmt(Random random) {
		return random.nextInt(2);
	}

	@Override
	public int cactusAmt(Random random) {
		return 1 + random.nextInt(3);
	}

	@Override
	public BlockState topState(Random random) {
		if (random.nextInt(16) == 0) {
			return Blocks.GRASS_BLOCK.getDefaultState();
		}

		return Blocks.SAND.getDefaultState();
	}

	@Override
	public BlockState pathState() {
		return Blocks.RED_SANDSTONE.getDefaultState();
	}

	@Override
	public BlockState underState() {
		return Blocks.SANDSTONE.getDefaultState();
	}

	@Override
	public BlockState underWaterState() {
		return Blocks.SAND.getDefaultState();
	}

	@Override
	public MapGen tree(int x, int z, Random random) {
		return DeadTreeGen.INSTANCE;
	}

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BiomeKeys.DESERT;
	}
}
