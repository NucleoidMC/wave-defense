package supercoder79.wavedefense.entity.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.WaveZombieEntity;
import supercoder79.wavedefense.game.WdActive;

public final class ZombieClass implements EnemyClass {
    public static final Codec<ZombieClass> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                ModifiersConfig.CODEC.optionalFieldOf("modifiers", ModifiersConfig.EMPTY).forGetter(c -> c.modifiers),
                EquipmentConfig.CODEC.optionalFieldOf("equipment", EquipmentConfig.EMPTY).forGetter(c -> c.equipment)
        ).apply(instance, ZombieClass::new);
    });

    public final ModifiersConfig modifiers;
    public final EquipmentConfig equipment;

    public ZombieClass(ModifiersConfig modifiers, EquipmentConfig equipment) {
        this.modifiers = modifiers;
        this.equipment = equipment;
    }

    @Override
    public WaveEntity create(WdActive game, EnemyConfig config) {
        return new WaveZombieEntity(game.world.getWorld(), game, config, this);
    }

    @Override
    public boolean canSpawnAt(ServerWorld world, BlockPos pos) {
        return !world.containsFluid(new Box(pos).expand(0.5));
    }

    @Override
    public Codec<? extends EnemyClass> getCodec() {
        return CODEC;
    }
}
