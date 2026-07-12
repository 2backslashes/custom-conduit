package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ExtraEffects;
import net.backslashes.extraeffects.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.*;
import org.jetbrains.annotations.NotNull;

public class EntityReach extends MobEffect {
    private static final ResourceLocation MODIFIER_RESOURCE = ResourceLocation.fromNamespaceAndPath(ExtraEffects.MODID, "effect.entity_reach");
    protected EntityReach() {
        super(MobEffectCategory.BENEFICIAL, 0xFDA667);
    }

    @Override
    public void addAttributeModifiers(@NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);

        AttributeModifier modifier = new AttributeModifier(
                MODIFIER_RESOURCE,
                ServerConfig.REACH_ENTITIES_PER_LEVEL.getAsDouble() * (1 + amplifier),
                AttributeModifier.Operation.ADD_VALUE
        );

        AttributeInstance attrib = attributeMap.getInstance(Attributes.ENTITY_INTERACTION_RANGE);
        if(attrib != null){
            attrib.addOrUpdateTransientModifier(modifier);
        }
    }

    @Override
    public void removeAttributeModifiers(@NotNull AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);

        AttributeInstance attrib = attributeMap.getInstance(Attributes.ENTITY_INTERACTION_RANGE);
        if(attrib != null) {
            attrib.removeModifier(MODIFIER_RESOURCE);
        }
    }
}