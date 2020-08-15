package supercoder79.wavedefense;

import supercoder79.wavedefense.game.WaveDefenseConfig;
import supercoder79.wavedefense.game.WaveDefenseWaiting;
import xyz.nucleoid.plasmid.game.GameType;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class WaveDefense implements ModInitializer {
	@Override
	public void onInitialize() {
		GameType.register(
				new Identifier("wavedefense", "wavedefense"),
				WaveDefenseWaiting::open,
				WaveDefenseConfig.CODEC
		);
	}
}
