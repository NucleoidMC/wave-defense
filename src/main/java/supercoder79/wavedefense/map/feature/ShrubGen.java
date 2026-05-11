package supercoder79.wavedefense.map.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xyz.nucleoid.substrate.gen.MapGen;

public final class ShrubGen implements MapGen {
    public static final ShrubGen INSTANCE = new ShrubGen();
    private static final BlockState LOG = Blocks.OAK_LOG.defaultBlockState();
    private static final BlockState LEAVES = Blocks.OAK_LEAVES.defaultBlockState().setValue(BlockStateProperties.DISTANCE, 1);

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        if (world.getBlockState(pos.below()) != Blocks.GRASS_BLOCK.defaultBlockState()
                || world.getBlockState(pos.offset(1, -1, 1)) != Blocks.GRASS_BLOCK.defaultBlockState()
                || world.getBlockState(pos.offset(1, -1, -1)) != Blocks.GRASS_BLOCK.defaultBlockState()
                || world.getBlockState(pos.offset(-1, -1, 1)) != Blocks.GRASS_BLOCK.defaultBlockState()
                || world.getBlockState(pos.offset(-1, -1, -1)) != Blocks.GRASS_BLOCK.defaultBlockState()) return;

        world.setBlock(pos, LOG, 3);

        if (random.nextBoolean()) {
            pos = pos.above();
            world.setBlock(pos, LOG, 3);
        }

        for (Direction dir : Direction.values()) {
            BlockPos local = pos.relative(dir);

            if (world.getBlockState(local).isAir()) {
                world.setBlock(local, LEAVES, 3);
            }
        }
    }
}
