package supercoder79.wavedefense.game;

public final class WdWave {
    public final int ordinal;
    public final int totalMonsters;

    public int remainingMonsters;

    public WdWave(int ordinal, int totalMonsters) {
        this.ordinal = ordinal;
        this.totalMonsters = totalMonsters;
    }

    public void onMonsterAdded() {
        this.remainingMonsters++;
    }

    public void onMonsterKilled() {
        this.remainingMonsters--;
    }
}
