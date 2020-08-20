package supercoder79.wavedefense.game;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.border.WorldBorder;
import xyz.nucleoid.plasmid.game.player.PlayerSet;

public final class WdBeacon implements PlayerSet.Listener {
    private final WdActive game;

    private BlockPos pos;
    private BlockState lastState;
    private BlockState lastGroundState;

    WdBeacon(WdActive game) {
        this.game = game;

        PlayerSet players = game.world.getPlayerSet();
        players.addListener(this);

        for (ServerPlayerEntity player : players) {
            this.onAddPlayer(player);
        }
    }

    public void moveTo(int x, int z) {
        if (this.pos != null && x == this.pos.getX() && z == this.pos.getZ()) {
            return;
        }

        ServerWorld world = game.world.getWorld();
        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(x, 0, z));

        if (this.pos != null) {
            world.setBlockState(this.pos, this.lastState);
            world.setBlockState(this.pos.down(), this.lastGroundState);
        }

        this.pos = pos;
        this.lastState = world.getBlockState(pos);
        this.lastGroundState = world.getBlockState(pos.down());

        world.setBlockState(pos, Blocks.BEACON.getDefaultState());

        this.sendWorldBorder();
    }

    @Override
    public void onAddPlayer(ServerPlayerEntity player) {
        WorldBorder worldBorder = getWorldBorder();
        player.networkHandler.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INITIALIZE));
    }

    private void sendWorldBorder() {
        WorldBorder worldBorder = getWorldBorder();
        PlayerSet players = game.world.getPlayerSet();
        players.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_CENTER));
    }

    private WorldBorder getWorldBorder() {
        WorldBorder worldBorder = new WorldBorder();

        int x = 0;
        int z = 0;
        if (pos != null) {
            x = pos.getX();
            z = pos.getZ();
        }

        worldBorder.setCenter(x + 0.5, z + 0.5);
        worldBorder.setSize(0.5);
        worldBorder.setDamagePerBlock(0.0);
        worldBorder.setWarningBlocks(-100000);
        worldBorder.setWarningTime(-100000);

        return worldBorder;
    }
}
