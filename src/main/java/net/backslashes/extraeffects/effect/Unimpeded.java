package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ExtraEffects;
import net.backslashes.extraeffects.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Unimpeded extends ModifierEffect {
    public static final String EFFECT_ID = "unimpeded";
    protected Unimpeded() {
        super(MobEffectCategory.BENEFICIAL, 0xE3A65B, List.of(
                new ModifierEntry(
                        Attributes.MOVEMENT_EFFICIENCY,
                        "move_efficiency",
                        EFFECT_ID,
                        (Integer level) -> 1.0f,
                        AttributeModifier.Operation.ADD_VALUE
                )
        ));
    }
}