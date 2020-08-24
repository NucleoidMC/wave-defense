package supercoder79.wavedefense.game;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameWorld;

import java.util.Random;

public final class WdSpawnLogic {
    private final GameWorld world;
    private final WdConfig config;

    public WdSpawnLogic(GameWorld world, WdConfig config) {
        this.world = world;
        this.config = config;
    }

    public void resetPlayer(ServerPlayerEntity player, GameMode gameMode) {
        player.inventory.clear();
        player.getEnderChestInventory().clear();
        player.clearStatusEffects();
        player.setHealth(20.0F);
        player.getHungerManager().setFoodLevel(20);
        player.getHungerManager().add(5, 0.5F);
        player.fallDistance = 0.0F;
        player.setGameMode(gameMode);
        player.setExperienceLevel(0);
        player.setExperiencePoints(0);
    }

    public void spawnPlayer(ServerPlayerEntity player) {
        ServerWorld world = this.world.getWorld();

        BlockPos pos = findSurfaceAround(Vec3d.ZERO, this.world.getWorld(), this.config);
        ChunkPos chunkPos = new ChunkPos(pos);
        world.getChunkManager().addTicket(ChunkTicketType.field_19347, chunkPos, 1, player.getEntityId());

        player.teleport(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
    }

    @Nullable
    public static BlockPos findSurfaceAt(ServerWorld world, BlockPos pos) {
        BlockPos.Mutable mutablePos = pos.mutableCopy();

        int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ());
        mutablePos.setY(topY - 1);

        BlockState ground = world.getBlockState(mutablePos);
        if (ground.getBlock().isIn(BlockTags.LEAVES)) {
            return null;
        }

        mutablePos.move(Direction.UP);
        return mutablePos.toImmutable();
    }

    public static BlockPos findSurfaceAround(Vec3d centerPos, ServerWorld world, WdConfig config) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        while (true) {
            Random random = world.getRandom();
            double x = centerPos.x + random.nextInt(config.playRadius) - random.nextInt(config.playRadius);
            double z = centerPos.z + random.nextInt(config.playRadius) - random.nextInt(config.playRadius);
            mutablePos.set(x, 0, z);

            world.getChunk(mutablePos);

            BlockPos surface = findSurfaceAt(world, mutablePos);
            if (surface != null) {
                return surface;
            }
        }
    }
}
