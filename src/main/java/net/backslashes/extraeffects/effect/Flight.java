package net.backslashes.extraeffects.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class Flight extends MobEffect {
    public static final String EFFECT_ID = "flight";
    protected Flight() {
        super(MobEffectCategory.BENEFICIAL, 0xFCF3D2);
    }
}
