package supercoder79.wavedefense.map.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import xyz.nucleoid.substrate.gen.GenHelper;
import xyz.nucleoid.substrate.gen.MapGen;

public final class SnowyTreeGen implements MapGen {
    public static final SnowyTreeGen INSTANCE = new SnowyTreeGen();
    private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState().with(Properties.DISTANCE_1_7, 1);

    @Override
    public void generate(ServerWorldAccess world, BlockPos pos, Random random) {
        if (world.getBlockState(pos.down()).getBlock() != Blocks.GRASS_BLOCK) return;

        int leafDistance = random.nextInt(3) + 3;

        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int y = 0; y < 16; y++) {
            world.setBlockState(mutable, LOG, 3);
            mutable.move(Direction.UP);
        }

        mutable = pos.mutableCopy();
        mutable.move(Direction.UP, leafDistance);

        for (int y = 15; y >= 0; y--) {
            double radius = Math.max(0.7d, 4 + (16 - y) % 2 - (16 - y) / 4f);
            GenHelper.circle(mutable.mutableCopy(), radius, leafPos -> {
                if (world.getBlockState(leafPos).isAir()) {
                    world.setBlockState(leafPos, LEAVES, 3);
                }
            });
            mutable.move(Direction.UP);
        }

        for (int y = 15; y >= 0; y--) {
            for (int x = -6; x <= 6; x++) {
                for (int z = -6; z <= 6; z++) {
                    BlockPos snowPos = mutable.add(x, -15, z);
                    if (world.getBlockState(snowPos).isAir() && world.getBlockState(snowPos.down()).equals(LEAVES))
                        world.setBlockState(snowPos, Blocks.SNOW.getDefaultState().with(Properties.LAYERS, random.nextInt(3) + 1), 3);
                }
            }
            mutable.move(Direction.UP);
        }
    }
}