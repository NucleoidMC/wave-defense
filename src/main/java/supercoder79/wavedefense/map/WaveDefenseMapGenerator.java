package supercoder79.wavedefense.map;

import net.minecraft.util.Util;

import java.util.concurrent.CompletableFuture;

import supercoder79.wavedefense.WaveDefenseConfig;

public class WaveDefenseMapGenerator {
	public CompletableFuture<WaveDefenseMap> create(WaveDefenseConfig config) {
		return CompletableFuture.supplyAsync(() -> build(config), Util.getMainWorkerExecutor());
	}

	public WaveDefenseMap build(WaveDefenseConfig config) {
		WaveDefensePath path = WaveDefensePath.generate(config.pathConfig.pathLength, config.pathConfig.pathSegmentLength);
		return new WaveDefenseMap(path, config);
	}
}
