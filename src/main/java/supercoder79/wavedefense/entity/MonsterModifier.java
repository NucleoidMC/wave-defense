package supercoder79.wavedefense.entity;

import java.util.function.Supplier;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

// Types of special monsters
public enum MonsterModifier {
	NORMAL("", null, 0),
	POISON("Poisoning ", () -> new StatusEffectInstance(StatusEffects.POISON, 160), 2),
	WEAKNESS("Weakening ", () -> new StatusEffectInstance(StatusEffects.WEAKNESS, 300), 3),
	WITHER("Withering ", () -> new StatusEffectInstance(StatusEffects.WITHER, 150), 3),
	HUNGER("Hungering ", () -> new StatusEffectInstance(StatusEffects.HUNGER, 300), 2),
	SLOWNESS("Slowing ", () -> new StatusEffectInstance(StatusEffects.SLOWNESS, 200), 2),
	BLINDNESS("Blinding ", () -> new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 3),
	NAUSEA("Nauseating ", () -> new StatusEffectInstance(StatusEffects.NAUSEA, 150), 3);

	public final String prefix;
	public final Supplier<StatusEffectInstance> effect;
	public final int ironBonus;

	MonsterModifier(String prefix, Supplier<StatusEffectInstance> effect, int ironBonus) {
		this.prefix = prefix;
		this.effect = effect;
		this.ironBonus = ironBonus;
	}
}
