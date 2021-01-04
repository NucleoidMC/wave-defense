package supercoder79.wavedefense.entity;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

// Types of special monsters
public enum MonsterModifier {
	NORMAL("", null),
	POISON("Poisoning ", new StatusEffectInstance(StatusEffects.POISON, 100)),
	WEAKNESS("Weakening ", new StatusEffectInstance(StatusEffects.WEAKNESS, 200)),
	WITHER("Withering ", new StatusEffectInstance(StatusEffects.WITHER, 100));

	public final String prefix;
	public final StatusEffectInstance effect;

	MonsterModifier(String prefix, StatusEffectInstance effect) {
		this.prefix = prefix;
		this.effect = effect;
	}
}
