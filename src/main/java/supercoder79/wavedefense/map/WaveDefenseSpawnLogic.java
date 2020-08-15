package supercoder79.wavedefense.map;

import java.util.Random;

import supercoder79.wavedefense.WaveDefenseConfig;
import xyz.nucleoid.plasmid.game.GameWorld;

import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;

public final class WaveDefenseSpawnLogic {
    private final GameWorld world;
    private final WaveDefenseConfig config;

    public WaveDefenseSpawnLogic(GameWorld world, WaveDefenseConfig config) {
        this.world = world;
        this.config = config;
    }

    public void resetPlayer(ServerPlayerEntity player, GameMode gameMode) {
        player.inventory.clear();
        player.getEnderChestInventory().clear();
        player.clearStatusEffects();
        player.setHealth(20.0F);
        player.getHungerManager().setFoodLevel(20);
        player.fallDistance = 0.0F;
        player.setGameMode(gameMode);
        player.setExperienceLevel(0);
        player.setExperiencePoints(0);
    }

    public void spawnPlayer(ServerPlayerEntity player) {
        ServerWorld world = this.world.getWorld();

        BlockPos pos = topPos(this.world, this.config);
        ChunkPos chunkPos = new ChunkPos(pos);
        world.getChunkManager().addTicket(ChunkTicketType.field_19347, chunkPos, 1, player.getEntityId());

        player.teleport(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
    }

    public static BlockPos topPos(GameWorld gameWorld, WaveDefenseConfig config) {
        ServerWorld world = gameWorld.getWorld();

        BlockPos pos = new BlockPos(0, 60, 0);
        boolean foundPos = false;
        while (!foundPos) {
            Random random = world.getRandom();
            int x = random.nextInt(config.borderSize) - (config.borderSize / 2);
            int z = random.nextInt(config.borderSize) - (config.borderSize / 2);
            pos = new BlockPos(x, 60, z);

            // Get the y position by using this amazing hack
            // TODO: fix
            BlockPos.Mutable mutable = pos.mutableCopy();
            mutable.setY(256);
            for (int y = 256; y > 0; y--) {
                if (world.getBlockState(mutable.set(x, y, z)).isOf(Blocks.GRASS_BLOCK)) {
                    foundPos = true;
                    break;
                }
            }

            pos = mutable.up(2).toImmutable();
        }

        return pos.toImmutable();
    }
}
