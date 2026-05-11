package supercoder79.wavedefense.map.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xyz.nucleoid.substrate.gen.GenHelper;
import xyz.nucleoid.substrate.gen.MapGen;

public final class SnowyTreeGen implements MapGen {
    public static final SnowyTreeGen INSTANCE = new SnowyTreeGen();
    private static final BlockState LOG = Blocks.SPRUCE_LOG.defaultBlockState();
    private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(BlockStateProperties.DISTANCE, 1);

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        if (world.getBlockState(pos.below()).getBlock() != Blocks.GRASS_BLOCK) return;

        int leafDistance = random.nextInt(3) + 3;

        BlockPos.MutableBlockPos mutable = pos.mutable();
        for (int y = 0; y < 16; y++) {
            world.setBlock(mutable, LOG, 3);
            mutable.move(Direction.UP);
        }

        mutable = pos.mutable();
        mutable.move(Direction.UP, leafDistance);

        for (int y = 15; y >= 0; y--) {
            double radius = Math.max(0.7d, 4 + (16 - y) % 2 - (16 - y) / 4f);
            GenHelper.circle(mutable.mutable(), radius, leafPos -> {
                if (world.getBlockState(leafPos).isAir()) {
                    world.setBlock(leafPos, LEAVES, 3);
                }
            });
            mutable.move(Direction.UP);
        }

        for (int y = 15; y >= 0; y--) {
            for (int x = -6; x <= 6; x++) {
                for (int z = -6; z <= 6; z++) {
                    BlockPos snowPos = mutable.offset(x, -15, z);
                    if (world.getBlockState(snowPos).isAir() && world.getBlockState(snowPos.below()).equals(LEAVES))
                        world.setBlock(snowPos, Blocks.SNOW.defaultBlockState().setValue(BlockStateProperties.LAYERS, random.nextInt(3) + 1), 3);
                }
            }
            mutable.move(Direction.UP);
        }
    }
}