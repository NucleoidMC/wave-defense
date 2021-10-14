package supercoder79.wavedefense.map;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import supercoder79.wavedefense.game.WdConfig;
import supercoder79.wavedefense.map.gen.WdChunkGenerator;
import supercoder79.wavedefense.map.gen.WdPath;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record WdMap(WdPath path, WdConfig config, DoubleList waveStarts) {

	public ChunkGenerator chunkGenerator(MinecraftServer server) {
		return new WdChunkGenerator(server, config, this);
	}
}
