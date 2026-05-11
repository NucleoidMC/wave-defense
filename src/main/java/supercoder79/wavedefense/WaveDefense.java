package supercoder79.wavedefense;

import supercoder79.wavedefense.game.WdConfig;
import supercoder79.wavedefense.game.WdWaiting;
import xyz.nucleoid.plasmid.api.game.GameTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class WaveDefense implements ModInitializer {
	@Override
	public void onInitialize() {
		GameTypes.register(
				Identifier.fromNamespaceAndPath("wavedefense", "wavedefense"),
				WdConfig.CODEC,
				WdWaiting::open
				);
	}
}
