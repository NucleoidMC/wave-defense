package supercoder79.wavedefense.map;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import supercoder79.wavedefense.game.WaveDefenseConfig;
import supercoder79.wavedefense.map.gen.WaveDefenseChunkGenerator;
import supercoder79.wavedefense.map.gen.WaveDefensePath;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public final class WaveDefenseMap {
	public final WaveDefensePath path;
	public final WaveDefenseConfig config;

	public final DoubleList waveStarts;

	public WaveDefenseMap(WaveDefensePath path, WaveDefenseConfig config, DoubleList waveStarts) {
		this.path = path;
		this.config = config;
		this.waveStarts = waveStarts;
	}

	public ChunkGenerator chunkGenerator(MinecraftServer server) {
		return new WaveDefenseChunkGenerator(server, config, this);
	}
}
