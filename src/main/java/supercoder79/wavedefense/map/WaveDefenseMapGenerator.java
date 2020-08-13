package supercoder79.wavedefense.map;

import java.util.concurrent.CompletableFuture;

import net.minecraft.util.Util;

public class WaveDefenseMapGenerator {
	public CompletableFuture<WaveDefenseMap> create() {
		return CompletableFuture.supplyAsync(this::build, Util.getMainWorkerExecutor());
	}

	public WaveDefenseMap build() {
		return new WaveDefenseMap();
	}
}
