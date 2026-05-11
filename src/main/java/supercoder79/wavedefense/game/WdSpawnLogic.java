package supercoder79.wavedefense.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public record WdSpawnLogic(ServerLevel world, WdConfig config) {

    public void resetPlayer(ServerPlayer player, GameType gameMode) {
        player.getInventory().clearContent();
        player.getEnderChestInventory().clearContent();
        player.removeAllEffects();
        player.setHealth(20.0F);
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().eat(5, 0.5F);
        player.fallDistance = 0.0F;
        player.setGameMode(gameMode);
        player.setExperienceLevels(0);
        player.setExperiencePoints(0);
    }

    private static final TicketType FORCE_TELEPORT = TicketType.PLAYER_LOADING;

    public void spawnPlayer(ServerPlayer player) {
        BlockPos pos = findSurfaceAround(Vec3.ZERO, this.world, this.config);
        ChunkPos chunkPos = ChunkPos.containing(pos);
        world.getChunkSource().addTicket(new Ticket(FORCE_TELEPORT, 3), chunkPos);

        player.teleportTo(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Set.of(), 0.0F, 0.0F, false);
    }

    public static BlockPos findSurfaceAround(Vec3 centerPos, ServerLevel world, WdConfig config) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        while (true) {
            RandomSource random = world.getRandom();
            double x = centerPos.x + random.nextInt(config.spawnRadius) - random.nextInt(config.spawnRadius);
            double z = centerPos.z + random.nextInt(config.spawnRadius) - random.nextInt(config.spawnRadius);
            mutablePos.set(x, 0, z);

            world.getChunk(mutablePos);
            int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, mutablePos.getX(), mutablePos.getZ());
            mutablePos.setY(topY - 1);

            BlockState ground = world.getBlockState(mutablePos);
            if (ground.is(BlockTags.LEAVES)) {
                continue;
            }

            mutablePos.move(Direction.UP);
            return mutablePos.immutable();
        }
    }

    public static BlockPos findSurfaceAt(int x, int z, int offset, ServerLevel world) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        while (true) {
            RandomSource random = world.getRandom();
            mutablePos.set(x + (random.nextInt(offset) - random.nextInt(offset)), 0, z + (random.nextInt(offset) - random.nextInt(offset)));

            world.getChunk(mutablePos);
            int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, mutablePos.getX(), mutablePos.getZ());
            mutablePos.setY(topY - 1);

            BlockState ground = world.getBlockState(mutablePos);
            if (ground.is(BlockTags.LEAVES)) {
                continue;
            }

            mutablePos.move(Direction.UP);
            return mutablePos.immutable();
        }
    }
}
