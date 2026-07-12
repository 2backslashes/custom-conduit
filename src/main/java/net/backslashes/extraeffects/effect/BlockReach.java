package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ExtraEffects;
import net.backslashes.extraeffects.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.*;
import org.jetbrains.annotations.NotNull;

public class BlockReach extends MobEffect {
    private static final ResourceLocation MODIFIER_RESOURCE = ResourceLocation.fromNamespaceAndPath(ExtraEffects.MODID, "effect.block_reach");
    protected BlockReach() {
        super(MobEffectCategory.BENEFICIAL, 0x766DF7);
    }

    @Override
    public void addAttributeModifiers(@NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);

        AttributeModifier modifier = new AttributeModifier(
                MODIFIER_RESOURCE,
                ServerConfig.REACH_BLOCKS_PER_LEVEL.getAsDouble() * (1 + amplifier),
                AttributeModifier.Operation.ADD_VALUE
        );

        AttributeInstance attrib = attributeMap.getInstance(Attributes.BLOCK_INTERACTION_RANGE);
        if(attrib != null){
            attrib.addOrUpdateTransientModifier(modifier);
        }
    }

    @Override
    public void removeAttributeModifiers(@NotNull AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);

        AttributeInstance attrib = attributeMap.getInstance(Attributes.BLOCK_INTERACTION_RANGE);
        if(attrib != null) {
            attrib.removeModifier(MODIFIER_RESOURCE);
        }
    }
}