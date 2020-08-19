package supercoder79.wavedefense.map;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.Util;
import supercoder79.wavedefense.game.WaveDefenseConfig;
import supercoder79.wavedefense.map.gen.WaveDefensePath;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public final class WaveDefenseMapGenerator {
    public CompletableFuture<WaveDefenseMap> create(WaveDefenseConfig config) {
        return CompletableFuture.supplyAsync(() -> build(config), Util.getMainWorkerExecutor());
    }

    public WaveDefenseMap build(WaveDefenseConfig config) {
        WaveDefensePath path = WaveDefensePath.generate(config.path.length, config.path.segmentLength);

        DoubleList waveStarts = new DoubleArrayList();
        double maxSpacing = config.maxWaveSpacing;
        double minSpacing = config.minWaveSpacing;

        Random random = new Random();

        double distance = path.getLength();
        while (distance >= 0.0) {
            waveStarts.add(0, distance);
            distance -= random.nextDouble() * (maxSpacing - minSpacing) + minSpacing;
        }

        return new WaveDefenseMap(path, config, waveStarts);
    }
}
