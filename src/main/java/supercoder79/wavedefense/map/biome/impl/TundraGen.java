package supercoder79.wavedefense.map.biome.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.feature.SnowyTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;

public final class TundraGen implements BiomeGen {
	public static final TundraGen INSTANCE = new TundraGen();

	@Override
	public double detailFactor() {
		return 3.85;
	}

	@Override
	public int treeAmt(RandomSource random) {
		return random.nextInt(5) == 0 ? 1 : 0;
	}

	@Override
	public int grassAmt(RandomSource random) {
		return 0;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.SNOWY_PLAINS;
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return SnowyTreeGen.INSTANCE;
	}

	@Override
	public BlockState pathState() {
		return Blocks.PACKED_ICE.defaultBlockState();
	}

	@Override
	public boolean isSnowy() {
		return true;
	}
}
