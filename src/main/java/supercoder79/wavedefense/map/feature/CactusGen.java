package supercoder79.wavedefense.map.feature;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import xyz.nucleoid.substrate.gen.GenHelper;
import xyz.nucleoid.substrate.gen.MapGen;

public final class CactusGen implements MapGen {
    public static final CactusGen INSTANCE = new CactusGen();

    public void generate(ServerWorldAccess world, BlockPos pos, Random random) {
        for(int i = 0; i < 16; ++i) {
            int aX = random.nextInt(8) - random.nextInt(8);
            int aY = random.nextInt(8) - random.nextInt(8);
            int aZ = random.nextInt(8) - random.nextInt(8);
            BlockPos local = pos.add(aX, aY, aZ);

            boolean canGenerate = true;
            for (Direction dir : GenHelper.HORIZONTALS) {
                if (!world.getBlockState(local.offset(dir)).isAir()) {
                    canGenerate = false;
                    break;
                }
            }

            if (canGenerate && (world.getBlockState(local.down()) == Blocks.SAND.getDefaultState() || world.getBlockState(local.down()) == Blocks.CACTUS.getDefaultState()) && world.getBlockState(local).isAir()) {
                world.setBlockState(local, Blocks.CACTUS.getDefaultState(), 3);
            }
        }
    }
}