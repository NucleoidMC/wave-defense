package supercoder79.wavedefense.entity;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

// Types of special monsters
public enum MonsterModifier {
	NORMAL("", null, 0),
	POISON("Poisoning ", new StatusEffectInstance(StatusEffects.POISON, 160), 2),
	WEAKNESS("Weakening ", new StatusEffectInstance(StatusEffects.WEAKNESS, 300), 2),
	WITHER("Withering ", new StatusEffectInstance(StatusEffects.WITHER, 100), 2);

	public final String prefix;
	public final StatusEffectInstance effect;
	public final int ironBonus;

	MonsterModifier(String prefix, StatusEffectInstance effect, int ironBonus) {
		this.prefix = prefix;
		this.effect = effect;
		this.ironBonus = ironBonus;
	}
}
