package supercoder79.wavedefense.map.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.substrate.gen.MapGen;

public final class ImprovedDiskGen implements MapGen {
	public static final ImprovedDiskGen INSTANCE = new ImprovedDiskGen();

	private static final BlockState[] STATES = new BlockState[]{ Blocks.SAND.defaultBlockState(), Blocks.GRAVEL.defaultBlockState() };

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {

		int radius = random.nextInt(5) + 2;
		int radiusSquared = radius * radius;

		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		BlockState state = STATES[random.nextInt(2)];

		for(int x = pos.getX() - radius; x <= pos.getX() + radius; ++x) {
			for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z) {
				int localX = x - pos.getX();
				int localZ = z - pos.getZ();
				if (localX * localX + localZ * localZ <= radiusSquared) {
					for(int y = pos.getY() - 2; y <= pos.getY() + 2; ++y) {
						mutable.set(x, y, z);

						if (world.getBlockState(mutable).is(Blocks.DIRT) || world.getBlockState(mutable).is(Blocks.GRASS_BLOCK)) {
							world.setBlock(mutable, state, 3);

							if (!world.getBlockState(mutable.above()).canSurvive(world, mutable) && !world.getBlockState(mutable.above()).is(Blocks.DIRT_PATH)) {
								world.setBlock(mutable.above(), Blocks.AIR.defaultBlockState(), 3);
							}
						}
					}
				}
			}
		}
	}
}