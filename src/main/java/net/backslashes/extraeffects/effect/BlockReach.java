package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ExtraEffects;
import net.backslashes.extraeffects.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockReach extends ModifierEffect {
    public static final String EFFECT_ID = "block_reach";
    protected BlockReach() {
        super(MobEffectCategory.BENEFICIAL, 0x766DF7, List.of(
                new ModifierEntry(
                        Attributes.BLOCK_INTERACTION_RANGE,
                        "reach",
                        EFFECT_ID,
                        (Integer level) -> (float) (ServerConfig.REACH_BLOCKS_PER_LEVEL.getAsDouble() * (1 + level)),
                        AttributeModifier.Operation.ADD_VALUE
                )
        ));
    }
}