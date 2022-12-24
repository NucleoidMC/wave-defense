package supercoder79.wavedefense.map.feature;

import supercoder79.wavedefense.map.gen.WdPath;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import xyz.nucleoid.substrate.gen.MapGen;

public final class ChestGen implements MapGen {
	private final WdPath path;

	public ChestGen(WdPath path) {
		this.path = path;
	}

	@Override
	public void generate(ServerWorldAccess world, BlockPos pos, Random random) {
		double dist = path.distanceToPath2(pos.getX(), pos.getZ());
		if (dist > 8 * 8 && dist <= 16 * 16) {
			if (world.getFluidState(pos).isEmpty()) {
				world.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState(), 3);
				world.setBlockState(pos.up(), Blocks.CHEST.getDefaultState(), 3);
			}
		}
	}
}
