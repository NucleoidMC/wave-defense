package supercoder79.wavedefense.map.biome;

import java.util.Random;

import supercoder79.wavedefense.map.feature.SprucePoplarTreeGen;
import xyz.nucleoid.plasmid.game.gen.MapGen;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;

public final class TaigaGen implements BiomeGen {
	public static final TaigaGen INSTANCE = new TaigaGen();

	@Override
	public double detailFactor() {
		return 3.85;
	}

	@Override
	public int treeAmt(Random random) {
		return 2 + random.nextInt(2);
	}

	@Override
	public int grassAmt(Random random) {
		return 4 + random.nextInt(4);
	}

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BuiltinBiomes.TAIGA;
	}

	@Override
	public MapGen tree(int x, int z, Random random) {
		return SprucePoplarTreeGen.INSTANCE;
	}
}
