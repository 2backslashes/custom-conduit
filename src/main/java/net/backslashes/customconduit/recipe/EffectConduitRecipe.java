package net.backslashes.customconduit.recipe;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.backslashes.customconduit.MathUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record EffectConduitRecipe(
        int frameSize,
        String displayName,
//        List<ConduitTier> tiers,
        int fuelBurnTime,
        Ingredient fuelIngredient,
        Ingredient frameBlockIngredient,
        List<ConduitEffect> outEffects,
        MathUtil.RgbColor color
) implements Recipe<EffectConduitRecipeInput> {
    public record ConduitTier(
        int frameBlockThreshold,
        int effectRange
    ) {
        public static List<List<ConduitTier>> DEFAULT_TIERS = List.of(
            // Size=0, frame=3x3.
            List.of(
                new ConduitTier(8, 16),
                new ConduitTier(12, 32),
                new ConduitTier(16, 64),
                new ConduitTier(20, 96)
            ),
            // Size=1, frame=5x5.
            List.of(
                new ConduitTier(16, 16),
                new ConduitTier(24, 32),
                new ConduitTier(32, 64),
                new ConduitTier(44, 96)
            ),
            // Size=2, frame=7x7.
            List.of(
                new ConduitTier(24, 16),
                new ConduitTier(36, 32),
                new ConduitTier(48, 64),
                new ConduitTier(68, 96)
            ),
            // Size=3, frame=9x9.
            List.of(
                new ConduitTier(32, 16),
                new ConduitTier(50, 32),
                new ConduitTier(68, 64),
                new ConduitTier(92, 96)
            )
        );
        public static final Codec<ConduitTier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("frameThreshold").forGetter(ConduitTier::frameBlockThreshold),
                Codec.INT.fieldOf("range").forGetter(ConduitTier::effectRange)
        ).apply(inst, ConduitTier::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ConduitTier> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ConduitTier>() {
            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buffer, ConduitTier effect) {
                buffer.writeInt(effect.frameBlockThreshold);
                buffer.writeInt(effect.effectRange);
            }

            @Override
            public @NotNull ConduitTier decode(@NotNull RegistryFriendlyByteBuf buffer) {
                int frameThreshold = buffer.readInt();
                int range = buffer.readInt();
                return new ConduitTier(frameThreshold, range);
            }
        };
    }

    public record ConduitEffect(
        Holder<MobEffect> effect,
        int amplifier
    ){
        public static final Codec<ConduitEffect> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                MobEffect.CODEC.fieldOf("effect").forGetter(ConduitEffect::effect),
                Codec.INT.optionalFieldOf("amplifier", 0).forGetter(ConduitEffect::amplifier)
        ).apply(inst, ConduitEffect::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ConduitEffect> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ConduitEffect>() {
            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buffer, ConduitEffect effect) {
                MobEffect.STREAM_CODEC.encode(buffer, effect.effect);
                buffer.writeInt(effect.amplifier);
            }

            @Override
            public @NotNull ConduitEffect decode(@NotNull RegistryFriendlyByteBuf buffer) {
                Holder<MobEffect> mobEffect = MobEffect.STREAM_CODEC.decode(buffer);
                int amplifier = buffer.readInt();
                return new ConduitEffect(mobEffect, amplifier);
            }
        };
    }

    public List<ConduitTier> getTiers(){
        return ConduitTier.DEFAULT_TIERS.get(this.frameSize());
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        // NonNullList.of doesn't work here. Not sure why, don't care enough to find out.
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(frameBlockIngredient);
        list.add(fuelIngredient);
        return list;
    }

    // We don't actually use this.
    @Override
    public boolean matches(@NotNull EffectConduitRecipeInput effectConduitRecipeInput, @NotNull Level level) {
        return false;
    }

    public <T> void computeValidFrameBlocks(HashMap<Block, List<T>> frameBlocksByType, List<T> outFrameBlocks){
        outFrameBlocks.clear();
        for (Map.Entry<Block, List<T>> entry : frameBlocksByType.entrySet()) {
            ItemStack itemStack = new ItemStack(entry.getKey().asItem(), 1);
            if(frameBlockIngredient.test(itemStack)){
                outFrameBlocks.addAll(entry.getValue());
            }
        }
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull EffectConduitRecipeInput effectConduitRecipeInput, HolderLookup.@NotNull Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return ItemStack.EMPTY;
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
                Codec.INT.optionalFieldOf("frameSize", 1).validate((Integer s) -> {
                    int maxSize = ConduitTier.DEFAULT_TIERS.size() - 1;
                    if(s >= 0 && s <= maxSize){
                        return DataResult.success(s);
                    }
                    return DataResult.error(() -> "Size must be between 0 and " + maxSize);
                }).forGetter(EffectConduitRecipe::frameSize),
                Codec.STRING.fieldOf("displayName").forGetter(EffectConduitRecipe::displayName),
//                ConduitTier.CODEC.listOf(4,4).optionalFieldOf("tiers", ConduitTier.DEFAULT_TIERS).forGetter(EffectConduitRecipe::tiers),
                Codec.INT.optionalFieldOf("fuelBurnTime", 1600).forGetter(EffectConduitRecipe::fuelBurnTime),
                Ingredient.CODEC_NONEMPTY.optionalFieldOf("fuel", Ingredient.EMPTY).forGetter(EffectConduitRecipe::fuelIngredient),
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
            int frameSize = buffer.readInt();
//            int tierCount = buffer.readInt();
//            List<ConduitTier> tiers = new ArrayList<>();
//            for(int i=0; i<tierCount; ++i){
//                ConduitTier tier = ConduitTier.STREAM_CODEC.decode(buffer);
//                tiers.add(tier);
//            }
            int fuelBurnTime = buffer.readInt();
            Ingredient fuelIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient frameBlockIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            List<ConduitEffect> effects = new ArrayList<>();
            int effectCount = buffer.readVarInt();
            for(int i=0; i<effectCount; ++i){
                ConduitEffect effect = ConduitEffect.STREAM_CODEC.decode(buffer);
                effects.add(effect);
            }
            MathUtil.RgbColor color = MathUtil.RgbColor.STREAM_CODEC.decode(buffer);
            return new EffectConduitRecipe(
                    frameSize,
                    displayName,
//                    tiers,
                    fuelBurnTime,
                    fuelIngredient,
                    frameBlockIngredient,
                    effects,
                    color
            );
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, EffectConduitRecipe recipe) {
            ByteBufCodecs.stringUtf8(DISPLAY_NAME_MAX_LENGTH).encode(buffer, recipe.displayName);
            buffer.writeInt(recipe.frameSize);
//            buffer.writeInt(recipe.tiers.frameSize());
//            for(ConduitTier tier : recipe.tiers){
//                ConduitTier.STREAM_CODEC.encode(buffer, tier);
//            }
            buffer.writeInt(recipe.fuelBurnTime);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.fuelIngredient);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.frameBlockIngredient);
            buffer.writeVarInt(recipe.outEffects.size());
            for(ConduitEffect effect : recipe.outEffects){
                ConduitEffect.STREAM_CODEC.encode(buffer, effect);
            }
            MathUtil.RgbColor.STREAM_CODEC.encode(buffer, recipe.color);
        }
    }
}
