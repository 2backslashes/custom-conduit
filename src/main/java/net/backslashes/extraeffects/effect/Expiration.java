package net.backslashes.extraeffects.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class Expiration extends MobEffect {
    public static final String EFFECT_ID = "expiration";
    protected Expiration() {
        super(MobEffectCategory.HARMFUL, 0x3D4532);
    }
}
