package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ExtraEffects;
import net.backslashes.extraeffects.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EntityReach extends ModifierEffect {
    public static final String EFFECT_ID = "entity_reach";
    protected EntityReach() {
        super(MobEffectCategory.BENEFICIAL, 0xFDA667, List.of(
                new ModifierEntry(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        "reach",
                        EFFECT_ID,
                        (Integer level) -> (float) (ServerConfig.REACH_ENTITIES_PER_LEVEL.getAsDouble() * (1 + level)),
                        AttributeModifier.Operation.ADD_VALUE
                )
        ));
    }
}
