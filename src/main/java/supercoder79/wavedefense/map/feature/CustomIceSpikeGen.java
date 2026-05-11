package supercoder79.wavedefense.map.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.substrate.gen.MapGen;

public class CustomIceSpikeGen implements MapGen {
    public static final CustomIceSpikeGen INSTANCE = new CustomIceSpikeGen();

    @Override
    public void generate(ServerLevelAccessor world, BlockPos blockPos, RandomSource random) {
        BlockPos.MutableBlockPos mutable = blockPos.mutable().move(0, -6, 0);

        if (world.getBlockState(blockPos.below()).getBlock() != Blocks.GRASS_BLOCK)
            return;

        int spikeHeight = random.nextInt(32) + 8;

        for (int i = 0; i < 8; i++)
            spikeHeight += random.nextInt(4) == 0 ? random.nextInt(8) + 4 : 0;

        BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();

        for (int y = 0; y < spikeHeight; y++) {
            double radius = random.nextDouble() * 1.2d + 0.5d;
            radius += (y % (random.nextInt(4) + 2) == 0 ? random.nextDouble() / 1.6d : 0) + (y % (random.nextInt(4) + 2) == 0 ? random.nextDouble() / 1.6d : 0) + (y < 8 ? (8 - y) / 4d : 0);
            radius -= y > spikeHeight - 4 ? (y - spikeHeight + 4) : 0;
            radius += spikeHeight > 40 ? 0.2d : 0;
            radius += spikeHeight > 60 ? 0.2d : 0;
            radius += spikeHeight > 80 ? 0.2d : 0;
            radius = Math.max(0.4d, radius);

            for (int x = -(int) radius; x <= (int) radius; x++) {
                for (int z = -(int) radius; z <= (int) radius; z++) {
                    if (x * x + z * z <= radius * radius) {
                        BlockPos icePos = mutable.offset(x, y, z);
                        world.setBlock(icePos, PACKED_ICE, 3);
                    }
                }
            }
        }
    }
}
