package net.backslashes.extraeffects.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class Explosive extends MobEffect {
    public static final String EFFECT_ID = "explosive";
    protected Explosive() {
        super(MobEffectCategory.HARMFUL, 0x591103);
    }
}
