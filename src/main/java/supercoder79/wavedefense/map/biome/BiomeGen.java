package supercoder79.wavedefense.map.biome;

import java.util.Random;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public interface BiomeGen {
	int treeAmt(Random random);

	int grassAmt(Random random);

	default int shrubAmt(Random random) {
		return 0;
	};

	RegistryKey<Biome> getFakingBiome();
}
