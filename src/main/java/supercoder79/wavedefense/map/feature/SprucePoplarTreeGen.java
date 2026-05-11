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

public final class SprucePoplarTreeGen implements MapGen {
    public static final SprucePoplarTreeGen INSTANCE = new SprucePoplarTreeGen();
    private static final BlockState LOG = Blocks.SPRUCE_LOG.defaultBlockState();
    private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(BlockStateProperties.DISTANCE, 1);

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        if (world.getBlockState(pos.below()) != Blocks.GRASS_BLOCK.defaultBlockState()) return;

        double maxRadius = 2.6 + ((random.nextDouble() - 0.5) * 0.2);
        int leafDistance = random.nextInt(3) + 3;

        BlockPos.MutableBlockPos mutable = pos.mutable();
        for (int y = 0; y < 12; y++) {
            world.setBlock(mutable, LOG, 3);
            //add branch blocks
            if (maxRadius * radius(y / 11.f) > 2.3) {
                Direction.Axis axis = getAxis(random);
                world.setBlock(mutable.relative(getDirection(axis, random)).above(leafDistance), LOG.setValue(BlockStateProperties.AXIS, axis), 3);
            }

            mutable.move(Direction.UP);
        }

        mutable = pos.mutable();
        mutable.move(Direction.UP, leafDistance);

        for (int y = 0; y < 12; y++) {
            GenHelper.circle(mutable.mutable(), maxRadius * radius(y / 11.f) + random.nextInt(3) - 1, leafPos -> {
                if (world.getBlockState(leafPos).isAir()) {
                    world.setBlock(leafPos, LEAVES, 3);
                }
            });
            mutable.move(Direction.UP);
        }
    }

    private double radius(double x) {
        return (-2 * (x * x * x)) + (1.9 * x) + 0.2;
    }

    private Direction.Axis getAxis(RandomSource random) {
        return random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
    }

    private Direction getDirection(Direction.Axis axis, RandomSource random) {
        if (axis == Direction.Axis.X) {
            return random.nextBoolean() ? Direction.EAST : Direction.WEST;
        } else {
            return random.nextBoolean() ? Direction.NORTH : Direction.SOUTH;
        }
    }
}