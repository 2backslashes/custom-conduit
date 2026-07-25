package net.backslashes.customconduit.recipe;

import net.backslashes.customconduit.CustomConduit;
import net.backslashes.customconduit.MathUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.internal.NeoForgeItemTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = CustomConduit.MODID)
public class ModRecipes {
    public static final String EFFECT_CONDUIT_RECIPE_ID = "effect_conduit";
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, CustomConduit.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, CustomConduit.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EffectConduitRecipe>> EFFECT_CONDUIT_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(EFFECT_CONDUIT_RECIPE_ID, EffectConduitRecipe.EffectConduitRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<EffectConduitRecipe>> EFFECT_CONDUIT_RECIPE_TYPE = RECIPE_TYPES.register(EFFECT_CONDUIT_RECIPE_ID, () -> new RecipeType<>() {
        @Override
        public String toString() {
            return EFFECT_CONDUIT_RECIPE_ID;
        }
    });

    public static void register(IEventBus eventBus){
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }

    public static class EffectConduitRecipeProvider extends RecipeProvider {
        public EffectConduitRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected void buildRecipes(@NotNull RecipeOutput output) {
            super.buildRecipes(output);
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
                Ingredient.of(Items.REDSTONE),
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
                "Divine Nourishment",
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
            )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "divine_nourishment"));

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
                        MobEffects.MOVEMENT_SPEED,
                            20
                    )
                ),
                new MathUtil.RgbColor(0.3f, 0.4f,0.0f),
                    true,
                    false,
                    true
            )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "fools_regret"));

            new EffectConduitRecipeBuilder(new EffectConduitRecipe(
                "Rage",
                    1,
                    EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(1),
                0,
                Ingredient.EMPTY,
                Ingredient.of(Items.NETHER_BRICK, Items.RED_NETHER_BRICKS),
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
                new MathUtil.RgbColor(0.7f, 0.7f,0.7f),
                    true,
                    true,
                    true
            )).save(output, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "lunar_gravity"));
        }
    }

    @SubscribeEvent // on the mod event bus
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new EffectConduitRecipeProvider(generator.getPackOutput(), lookupProvider));
    }
}
