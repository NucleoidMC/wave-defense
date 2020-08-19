package supercoder79.wavedefense;

import supercoder79.wavedefense.game.WdConfig;
import supercoder79.wavedefense.game.WdWaiting;
import xyz.nucleoid.plasmid.game.GameType;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class WaveDefense implements ModInitializer {
	@Override
	public void onInitialize() {
		GameType.register(
				new Identifier("wavedefense", "wavedefense"),
				WdWaiting::open,
				WdConfig.CODEC
		);
	}
}
