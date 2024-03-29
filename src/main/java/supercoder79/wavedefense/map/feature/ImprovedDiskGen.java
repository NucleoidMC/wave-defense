package supercoder79.wavedefense.map.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import xyz.nucleoid.substrate.gen.MapGen;

public final class ImprovedDiskGen implements MapGen {
	public static final ImprovedDiskGen INSTANCE = new ImprovedDiskGen();

	private static final BlockState[] STATES = new BlockState[]{ Blocks.SAND.getDefaultState(), Blocks.GRAVEL.getDefaultState() };

	@Override
	public void generate(ServerWorldAccess world, BlockPos pos, Random random) {

		int radius = random.nextInt(5) + 2;
		int radiusSquared = radius * radius;

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockState state = STATES[random.nextInt(2)];

		for(int x = pos.getX() - radius; x <= pos.getX() + radius; ++x) {
			for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z) {
				int localX = x - pos.getX();
				int localZ = z - pos.getZ();
				if (localX * localX + localZ * localZ <= radiusSquared) {
					for(int y = pos.getY() - 2; y <= pos.getY() + 2; ++y) {
						mutable.set(x, y, z);

						if (world.getBlockState(mutable).isOf(Blocks.DIRT) || world.getBlockState(mutable).isOf(Blocks.GRASS_BLOCK)) {
							world.setBlockState(mutable, state, 3);

							if (!world.getBlockState(mutable.up()).canPlaceAt(world, mutable) && !world.getBlockState(mutable.up()).isOf(Blocks.DIRT_PATH)) {
								world.setBlockState(mutable.up(), Blocks.AIR.getDefaultState(), 3);
							}
						}
					}
				}
			}
		}
	}
}