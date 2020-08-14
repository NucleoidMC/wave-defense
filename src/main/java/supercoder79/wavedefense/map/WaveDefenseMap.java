package supercoder79.wavedefense.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WaveDefenseMap {
	public final WaveDefensePath path;

	public WaveDefenseMap(WaveDefensePath path) {
		this.path = path;
	}

	public ChunkGenerator chunkGenerator(MinecraftServer server) {
		return new WaveDefenseChunkGenerator(server, this);
	}
}
