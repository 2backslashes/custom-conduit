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

public class Unimpeded extends MobEffect {
    private static final ResourceLocation SPEED_MODIFIER_RESOURCE = ResourceLocation.fromNamespaceAndPath(ExtraEffects.MODID, "effect.unimpeded_speed");
    protected Unimpeded() {
        super(MobEffectCategory.BENEFICIAL, 0xe3A65B);
    }

    @Override
    public void addAttributeModifiers(@NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);

        AttributeModifier speedModifier = new AttributeModifier(
                SPEED_MODIFIER_RESOURCE,
                1.0 * amplifier,
                AttributeModifier.Operation.ADD_VALUE
        );

        AttributeInstance speedAttrib = attributeMap.getInstance(Attributes.MOVEMENT_EFFICIENCY);
        if(speedAttrib != null){
            speedAttrib.addOrUpdateTransientModifier(speedModifier);
        }
    }

    @Override
    public void removeAttributeModifiers(@NotNull AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);

        AttributeInstance speedAttrib = attributeMap.getInstance(Attributes.MOVEMENT_EFFICIENCY);
        if(speedAttrib != null) {
            speedAttrib.removeModifier(SPEED_MODIFIER_RESOURCE);
        }
    }
}
