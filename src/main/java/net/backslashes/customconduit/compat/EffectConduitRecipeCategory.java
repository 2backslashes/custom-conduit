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
import net.backslashes.customconduit.MathUtil;
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
    private static final ResourceLocation FUEL_BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/jei_fuel.png");
    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 94;
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
        if(!recipe.fuelIngredient().isEmpty()){
            builder.addSlot(RecipeIngredientRole.INPUT, 67, 34).addIngredients(ingredients.get(1));
        }
    }

    public static String effectAmplifierToString(int amplifier){
        final String[] numerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        if(amplifier < numerals.length){
            return numerals[amplifier];
        }

        return Integer.toString(amplifier + 1);
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
        var font = Minecraft.getInstance().font;

        guiGraphics.blit(BG_TEXTURE, 0, 0, 0.0f, 0.0f, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
        guiGraphics.drawCenteredString(font, recipe.displayName(), 87, 7, recipe.color().toHexArgb());

        int textColor = 0xFFFFFF;
        guiGraphics.drawString(font, "Frame:", 7, 24, textColor);

        // Effects.
        double minRange = Double.MAX_VALUE;
        double maxRange = 0.0;
        guiGraphics.drawString(font, "Effects", 118, 22, textColor);
        for(int i=0; i<recipe.outEffects().size(); ++i){
            var effect = recipe.outEffects().get(i);
            int y = 34 + 11 * i;
            guiGraphics.drawString(font, effectAmplifierToString(effect.amplifier()), 118, y, textColor);
            guiGraphics.drawString(font, effect.effect().value().getDisplayName(), 132, y, textColor);
            minRange = Math.min(minRange, effect.minRange());
            maxRange = Math.max(maxRange, effect.maxRange());
        }

        // Frame counts.
        int minBlockCount = recipe.minFrameBlockCount();
        int maxBlockCount = recipe.maxFrameBlockCount();
        for(int i=0; i<4; ++i){
            int blockCount = (int) Math.ceil(MathUtil.lerpf(minBlockCount, maxBlockCount, i/3.0f));
            int range = Math.round(MathUtil.lerpf((float)minRange, (float)maxRange, i/3.0f));
            guiGraphics.drawString(font, Integer.toString(blockCount), 19, 42 + 12 * i, textColor);
            guiGraphics.drawString(font, Integer.toString(range), 46, 42 + 12 * i, textColor);
        }

        // Fuel.
        if(!recipe.fuelIngredient().isEmpty()){
            guiGraphics.blit(FUEL_BG_TEXTURE, 65, 20, 0.0f, 0.0f, 49, 69, 49, 69);
            guiGraphics.drawCenteredString(font, "Fuel", 89, 22, textColor);
            guiGraphics.drawString(font, recipe.fuelBurnTime() + "t", 85, 39, textColor);
        }
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
