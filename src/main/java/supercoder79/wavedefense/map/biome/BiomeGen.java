package supercoder79.wavedefense.map.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.tree.PoplarTreeGen;

public interface BiomeGen {
	default double upperNoiseFactor() {
		return 14;
	}

	default double lowerNoiseFactor() {
		return 12;
	}

	default double detailFactor() {
		return 3.25;
	}

	int treeAmt(RandomSource random);

	int grassAmt(RandomSource random);

	default int shrubAmt(RandomSource random) {
		return 0;
	}

	default int cactusAmt(RandomSource random) {
		return 0;
	}

	default BlockState topState(RandomSource random) {
		return Blocks.GRASS_BLOCK.defaultBlockState();
	}

	default BlockState pathState() {
		return Blocks.DIRT_PATH.defaultBlockState();
	}

	default BlockState underState() {
		return Blocks.DIRT.defaultBlockState();
	}

	default BlockState underWaterState() {
		return Blocks.DIRT.defaultBlockState();
	}

	default MapGen tree(int x, int z, RandomSource random) {
		return PoplarTreeGen.INSTANCE;
	}

	default boolean isSnowy() { return false; }

	ResourceKey<Biome> getFakingBiome();
}
