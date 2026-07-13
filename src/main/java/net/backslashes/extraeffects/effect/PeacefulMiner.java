package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ServerConfig;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Arrays;

public class PeacefulMiner extends ModifierEffect{
    public static final String EFFECT_ID = "peaceful_miner";
    protected PeacefulMiner() {
        super(MobEffectCategory.BENEFICIAL, 0x446633, Arrays.asList(
                new ModifierEntry(
                        Attributes.BLOCK_BREAK_SPEED,
                        "haste",
                        EFFECT_ID,
                        (Integer level) -> (float) (ServerConfig.PEACEFUL_MINER_SPEED_PER_LEVEL.getAsDouble() * (1 + level)),
                        AttributeModifier.Operation.ADD_VALUE
                ),
                new ModifierEntry(
                        Attributes.ATTACK_DAMAGE,
                        "damage",
                        EFFECT_ID,
                        (Integer level) -> (float) (-ServerConfig.PEACEFUL_MINER_DAMAGE_PER_LEVEL.getAsDouble() * (1 + level)),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        ));
    }
}
