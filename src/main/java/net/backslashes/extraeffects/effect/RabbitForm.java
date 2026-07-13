package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ServerConfig;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Arrays;

public class RabbitForm extends ModifierEffect {
    public static final String EFFECT_ID = "rabbit_form";
    protected RabbitForm() {
        super(MobEffectCategory.BENEFICIAL, 0xEDCC93, Arrays.asList(
                new ModifierEntry(
                        Attributes.MOVEMENT_SPEED,
                        "speed",
                        EFFECT_ID,
                        (Integer level) -> (float) (ServerConfig.RABBIT_SPEED_PER_LEVEL.getAsDouble() * (1 + level)),
                        AttributeModifier.Operation.ADD_VALUE
                ),
                new ModifierEntry(
                        Attributes.JUMP_STRENGTH,
                        "jump",
                        EFFECT_ID,
                        (Integer level) -> (float) (ServerConfig.RABBIT_JUMP_PER_LEVEL.getAsDouble() * (1 + level)),
                        AttributeModifier.Operation.ADD_VALUE
                ),
                new ModifierEntry(
                        Attributes.FALL_DAMAGE_MULTIPLIER,
                        "fall",
                        EFFECT_ID,
                        (Integer level) -> -1.0f,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ),
                new ModifierEntry(
                        Attributes.ARMOR,
                        "armor",
                        EFFECT_ID,
                        ((Integer level) -> -0.75f),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ),
                new ModifierEntry(
                        Attributes.MAX_HEALTH,
                        "health",
                        EFFECT_ID,
                        ((Integer level) -> -0.75f),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ),
                new ModifierEntry(
                        Attributes.ATTACK_DAMAGE,
                        "damage",
                        EFFECT_ID,
                        ((Integer level) -> -0.75f),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        ));
    }
}
