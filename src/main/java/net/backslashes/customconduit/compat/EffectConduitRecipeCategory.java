package net.backslashes.customconduit.compat;

import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.backslashes.customconduit.CustomConduit;
import net.backslashes.customconduit.block.ModBlocks;
import net.backslashes.customconduit.recipe.EffectConduitRecipe;
import net.backslashes.customconduit.recipe.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffectConduitRecipeCategory implements IRecipeCategory<EffectConduitRecipe> {
    private static final ResourceLocation BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/jei_bg.png");
    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 100;
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, ModRecipes.EFFECT_CONDUIT_RECIPE_ID);
    public static final RecipeType<EffectConduitRecipe> EFFECT_CONDUIT_RECIPE_TYPE = new RecipeType<>(UID, EffectConduitRecipe.class);

    private final IDrawable icon;

    public EffectConduitRecipeCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.EFFECT_CONDUIT));
    }

    @Override
    public @NotNull RecipeType<EffectConduitRecipe> getRecipeType() {
        return EFFECT_CONDUIT_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        // TODO: use translatable.
        return Component.literal("Effect Conduit");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EffectConduitRecipe recipe, @NotNull IFocusGroup focuses) {
        var ingredients = recipe.getIngredients();
        // Frame.
        builder.addSlot(RecipeIngredientRole.INPUT, 42, 21).addIngredients(ingredients.get(0));

//        for(int i=0; i<recipe.outEffects().size(); ++i){
//            builder.addSlot(RecipeIngredientRole.OUTPUT, 30, 10 + 20*i).addItemStack(new ItemStack(Items.POTION, 1));
//        }
    }

    @Override
    public void draw(
            @NotNull EffectConduitRecipe recipe,
            @NotNull IRecipeSlotsView recipeSlotsView,
            @NotNull GuiGraphics guiGraphics,
            double mouseX,
            double mouseY
    ) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        guiGraphics.blit(BG_TEXTURE, 0, 0, 0.0f, 0.0f, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, recipe.displayName(), 87, 7, recipe.color().toHexArgb());

        guiGraphics.drawString(Minecraft.getInstance().font, "Frame:", 7, 24, 0xFFFFFF);
    }

    @Override
    public int getWidth() {
        return BG_WIDTH;
    }

    @Override
    public int getHeight() {
        return BG_HEIGHT;
    }
}
