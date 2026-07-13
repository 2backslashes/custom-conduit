package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ExtraEffects;
import net.backslashes.extraeffects.ServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class ModifierEffect extends MobEffect {
    public static class ModifierEntry{
        public final ResourceLocation resourceLocation;
        public final Function<Integer, Float> amountFunc;
        public final AttributeModifier.Operation operation;
        public final Holder<Attribute> attribute;

        public ModifierEntry(Holder<Attribute> attribute, String modifierId, String effectId, Function<Integer, Float> amountFunc, AttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.resourceLocation = ResourceLocation.fromNamespaceAndPath(ExtraEffects.MODID, "effect." + effectId + "." + modifierId);
            this.amountFunc = amountFunc;
            this.operation = operation;
        }

        public void add(@NotNull AttributeMap attributeMap, int amplifier){
            AttributeModifier modifier = new AttributeModifier(
                    this.resourceLocation,
                    this.amountFunc.apply(amplifier),
                    this.operation
            );

            AttributeInstance attrib = attributeMap.getInstance(this.attribute);
            if(attrib != null){
                attrib.addOrUpdateTransientModifier(modifier);
            }
        }

        public void remove(@NotNull AttributeMap attributeMap){
            AttributeInstance attrib = attributeMap.getInstance(this.attribute);
            if(attrib != null) {
                attrib.removeModifier(this.resourceLocation);
            }
        }
    }

    public final List<ModifierEntry> modifierEntries;
    protected ModifierEffect(MobEffectCategory category, int color, List<ModifierEntry> modifierEntries) {
        super(category, color);
        this.modifierEntries = modifierEntries;
    }

    @Override
    public void addAttributeModifiers(@NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);

        for(ModifierEntry entry : this.modifierEntries){
            entry.add(attributeMap, amplifier);
        }
    }

    @Override
    public void removeAttributeModifiers(@NotNull AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);

        for(ModifierEntry entry : this.modifierEntries){
            entry.remove(attributeMap);
        }
    }
}
