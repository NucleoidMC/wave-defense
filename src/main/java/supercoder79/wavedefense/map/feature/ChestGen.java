package supercoder79.wavedefense.map.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import supercoder79.wavedefense.map.gen.WdPath;
import xyz.nucleoid.substrate.gen.MapGen;

public final class ChestGen implements MapGen {
	private final WdPath path;

	public ChestGen(WdPath path) {
		this.path = path;
	}

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
		double dist = path.distanceToPath2(pos.getX(), pos.getZ());
		if (dist > 8 * 8 && dist <= 16 * 16) {
			if (world.getFluidState(pos).isEmpty()) {
				world.setBlock(pos, Blocks.GLOWSTONE.defaultBlockState(), 3);
				world.setBlock(pos.above(), Blocks.CHEST.defaultBlockState(), 3);
			}
		}
	}
}
