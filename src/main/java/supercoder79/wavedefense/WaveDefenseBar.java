package supercoder79.wavedefense;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class WaveDefenseBar implements AutoCloseable {
	private final ServerBossBar bar;

	public WaveDefenseBar() {
		LiteralText title = new LiteralText("x Zombies remaining");

		this.bar = new ServerBossBar(title, BossBar.Color.GREEN, BossBar.Style.PROGRESS);
		this.bar.setDarkenSky(false);
		this.bar.setDragonMusic(false);
		this.bar.setThickenFog(false);
	}

	public void tick(int waveNum, int totalZombies, int killedZombies) {
		this.bar.setName(new LiteralText("Wave " + waveNum + ": " + (totalZombies - killedZombies) + " zombies remain."));
		this.bar.setPercent((totalZombies - killedZombies) / (float) totalZombies);
	}

	public void addPlayer(ServerPlayerEntity player) {
		this.bar.addPlayer(player);
	}

	public void removePlayer(ServerPlayerEntity player) {
		this.bar.removePlayer(player);
	}

	@Override
	public void close() {
		this.bar.clearPlayers();
		this.bar.setVisible(false);
	}
}
