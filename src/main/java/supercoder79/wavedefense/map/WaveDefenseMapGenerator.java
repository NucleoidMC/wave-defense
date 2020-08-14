package supercoder79.wavedefense.map;

import net.minecraft.util.Util;

import java.util.concurrent.CompletableFuture;

public class WaveDefenseMapGenerator {
	public CompletableFuture<WaveDefenseMap> create() {
		return CompletableFuture.supplyAsync(this::build, Util.getMainWorkerExecutor());
	}

	public WaveDefenseMap build() {
		WaveDefensePath path = WaveDefensePath.generate(1024, 32);
		return new WaveDefenseMap(path);
	}
}
