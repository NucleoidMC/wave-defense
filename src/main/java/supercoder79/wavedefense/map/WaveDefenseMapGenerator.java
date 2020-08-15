package supercoder79.wavedefense.map;

import java.util.concurrent.CompletableFuture;

import supercoder79.wavedefense.game.WaveDefenseConfig;
import supercoder79.wavedefense.map.gen.WaveDefensePath;

import net.minecraft.util.Util;

public final class WaveDefenseMapGenerator {
	public CompletableFuture<WaveDefenseMap> create(WaveDefenseConfig config) {
		return CompletableFuture.supplyAsync(() -> build(config), Util.getMainWorkerExecutor());
	}

	public WaveDefenseMap build(WaveDefenseConfig config) {
		WaveDefensePath path = WaveDefensePath.generate(config.pathConfig.pathLength, config.pathConfig.pathSegmentLength);
		return new WaveDefenseMap(path, config);
	}
}
