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

import xyz.nucleoid.plasmid.game.GameSpace;

import java.util.Random;

public final class WdSpawnLogic {
    private final GameSpace space;
    private final WdConfig config;

    public WdSpawnLogic(GameSpace space, WdConfig config) {
        this.space = space;
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
        ServerWorld world = this.space.getWorld();

        BlockPos pos = findSurfaceAround(Vec3d.ZERO, this.space.getWorld(), this.config);
        ChunkPos chunkPos = new ChunkPos(pos);
        world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getEntityId());

        player.teleport(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
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
            if (ground.getBlock().isIn(BlockTags.LEAVES)) {
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
            if (ground.getBlock().isIn(BlockTags.LEAVES)) {
                continue;
            }

            mutablePos.move(Direction.UP);
            return mutablePos.toImmutable();
        }
    }
}
