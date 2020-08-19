package supercoder79.wavedefense.map;

import supercoder79.wavedefense.game.WaveDefenseConfig;
import supercoder79.wavedefense.map.gen.WaveDefenseChunkGenerator;
import supercoder79.wavedefense.map.gen.WaveDefensePath;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public final class WaveDefenseMap {
	public final WaveDefensePath path;
	public final WaveDefenseConfig config;

	public WaveDefenseMap(WaveDefensePath path, WaveDefenseConfig config) {
		this.path = path;
		this.config = config;
	}

	public ChunkGenerator chunkGenerator(MinecraftServer server) {
		return new WaveDefenseChunkGenerator(server, config, this);
	}
}
