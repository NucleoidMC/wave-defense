package supercoder79.wavedefense.entity;

import java.util.function.Supplier;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import supercoder79.wavedefense.entity.monster.classes.PhantomClasses;
import supercoder79.wavedefense.entity.monster.waveentity.*;
import supercoder79.wavedefense.util.RandomCollection;

// Types of special monsters
public enum MonsterModifier {
    NORMAL("", null, 0),
    POISON("Poisoning", () -> new StatusEffectInstance(StatusEffects.POISON, 160), 2),
    WEAKNESS("Weakening", () -> new StatusEffectInstance(StatusEffects.WEAKNESS, 300), 3),
    WITHER("Withering", () -> new StatusEffectInstance(StatusEffects.WITHER, 150), 2),
    HUNGER("Hungering", () -> new StatusEffectInstance(StatusEffects.HUNGER, 300), 1),
    SLOWNESS("Slowing", () -> new StatusEffectInstance(StatusEffects.SLOWNESS, 200), 3),
    BLINDNESS("Blinding", () -> new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 3),
    NAUSEA("Nauseating", () -> new StatusEffectInstance(StatusEffects.NAUSEA, 150), 2);

    public final String prefix;
    public final Supplier<StatusEffectInstance> effect;
    public final int ironBonus;

    MonsterModifier(String prefix, Supplier<StatusEffectInstance> effect, int ironBonus) {
        this.prefix = prefix;
        this.effect = effect;
        this.ironBonus = ironBonus;
    }

    public static MonsterModifier next(int waveOrdinal, WaveEntity entity) {
        RandomCollection<MonsterModifier> modifiers = new RandomCollection<>();
        if (entity instanceof WaveZombieEntity) {
            modifiers
                    .add(10, MonsterModifier.NORMAL)
                    .add(Math.min(4, waveOrdinal / 1.5d - 1), MonsterModifier.HUNGER)
                    .add(Math.min(1, waveOrdinal / 3d - 4), MonsterModifier.WEAKNESS);
        }

        if (entity instanceof WaveDrownedEntity) {
            modifiers
                    .add(10, MonsterModifier.NORMAL)
                    .add(Math.min(3, waveOrdinal / 3d), MonsterModifier.HUNGER)
                    .add(Math.min(2, waveOrdinal / 4d - 1), MonsterModifier.NAUSEA);
        }

        if (entity instanceof WaveHuskEntity) {
            modifiers
                    .add(10, MonsterModifier.NORMAL)
                    .add(Math.min(3, waveOrdinal / 3d), MonsterModifier.NAUSEA)
                    .add(Math.min(2, waveOrdinal / 4d - 1), MonsterModifier.WITHER);
        }

        if (entity instanceof WaveSkeletonEntity) {
            modifiers
                    .add(10, MonsterModifier.NORMAL)
                    .add(Math.min(2, waveOrdinal / 4d - 3), MonsterModifier.SLOWNESS)
                    .add(Math.min(1, waveOrdinal / 5d - 4), MonsterModifier.BLINDNESS);
        }

        if (entity instanceof WaveStrayEntity) {
            modifiers
                    .add(10, MonsterModifier.NORMAL)
                    .add(Math.min(2, waveOrdinal / 4d - 3), MonsterModifier.BLINDNESS)
                    .add(Math.min(1, waveOrdinal / 5d - 4), MonsterModifier.WITHER);
        }

        if (entity instanceof WavePhantomEntity) {
            if (entity.getMonsterClass().equals(PhantomClasses.DEFAULT))
                modifiers
                        .add(10, MonsterModifier.NORMAL)
                        .add(Math.min(4, waveOrdinal / 3d - 3), MonsterModifier.NAUSEA)
                        .add(Math.min(2, waveOrdinal / 4d - 4), MonsterModifier.WEAKNESS);

            if (entity.getMonsterClass().equals(PhantomClasses.LARGE))
                modifiers
                        .add(10, MonsterModifier.NORMAL)
                        .add(Math.min(5, waveOrdinal / 3d - 3), MonsterModifier.WEAKNESS)
                        .add(Math.min(3, waveOrdinal / 4d - 4), MonsterModifier.BLINDNESS);
        }

        if (entity instanceof WaveSummonerEntity) {
            modifiers
                    .add(10, MonsterModifier.NORMAL)
                    .add(Math.min(10, waveOrdinal / 5d - 5), MonsterModifier.WEAKNESS)
                    .add(Math.min(10, waveOrdinal / 5d - 5), MonsterModifier.BLINDNESS);
        }

        if (entity instanceof WaveCaveSpiderEntity) {
            modifiers
                    .add(10, MonsterModifier.NORMAL)
                    .add(Math.min(3, waveOrdinal / 3d - 3), MonsterModifier.SLOWNESS)
                    .add(Math.min(3, waveOrdinal / 3d - 3), MonsterModifier.BLINDNESS);
        }

        if (entity instanceof WaveWitchEntity) {
            modifiers
                    .add(1, MonsterModifier.NORMAL);
        }

        return modifiers.next();
    }
}
