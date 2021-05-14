package supercoder79.wavedefense.map.feature;

import java.util.Random;

import xyz.nucleoid.plasmid.game.gen.MapGen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;

public final class ShrubGen implements MapGen {
    public static final ShrubGen INSTANCE = new ShrubGen();
    private static final BlockState LOG = Blocks.OAK_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.OAK_LEAVES.getDefaultState().with(Properties.DISTANCE_1_7, 1);

    @Override
    public void generate(ServerWorldAccess world, BlockPos pos, Random random) {
        if (world.getBlockState(pos.down()) != Blocks.GRASS_BLOCK.getDefaultState()
                || world.getBlockState(pos.add(1, -1, 1)) != Blocks.GRASS_BLOCK.getDefaultState()
                || world.getBlockState(pos.add(1, -1, -1)) != Blocks.GRASS_BLOCK.getDefaultState()
                || world.getBlockState(pos.add(-1, -1, 1)) != Blocks.GRASS_BLOCK.getDefaultState()
                || world.getBlockState(pos.add(-1, -1, -1)) != Blocks.GRASS_BLOCK.getDefaultState()) return;

        world.setBlockState(pos, LOG, 3);

        if (random.nextBoolean()) {
            pos = pos.up();
            world.setBlockState(pos, LOG, 3);
        }

        for (Direction dir : Direction.values()) {
            BlockPos local = pos.offset(dir);

            if (world.getBlockState(local).isAir()) {
                world.setBlockState(local, LEAVES, 3);
            }
        }
    }
}
