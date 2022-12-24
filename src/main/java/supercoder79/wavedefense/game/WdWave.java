package supercoder79.wavedefense.game;

import net.minecraft.util.math.random.Random;

public final class WdWave {
    public final int ordinal;
    public final int totalMonsterScore;
    public int remainingMonsterScore;
    public int accumulatedMonsterScore;
    public int monsterCount;
    public int remainingMonsterCount;

    public boolean isSummonerWave;
    public boolean isSpiderWave;

    public WdWave(Random random, int ordinal, int totalMonsterScore) {
        this.ordinal = ordinal;
        this.totalMonsterScore = totalMonsterScore;

        this.isSummonerWave = ordinal >= 15 && ordinal % 5 == 0;
        this.isSpiderWave = ordinal >= 10 && random.nextInt(5) == 0 && !isSummonerWave;
    }

    public void onMonsterAdded(int score) {
        this.accumulatedMonsterScore += score;
    }

    public void onMonsterSpawned(int score) {
        this.remainingMonsterScore += score;
        this.remainingMonsterCount++;
    }

    public void onMonsterKilled(int score) {
        this.remainingMonsterScore -= score;
        this.remainingMonsterCount--;
    }
}
