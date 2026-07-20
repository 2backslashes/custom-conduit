package net.backslashes.customconduit.recipe;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.backslashes.customconduit.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record EffectConduitRecipe(
        String displayName,
        int minFrameBlockCount,
        int maxFrameBlockCount,
        Ingredient frameBlockIngredient,
        List<ConduitEffect> outEffects,
        MathUtil.RgbColor color
) implements Recipe<EffectConduitRecipeInput> {
    public record ConduitEffect(
        Holder<MobEffect> effect,
        int amplifier,
        double minRange,
        double maxRange
    ){
        public static final Codec<ConduitEffect> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                MobEffect.CODEC.fieldOf("effect").forGetter(ConduitEffect::effect),
                Codec.INT.optionalFieldOf("amplifier", 0).forGetter(ConduitEffect::amplifier),
                Codec.DOUBLE.optionalFieldOf("minRange", 32.0).forGetter(ConduitEffect::minRange),
                Codec.DOUBLE.optionalFieldOf("maxRange", 96.0).forGetter(ConduitEffect::maxRange)
        ).apply(inst, ConduitEffect::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ConduitEffect> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ConduitEffect>() {
            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buffer, ConduitEffect effect) {
                MobEffect.STREAM_CODEC.encode(buffer, effect.effect);
                buffer.writeInt(effect.amplifier);
                buffer.writeDouble(effect.minRange);
                buffer.writeDouble(effect.maxRange);
            }

            @Override
            public @NotNull ConduitEffect decode(@NotNull RegistryFriendlyByteBuf buffer) {
                Holder<MobEffect> mobEffect = MobEffect.STREAM_CODEC.decode(buffer);
                int amplifier = buffer.readInt();
                double minRange = buffer.readDouble();
                double maxRange = buffer.readDouble();
                return new ConduitEffect(mobEffect, amplifier, minRange, maxRange);
            }
        };

        public double computeEffectRange(double powerFactor){
            return powerFactor * (maxRange - minRange) + minRange;
        }
    }
    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(frameBlockIngredient);
    }

    // We don't actually use this.
    @Override
    public boolean matches(@NotNull EffectConduitRecipeInput effectConduitRecipeInput, Level level) {
        return false;
    }

    public <T> List<T> computeValidFrameBlocks(HashMap<Block, List<T>> frameBlocksByType){
        List<T> validBlocks = new ArrayList<>();
        for (Map.Entry<Block, List<T>> entry : frameBlocksByType.entrySet()) {
            ItemStack itemStack = new ItemStack(entry.getKey().asItem(), 1);
            if(frameBlockIngredient.test(itemStack)){
                validBlocks.addAll(entry.getValue());
            }
        }
        return validBlocks;
    }

    public double computePowerFactor(int validFrameBlockCount){
        return Double.min(1.0, (validFrameBlockCount - minFrameBlockCount) / (double) (maxFrameBlockCount - minFrameBlockCount));
    }

    @Override
    public ItemStack assemble(@NotNull EffectConduitRecipeInput effectConduitRecipeInput, HolderLookup.@NotNull Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return null;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.EFFECT_CONDUIT_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.EFFECT_CONDUIT_RECIPE_TYPE.get();
    }

    public static class EffectConduitRecipeSerializer implements RecipeSerializer<EffectConduitRecipe> {
        public static final MapCodec<EffectConduitRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.fieldOf("displayName").forGetter(EffectConduitRecipe::displayName),
                Codec.INT.optionalFieldOf("minFrameBlockCount", 16).forGetter(EffectConduitRecipe::minFrameBlockCount),
                Codec.INT.optionalFieldOf("maxFrameBlockCount", 42).forGetter(EffectConduitRecipe::maxFrameBlockCount),
                Ingredient.CODEC_NONEMPTY.fieldOf("frameIngredient").forGetter(EffectConduitRecipe::frameBlockIngredient),
                ConduitEffect.CODEC.listOf(1, 255).fieldOf("effects").forGetter(EffectConduitRecipe::outEffects),
                MathUtil.RgbColor.CODEC.optionalFieldOf("color", new MathUtil.RgbColor(1.0f, 1.0f, 1.0f)).forGetter(EffectConduitRecipe::color)
        ).apply(inst, EffectConduitRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EffectConduitRecipe> STREAM_CODEC = StreamCodec.of(EffectConduitRecipeSerializer::toNetwork, EffectConduitRecipeSerializer::fromNetwork);

        @Override
        public @NotNull MapCodec<EffectConduitRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, EffectConduitRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static final int DISPLAY_NAME_MAX_LENGTH = 64;

        private static EffectConduitRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String displayName = ByteBufCodecs.stringUtf8(DISPLAY_NAME_MAX_LENGTH).decode(buffer);
            int minFrameBlockCount = buffer.readByte();
            int maxFrameBlockCount = buffer.readByte();
            Ingredient frameBlockIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            List<ConduitEffect> effects = new ArrayList<>();
            int effectCount = buffer.readVarInt();
            for(int i=0; i<effectCount; ++i){
                ConduitEffect effect = ConduitEffect.STREAM_CODEC.decode(buffer);
                effects.add(effect);
            }
            MathUtil.RgbColor color = MathUtil.RgbColor.STREAM_CODEC.decode(buffer);
            return new EffectConduitRecipe(
                    displayName,
                    minFrameBlockCount,
                    maxFrameBlockCount,
                    frameBlockIngredient,
                    effects,
                    color
            );
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, EffectConduitRecipe recipe) {
            ByteBufCodecs.stringUtf8(DISPLAY_NAME_MAX_LENGTH).encode(buffer, recipe.displayName);
            buffer.writeByte(recipe.minFrameBlockCount);
            buffer.writeByte(recipe.maxFrameBlockCount);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.frameBlockIngredient);
            buffer.writeVarInt(recipe.outEffects.size());
            for(ConduitEffect effect : recipe.outEffects){
                ConduitEffect.STREAM_CODEC.encode(buffer, effect);
            }
            MathUtil.RgbColor.STREAM_CODEC.encode(buffer, recipe.color);
        }
    }
}
