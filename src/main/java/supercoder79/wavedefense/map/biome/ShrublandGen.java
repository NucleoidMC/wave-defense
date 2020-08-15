package supercoder79.wavedefense.map.biome;

import java.util.Random;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;

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
	public int treeAmt(Random random) {
		return random.nextInt(16) == 0 ? 1 : 0;
	}

	@Override
	public int grassAmt(Random random) {
		return 8 + random.nextInt(8);
	}

	@Override
	public int shrubAmt(Random random) {
		return random.nextInt(3) + 1;
	}

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BuiltinBiomes.SAVANNA;
	}
}
