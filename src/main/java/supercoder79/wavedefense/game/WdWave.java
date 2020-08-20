package supercoder79.wavedefense.game;

public final class WdWave {
    public final int ordinal;
    public final int totalZombies;

    public int remainingZombies;

    public WdWave(int ordinal, int totalZombies) {
        this.ordinal = ordinal;
        this.totalZombies = totalZombies;
    }

    public void onZombieAdded() {
        this.remainingZombies++;
    }

    public void onZombieKilled() {
        this.remainingZombies--;
    }
}
