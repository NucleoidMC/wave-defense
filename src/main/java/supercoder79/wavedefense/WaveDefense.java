package supercoder79.wavedefense;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import supercoder79.wavedefense.entity.config.DrownedClass;
import supercoder79.wavedefense.entity.config.EnemyClass;
import supercoder79.wavedefense.entity.config.ZombieClass;
import supercoder79.wavedefense.game.WdConfig;
import supercoder79.wavedefense.game.WdWaiting;
import xyz.nucleoid.plasmid.game.GameType;

public class WaveDefense implements ModInitializer {
	@Override
	public void onInitialize() {
		GameType.register(
				new Identifier("wavedefense", "wavedefense"),
				WdWaiting::open,
				WdConfig.CODEC
		);

		EnemyClass.REGISTRY.register(new Identifier("wavedefense", "zombie"), ZombieClass.CODEC);
		EnemyClass.REGISTRY.register(new Identifier("wavedefense", "drowned"), DrownedClass
				.CODEC);
	}
}
