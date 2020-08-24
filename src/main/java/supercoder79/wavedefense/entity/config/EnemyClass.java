package supercoder79.wavedefense.entity.config;

import com.mojang.serialization.Codec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.game.WdActive;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.util.function.Function;

public interface EnemyClass {
    TinyRegistry<Codec<? extends EnemyClass>> REGISTRY = TinyRegistry.newStable();
    Codec<EnemyClass> CODEC = REGISTRY.dispatchStable(EnemyClass::getCodec, Function.identity());

    WaveEntity create(WdActive game, EnemyConfig config);

    default boolean canSpawnAt(ServerWorld world, BlockPos pos) {
        return true;
    }

    Codec<? extends EnemyClass> getCodec();
}
