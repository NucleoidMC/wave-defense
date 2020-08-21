package supercoder79.wavedefense.map.biome;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import xyz.nucleoid.plasmid.game.gen.MapGen;
import xyz.nucleoid.plasmid.game.gen.feature.tree.PoplarTreeGen;

import java.util.Random;

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

	int treeAmt(Random random);

	int grassAmt(Random random);

	default int shrubAmt(Random random) {
		return 0;
	}

	default int cactusAmt(Random random) {
		return 0;
	}

	default BlockState topState(Random random) {
		return Blocks.GRASS_BLOCK.getDefaultState();
	}

	default BlockState pathState() {
		return Blocks.GRASS_PATH.getDefaultState();
	}

	default BlockState underState() {
		return Blocks.DIRT.getDefaultState();
	}

	default BlockState underWaterState() {
		return Blocks.DIRT.getDefaultState();
	}

	default MapGen tree(int x, int z, Random random) {
		return PoplarTreeGen.INSTANCE;
	}

	RegistryKey<Biome> getFakingBiome();
}
