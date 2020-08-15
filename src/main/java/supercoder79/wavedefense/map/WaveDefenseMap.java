package supercoder79.wavedefense.map;

import supercoder79.wavedefense.WaveDefenseConfig;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WaveDefenseMap {
	public final WaveDefensePath path;
	public final WaveDefenseConfig config;

	public WaveDefenseMap(WaveDefensePath path, WaveDefenseConfig config) {
		this.path = path;
		this.config = config;
	}

	public ChunkGenerator chunkGenerator(MinecraftServer server) {
		return new WaveDefenseChunkGenerator(server, this);
	}
}
