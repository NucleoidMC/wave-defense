package supercoder79.wavedefense.game;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public final class WdBeacon {
    private final WdActive game;

    private BlockPos lastPos;
    private BlockState lastState;

    WdBeacon(WdActive game) {
        this.game = game;
    }

    public void moveTo(int x, int z) {
        if (this.lastPos != null && x == this.lastPos.getX() && z == this.lastPos.getZ()) {
            return;
        }

        ServerWorld world = game.world.getWorld();
        BlockPos surface = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(x, 0, z));

        if (lastPos != null) {
            world.setBlockState(lastPos, lastState);
        }

        lastPos = surface;
        lastState = world.getBlockState(surface);
        world.setBlockState(surface, Blocks.BEACON.getDefaultState());
    }
}
