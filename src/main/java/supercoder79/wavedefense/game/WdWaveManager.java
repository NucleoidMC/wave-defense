package supercoder79.wavedefense.game;

import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.player.PlayerSet;

public final class WdWaveManager {
    private final WdActive game;
    private int nextWaveIndex;

    private WdWave activeWave;
    private WdWaveSpawner waveSpawner;

    WdWaveManager(WdActive game) {
        this.game = game;
    }

    public void tick(long time, double progressBlocks) {
        if (activeWave != null) {
            tickActive(time, activeWave);
        } else {
            tickInactive(progressBlocks);
        }
    }

    private void tickActive(long time, WdWave wave) {
        if (waveSpawner != null) {
            if (waveSpawner.tick(time)) {
                waveSpawner = null;
            }
        }

        if (waveSpawner == null && wave.remainingZombies <= 0) {
            PlayerSet players = game.space.getPlayers();
            players.sendMessage(new LiteralText("The wave has ended!"));

            activeWave = null;
        }
    }

    private void tickInactive(double progressBlocks) {
        if (nextWaveIndex >= game.map.waveStarts.size()) {
            return;
        }

        if (progressBlocks >= getNextWaveDistance()) {
            WdWave wave = createWave(nextWaveIndex++);
            openWave(wave);
        }
    }

    private void openWave(WdWave wave) {
        activeWave = wave;
        waveSpawner = new WdWaveSpawner(game, wave);

        PlayerSet players = game.space.getPlayers();
        players.sendMessage(new LiteralText("Wave #" + wave.ordinal + " with " + wave.totalZombies + " zombies is coming!"));
    }

    private WdWave createWave(int index) {
        return new WdWave(index + 1, zombieCount(index));
    }

    @Nullable
    public WdWave getActiveWave() {
        return activeWave;
    }

    public int getWaveOrdinal() {
        return nextWaveIndex;
    }

    public boolean isActive() {
        return activeWave != null;
    }

    public double getNextWaveDistance() {
        return game.map.waveStarts.getDouble(Math.min(nextWaveIndex, game.map.waveStarts.size() - 1));
    }

    private int zombieCount(int index) {
        double baseCount = game.groupSize * 1.5 + 5.0;
        return Math.min(MathHelper.floor(baseCount + index * 2.5), 64 + this.game.groupSize * 2);
    }
}
