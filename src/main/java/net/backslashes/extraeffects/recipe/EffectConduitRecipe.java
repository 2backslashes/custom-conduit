package net.backslashes.extraeffects.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
        int minFrameBlockCount,
        int maxFrameBlockCount,
        double minRange,
        double maxRange,
        Ingredient frameBlockIngredient,
        List<Pair<Holder<MobEffect>, Integer>> outEffects
) implements Recipe<EffectConduitRecipeInput> {
    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(frameBlockIngredient);
    }

    // We don't actually use this.
    @Override
    public boolean matches(@NotNull EffectConduitRecipeInput effectConduitRecipeInput, Level level) {
        return false;
    }

    public List<BlockState> computeValidFrameBlocks(HashMap<Block, List<BlockState>> frameBlocksByType){
        List<BlockState> validBlocks = new ArrayList<>();
        for (Map.Entry<Block, List<BlockState>> entry : frameBlocksByType.entrySet()) {
            if(frameBlockIngredient.test(new ItemStack(entry.getKey()))){
                validBlocks.addAll(entry.getValue());
            }
        }
        return validBlocks;
    }

    public double computeEffectRange(int validFrameBlockCount){
        if(validFrameBlockCount < minFrameBlockCount){
            return 0.0;
        }

        double factor = Double.min(1.0, (validFrameBlockCount - minFrameBlockCount) / (double) (maxFrameBlockCount - minFrameBlockCount));
        return factor * (maxRange - minRange) + minRange;
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
                Codec.INT.fieldOf("minFrameBlockCount").forGetter(EffectConduitRecipe::minFrameBlockCount),
                Codec.INT.fieldOf("maxFrameBlockCount").forGetter(EffectConduitRecipe::maxFrameBlockCount),
                Codec.DOUBLE.fieldOf("minRange").forGetter(EffectConduitRecipe::minRange),
                Codec.DOUBLE.fieldOf("maxRange").forGetter(EffectConduitRecipe::maxRange),
                Ingredient.CODEC_NONEMPTY.fieldOf("frame").forGetter(EffectConduitRecipe::frameBlockIngredient),
                Codec.pair(MobEffect.CODEC, Codec.INT).listOf().fieldOf("effects").forGetter((recipe) -> recipe.outEffects)
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

        private static EffectConduitRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int minFrameBlockCount = buffer.readByte();
            int maxFrameBlockCount = buffer.readByte();
            double minRange = buffer.readDouble();
            double maxRange = buffer.readDouble();
            Ingredient frameBlockIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            List<Pair<Holder<MobEffect>, Integer>> effects = new ArrayList<>();
            int effectCount = buffer.readVarInt();
            for(int i=0; i<effectCount; ++i){
                Holder<MobEffect> effect = MobEffect.STREAM_CODEC.decode(buffer);
                int amplifier = buffer.readInt();
                effects.add(new Pair<>(effect, amplifier));
            }
            return new EffectConduitRecipe(minFrameBlockCount, maxFrameBlockCount, minRange, maxRange, frameBlockIngredient, effects);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, EffectConduitRecipe recipe) {
            buffer.writeByte(recipe.minFrameBlockCount);
            buffer.writeByte(recipe.maxFrameBlockCount);
            buffer.writeDouble(recipe.minRange);
            buffer.writeDouble(recipe.maxRange);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.frameBlockIngredient);
            buffer.writeVarInt(recipe.outEffects.size());
            for(Pair<Holder<MobEffect>, Integer> pair : recipe.outEffects){
                MobEffect.STREAM_CODEC.encode(buffer, pair.getFirst());
                buffer.writeInt(pair.getSecond());
            }
        }
    }
}
