package supercoder79.wavedefense.game;

import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.widget.BossBarWidget;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public final class WdBar implements AutoCloseable {
	private final BossBarWidget bar;

	public WdBar(GameWorld world) {
		LiteralText title = new LiteralText("x Zombies remaining");

		this.bar = BossBarWidget.open(world.getPlayerSet(), title, BossBar.Color.GREEN, BossBar.Style.PROGRESS);
	}

	public void tick(int waveNum, int totalZombies, int killedZombies) {
		this.bar.setTitle(new LiteralText("Wave " + waveNum + ": " + (totalZombies - killedZombies) + " zombies remain."));
		this.bar.setProgress((totalZombies - killedZombies) / (float) totalZombies);
	}

	@Override
	public void close() {
		this.bar.close();
	}
}
