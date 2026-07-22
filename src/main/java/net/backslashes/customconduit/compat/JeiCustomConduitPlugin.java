package net.backslashes.customconduit.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.backslashes.customconduit.CustomConduit;
import net.backslashes.customconduit.block.ModBlocks;
import net.backslashes.customconduit.item.ModItems;
import net.backslashes.customconduit.recipe.EffectConduitRecipe;
import net.backslashes.customconduit.recipe.ModRecipes;
import net.backslashes.customconduit.screen.custom.ConduitScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class JeiCustomConduitPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);

        registration.addRecipeCategories(new EffectConduitRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        IModPlugin.super.registerRecipes(registration);

        assert Minecraft.getInstance().level != null;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

        List<EffectConduitRecipe> recipes = manager.getAllRecipesFor(ModRecipes.EFFECT_CONDUIT_RECIPE_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(EffectConduitRecipeCategory.EFFECT_CONDUIT_RECIPE_TYPE, recipes);
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        IModPlugin.super.registerGuiHandlers(registration);
        // TODO:
//        registration.addRecipeClickArea(ConduitScreen.class, x,y,w,h, EffectConduitRecipeCategory.EFFECT_CONDUIT_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        IModPlugin.super.registerRecipeCatalysts(registration);

        registration.addRecipeCatalyst(ModBlocks.EFFECT_CONDUIT.asItem(), EffectConduitRecipeCategory.EFFECT_CONDUIT_RECIPE_TYPE);
    }
}
