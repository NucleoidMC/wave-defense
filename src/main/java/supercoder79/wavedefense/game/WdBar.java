package supercoder79.wavedefense.game;

import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.common.widget.BossBarWidget;

public final class WdBar {
    private final BossBarWidget bar;
    private static final Component IDLE_TITLE = Component.literal("Wave Defense");

    private WdBar(BossBarWidget bar) {
        this.bar = bar;
    }

    public static WdBar create(GlobalWidgets widgets) {
        return new WdBar(widgets.addBossBar(IDLE_TITLE, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS));
    }

    public void tick(@Nullable WdWave wave) {
        if (wave != null) {
            this.bar.setTitle(this.titleForWave(wave));
            this.bar.setProgress(wave.remainingMonsterScore / (float) wave.accumulatedMonsterScore);
        } else {
            this.bar.setTitle(IDLE_TITLE);
            this.bar.setProgress(0.0F);
        }
    }

    private Component titleForWave(WdWave wave) {
        String monsterSuffix = wave.remainingMonsterCount == 1 ? "" : "s";
        String remainSuffix = wave.remainingMonsterCount == 1 ? "s" : "";

        return Component.literal("Wave #" + wave.ordinal + ": " + wave.remainingMonsterCount + " monster" + monsterSuffix + " remain" + remainSuffix);
    }
}
