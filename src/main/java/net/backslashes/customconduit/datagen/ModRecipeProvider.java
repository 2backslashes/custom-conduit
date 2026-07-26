package net.backslashes.customconduit.datagen;

import net.backslashes.customconduit.CustomConduit;
import net.backslashes.customconduit.MathUtil;
import net.backslashes.customconduit.block.ModBlocks;
import net.backslashes.customconduit.recipe.EffectConduitRecipe;
import net.backslashes.customconduit.recipe.EffectConduitRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        super.buildRecipes(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.EFFECT_CONDUIT.get())
                .pattern("OGO")
                .pattern("GHG")
                .pattern("OGO")
                .define('O', Ingredient.of(Tags.Items.OBSIDIANS_NORMAL))
                .define('G', Ingredient.of(Tags.Items.INGOTS_GOLD))
                .define('H', Ingredient.of(Items.HEART_OF_THE_SEA))
                .unlockedBy("has_heart_of_the_sea", has(Items.HEART_OF_THE_SEA))
                .save(output);

        buildConduitRecipes(output);
    }

    private static void buildConduitRecipes(RecipeOutput output){
        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Watcher's Crown",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                1200,
                Ingredient.of(Items.GLOW_BERRIES, Items.GLOWSTONE_DUST, Items.GLOW_INK_SAC),
                Ingredient.of(Items.GLOWSTONE, Items.SHROOMLIGHT),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.GLOWING,
                                0
                        ),
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.NIGHT_VISION,
                                0
                        )
                ),
                new MathUtil.RgbColor(0.3f, 1.0f, 0.1f),
                true,
                true,
                true
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "watchers_crown"));

        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Winter's Blessing",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                1200,
                Ingredient.of(Items.SNOWBALL),
                Ingredient.of(Items.BLUE_ICE),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.FIRE_RESISTANCE,
                                0
                        )
                ),
                new MathUtil.RgbColor(0.5f, 0.8f, 1.0f),
                true,
                true,
                false
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "winters_blessing"));

        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Acceleration",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                1200,
                Ingredient.of(Items.SUGAR),
                Ingredient.of(Items.REDSTONE_BLOCK),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.MOVEMENT_SPEED,
                                0
                        ),
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.DIG_SPEED,
                                1
                        )
                ),
                new MathUtil.RgbColor(1.0f, 0.3f, 0.1f),
                true,
                false,
                false
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "acceleration"));

        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Soul Snare",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                0,
                Ingredient.EMPTY,
                Ingredient.of(Items.SOUL_SOIL, Items.SOUL_SAND),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.MOVEMENT_SLOWDOWN,
                                1
                        )
                ),
                new MathUtil.RgbColor(0.4f, 0.2f, 0.1f),
                false,
                false,
                true
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "soul_snare"));

        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Animal Friend",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                0,
                Ingredient.EMPTY,
                Ingredient.of(Items.HAY_BLOCK),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.REGENERATION,
                                0
                        )
                ),
                new MathUtil.RgbColor(0.2f, 0.8f, 0.0f),
                false,
                true,
                false
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "animal_friend"));

        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Nourishment",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                600,
                Ingredient.of(Items.BREAD),
                Ingredient.of(Items.CAKE),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.SATURATION,
                                0
                        )
                ),
                new MathUtil.RgbColor(1.0f, 0.8f, 0.4f),
                true,
                false,
                false
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "nourishment"));

        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "The Hollowing",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                0,
                Ingredient.EMPTY,
                Ingredient.of(Tags.Items.GLASS_BLOCKS),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.GLOWING,
                                0
                        ),
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.INVISIBILITY,
                                0
                        ),
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.SLOW_FALLING,
                                0
                        ),
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.WEAKNESS,
                                2
                        ),
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.DIG_SLOWDOWN,
                                0
                        )
                ),
                new MathUtil.RgbColor(0.9f, 0.9f,0.9f),
                true,
                true,
                true
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "hollowing"));

        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Fool's Regret",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                0,
                Ingredient.EMPTY,
                Ingredient.of(ItemTags.DIRT),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.CONFUSION,
                                0
                        ),
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.DARKNESS,
                                0
                        ),
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.JUMP,
                                49
                        )
                ),
                new MathUtil.RgbColor(0.2f, 0.25f,0.0f),
                true,
                false,
                false
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "fools_regret"));

        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Rage",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                0,
                Ingredient.EMPTY,
                Ingredient.of(Items.NETHER_BRICKS, Items.RED_NETHER_BRICKS),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.DAMAGE_BOOST,
                                4
                        )
                ),
                new MathUtil.RgbColor(0.7f, 0.0f,0.0f),
                true,
                true,
                true
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "rage"));

        new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Lunar Gravity",
                1,
                EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                0,
                Ingredient.EMPTY,
                Ingredient.of(Items.END_STONE, Items.END_STONE_BRICKS),
                List.of(
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.JUMP,
                                3
                        ),
                        new EffectConduitRecipe.ConduitEffect(
                                MobEffects.SLOW_FALLING,
                                0
                        )
                ),
                new MathUtil.RgbColor(0.85f, 0.9f,0.7f),
                true,
                true,
                true
        )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "lunar_gravity"));
    }
}
