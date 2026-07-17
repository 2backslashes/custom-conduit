package net.backslashes.customconduit.recipe;

import net.backslashes.customconduit.block.ModBlocks;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class EffectConduitRecipeBuilder implements RecipeBuilder {
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    protected String group;
    EffectConduitRecipe recipe;

    public EffectConduitRecipeBuilder(EffectConduitRecipe recipe){
        this.recipe = recipe;
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return ModBlocks.EFFECT_CONDUIT.asItem();
    }

    // Saves a recipe using the given RecipeOutput and id. This method is defined in the RecipeBuilder interface.
    @Override
    public void save(RecipeOutput output, @NotNull ResourceLocation id) {
        // Our factory parameters are the result, the block state, and the ingredient.
        // Pass the id, the recipe, and the recipe advancement into the RecipeOutput.
        output.accept(id, this.recipe, null);
    }
}