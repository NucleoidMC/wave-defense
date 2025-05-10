package supercoder79.wavedefense.game;

import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;

import java.util.Set;

public record WdSpawnLogic(ServerWorld world, WdConfig config) {

    public void resetPlayer(ServerPlayerEntity player, GameMode gameMode) {
        player.getInventory().clear();
        player.getEnderChestInventory().clear();
        player.clearStatusEffects();
        player.setHealth(20.0F);
        player.getHungerManager().setFoodLevel(20);
        player.getHungerManager().add(5, 0.5F);
        player.fallDistance = 0.0F;
        player.changeGameMode(gameMode);
        player.setExperienceLevel(0);
        player.setExperiencePoints(0);
    }

    private static final ChunkTicketType FORCE_TELEPORT = ChunkTicketType.PLAYER_LOADING;

    public void spawnPlayer(ServerPlayerEntity player) {
        BlockPos pos = findSurfaceAround(Vec3d.ZERO, this.world, this.config);
        ChunkPos chunkPos = new ChunkPos(pos);
        world.getChunkManager().addTicket(new ChunkTicket(FORCE_TELEPORT, 3), chunkPos);

        player.teleport(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Set.of(), 0.0F, 0.0F, false);
    }

    public static BlockPos findSurfaceAround(Vec3d centerPos, ServerWorld world, WdConfig config) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        while (true) {
            Random random = world.getRandom();
            double x = centerPos.x + random.nextInt(config.spawnRadius) - random.nextInt(config.spawnRadius);
            double z = centerPos.z + random.nextInt(config.spawnRadius) - random.nextInt(config.spawnRadius);
            mutablePos.set(x, 0, z);

            world.getChunk(mutablePos);
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutablePos.getX(), mutablePos.getZ());
            mutablePos.setY(topY - 1);

            BlockState ground = world.getBlockState(mutablePos);
            if (ground.isIn(BlockTags.LEAVES)) {
                continue;
            }

            mutablePos.move(Direction.UP);
            return mutablePos.toImmutable();
        }
    }

    public static BlockPos findSurfaceAt(int x, int z, int offset, ServerWorld world) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        while (true) {
            Random random = world.getRandom();
            mutablePos.set(x + (random.nextInt(offset) - random.nextInt(offset)), 0, z + (random.nextInt(offset) - random.nextInt(offset)));

            world.getChunk(mutablePos);
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutablePos.getX(), mutablePos.getZ());
            mutablePos.setY(topY - 1);

            BlockState ground = world.getBlockState(mutablePos);
            if (ground.isIn(BlockTags.LEAVES)) {
                continue;
            }

            mutablePos.move(Direction.UP);
            return mutablePos.toImmutable();
        }
    }
}
