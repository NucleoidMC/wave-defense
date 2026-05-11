package supercoder79.wavedefense.map.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import xyz.nucleoid.substrate.gen.GenHelper;
import xyz.nucleoid.substrate.gen.MapGen;

public final class CactusGen implements MapGen {
    public static final CactusGen INSTANCE = new CactusGen();

    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        for(int i = 0; i < 16; ++i) {
            int aX = random.nextInt(8) - random.nextInt(8);
            int aY = random.nextInt(8) - random.nextInt(8);
            int aZ = random.nextInt(8) - random.nextInt(8);
            BlockPos local = pos.offset(aX, aY, aZ);

            boolean canGenerate = true;
            for (Direction dir : GenHelper.HORIZONTALS) {
                if (!world.getBlockState(local.relative(dir)).isAir()) {
                    canGenerate = false;
                    break;
                }
            }

            if (canGenerate && (world.getBlockState(local.below()) == Blocks.SAND.defaultBlockState() || world.getBlockState(local.below()) == Blocks.CACTUS.defaultBlockState()) && world.getBlockState(local).isAir()) {
                world.setBlock(local, Blocks.CACTUS.defaultBlockState(), 3);
            }
        }
    }
}