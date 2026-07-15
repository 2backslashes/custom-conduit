package net.backslashes.extraeffects.recipe;

import net.backslashes.extraeffects.ExtraEffects;
import net.backslashes.extraeffects.effect.ModEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ExtraEffects.MODID)
public class ModRecipes {
    public static final String EFFECT_CONDUIT_RECIPE_ID = "growth_chamber";
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ExtraEffects.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ExtraEffects.MODID);

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
        protected void buildRecipes(RecipeOutput output) {
            super.buildRecipes(output);
        }
    }

    @SubscribeEvent // on the mod event bus
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new EffectConduitRecipeProvider(generator.getPackOutput(), lookupProvider));
    }
}
