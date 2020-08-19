package supercoder79.wavedefense.game;

import supercoder79.wavedefense.map.WaveDefenseMap;

public final class WaveDefenseWaveStarter {
    private final WaveDefenseMap map;
    private int nextWaveIndex;

    WaveDefenseWaveStarter(WaveDefenseMap map) {
        this.map = map;
    }

    public boolean tick(double progressBlocks) {
        if (nextWaveIndex >= map.waveStarts.size()) {
            return false;
        }

        double nextWaveDistance = map.waveStarts.getDouble(nextWaveIndex);
        if (progressBlocks >= nextWaveDistance) {
            nextWaveIndex++;
            return true;
        }

        return false;
    }
}
