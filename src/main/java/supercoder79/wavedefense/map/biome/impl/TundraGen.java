package supercoder79.wavedefense.map.biome.impl;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.feature.SnowyTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;

import java.util.Random;

public final class TundraGen implements BiomeGen {
	public static final TundraGen INSTANCE = new TundraGen();

	@Override
	public double detailFactor() {
		return 3.85;
	}

	@Override
	public int treeAmt(Random random) {
		return random.nextInt(5) == 0 ? 1 : 0;
	}

	@Override
	public int grassAmt(Random random) {
		return 0;
	}

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BiomeKeys.SNOWY_TUNDRA;
	}

	@Override
	public MapGen tree(int x, int z, Random random) {
		return SnowyTreeGen.INSTANCE;
	}

	@Override
	public BlockState pathState() {
		return Blocks.PACKED_ICE.getDefaultState();
	}

	@Override
	public boolean isSnowy() {
		return true;
	}
}
