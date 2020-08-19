package supercoder79.wavedefense.game;

import supercoder79.wavedefense.map.WdMap;

public final class WdWaveStarter {
    private final WdMap map;
    private int nextWaveIndex;

    WdWaveStarter(WdMap map) {
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
