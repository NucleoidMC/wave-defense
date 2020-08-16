package supercoder79.wavedefense.map.biome;

import java.util.Random;

import xyz.nucleoid.plasmid.game.gen.MapGen;
import xyz.nucleoid.plasmid.game.gen.feature.PoplarTreeGen;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

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

	default MapGen tree(int x, int z, Random random) {
		return PoplarTreeGen.INSTANCE;
	}

	RegistryKey<Biome> getFakingBiome();
}
