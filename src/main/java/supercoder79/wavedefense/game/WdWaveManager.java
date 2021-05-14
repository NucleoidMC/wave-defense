package supercoder79.wavedefense.game;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;
import xyz.nucleoid.plasmid.game.player.PlayerSet;

public final class WdWaveManager {
    private final WdActive game;
    private int nextWaveIndex;

    private WdWave activeWave;
    private WdWaveSpawner waveSpawner;

    WdWaveManager(WdActive game) {
        this.game = game;
        this.nextWaveIndex = 0;
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

        if (waveSpawner == null && wave.remainingMonsterScore <= 0) {
            PlayerSet players = game.space.getPlayers();
            players.sendMessage(new LiteralText("The wave has ended!")
                    .styled(style ->
                            style.withColor(TextColor.parse("green"))
                    ));
            int survivalBonus = 3 + wave.ordinal;
            int survivalGold = wave.ordinal % 5 == 0 ? MathHelper.ceil(wave.ordinal / 10d) : 0;

            if (survivalGold == 0)
                players.sendMessage(new LiteralText("You earned " + survivalBonus + " iron for surviving this wave!")
                        .styled(style ->
                                style.withColor(TextColor.parse("yellow"))
                        ));
            else
                players.sendMessage(new LiteralText("You earned " + survivalBonus + " iron and " + survivalGold + " gold for surviving this wave!")
                        .styled(style ->
                                style.withColor(TextColor.parse("yellow"))
                        ));

            for (PlayerEntity player : players) {
                player.inventory.insertStack(new ItemStack(Items.IRON_INGOT, survivalBonus));
                player.inventory.insertStack(new ItemStack(Items.GOLD_INGOT, survivalGold));
            }

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
        players.sendMessage(new LiteralText("Wave #" + wave.ordinal + " with " + wave.monsterCount + " monsters is coming!")
                .styled(style ->
                        style.withColor(TextColor.parse("light_purple"))
                ));
    }

    private WdWave createWave(int index) {
        return new WdWave(index + 1, monsterScore(index));
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

    private int monsterScore(int index) {
        WdConfig.MonsterSpawns monsterSpawns = game.config.monsterSpawns;
        double base = game.groupSize * monsterSpawns.baseGroupSizeScale + index * monsterSpawns.baseIndexScale + 2;
        return Math.min(MathHelper.floor(base + Math.pow(Math.max(0, index - 5), monsterSpawns.postWaveFiveScale) + Math.pow(index, monsterSpawns.indexScale) * Math.pow(game.groupSize, monsterSpawns.groupSizeScale)), MathHelper.floor(80 + Math.pow(game.groupSize, monsterSpawns.upperGroupSizeScale) * 18 + Math.pow(index, monsterSpawns.upperIndexScale)));
    }
}
