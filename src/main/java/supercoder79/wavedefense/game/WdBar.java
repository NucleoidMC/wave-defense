package supercoder79.wavedefense.game;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.widget.BossBarWidget;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

public final class WdBar {
    private final BossBarWidget bar;
    private static final LiteralText IDLE_TITLE = new LiteralText("Wave Defense");

    private WdBar(BossBarWidget bar) {
        this.bar = bar;
    }

    public static WdBar create(GlobalWidgets widgets) {
        return new WdBar(widgets.addBossBar(IDLE_TITLE, BossBar.Color.GREEN, BossBar.Style.PROGRESS));
    }

    public void tick(@Nullable WdWave wave) {
        if (wave != null) {
            this.bar.setTitle(this.titleForWave(wave));
            this.bar.setProgress(wave.remainingMonsters / (float) wave.totalMonsters);
        } else {
            this.bar.setTitle(IDLE_TITLE);
            this.bar.setProgress(0.0F);
        }
    }

    private Text titleForWave(WdWave wave) {
        return new LiteralText("Wave #" + wave.ordinal + ": " + wave.remainingMonsters + " monsters remain.");
    }
}
