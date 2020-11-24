package supercoder79.wavedefense.game;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.widget.BossBarWidget;

public final class WdBar implements AutoCloseable {
    private final BossBarWidget bar;
    private final LiteralText idleTitle = new LiteralText("Wave Defense");

    public WdBar(GameSpace space) {
        PlayerSet players = space.getPlayers();
        this.bar = new BossBarWidget(space, idleTitle, BossBar.Color.GREEN, BossBar.Style.PROGRESS);
    }

    public void tick(@Nullable WdWave wave) {
        if (wave != null) {
            this.bar.setTitle(this.titleForWave(wave));
            this.bar.setProgress(wave.remainingZombies / (float) wave.totalZombies);
        } else {
            this.bar.setTitle(idleTitle);
            this.bar.setProgress(0.0F);
        }
    }

    private Text titleForWave(WdWave wave) {
        return new LiteralText("Wave #" + wave.ordinal + ": " + wave.remainingZombies + " zombies remain.");
    }

    @Override
    public void close() {
        this.bar.close();
    }
}
