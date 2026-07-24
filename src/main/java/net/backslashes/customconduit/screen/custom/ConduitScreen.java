package net.backslashes.customconduit.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import net.backslashes.customconduit.CustomConduit;
import net.backslashes.customconduit.recipe.EffectConduitRecipe;
import net.backslashes.customconduit.recipe.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.backslashes.customconduit.block.entity.EffectConduitBlockEntity.*;

public class ConduitScreen extends AbstractContainerScreen<ConduitMenu> {
    private static final ResourceLocation BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_bg.png");
    private static final ResourceLocation BG_TEXTURE_EMPTY = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_bg_empty.png");
    private static final ResourceLocation FUEL_TEXTURE_EMPTY = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_fuel_empty.png");
    private static final ResourceLocation FUEL_TEXTURE_FULL = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_fuel_full.png");

    private float tickCounter;
    private int originX;
    private int originY;
    private final List<EffectConduitRecipe> recipes;
    private RecipesMenu recipesMenu;
    public ConduitScreen(ConduitMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        recipes = playerInventory.player.level().getRecipeManager().getAllRecipesFor(ModRecipes.EFFECT_CONDUIT_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        imageWidth = 176;
        imageHeight = 180;
        tickCounter = 0.0f;
    }

    @Override
    protected void init() {
        super.init();

        originX = (width - imageWidth) / 2;
        originY = (height - imageHeight) / 2;

        RecipesMenu recipesMenu = new RecipesMenu(
                this,
                this.minecraft,
                90,
                70,
                originY + 6 - 70,
                originX + 43
        );

        this.recipesMenu = this.addRenderableWidget(recipesMenu);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX >= originX + 43 && mouseX <= originX + 132 && mouseY >= originY + 7 && mouseY <= originY + 17) {
            this.recipesMenu.open = !this.recipesMenu.open;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics  guiGraphics, int mouseX, int mouseY) {
        // Don't render the labels :3
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        tickCounter += partialTick;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BG_TEXTURE);

        int selectedRecipeIndex = this.menu.conduitData.get(DATA_SELECTED_RECIPE);
        if(selectedRecipeIndex >= 0){
            guiGraphics.blit(BG_TEXTURE, originX, originY, 0.0f, 0.0f, imageWidth, imageHeight, imageWidth, imageHeight);
            EffectConduitRecipe recipe = this.recipes.get(selectedRecipeIndex);
            int frameLevel = this.menu.conduitData.get(DATA_FRAME_PROGRESS);
            ConduitScreenBase.draw(recipe, guiGraphics, frameLevel, originX, originY);

            ItemStack[] frameItems = recipe.frameBlockIngredient().getItems();
            int frameItemIndex = Math.round(tickCounter / 20.0f) % frameItems.length;
            guiGraphics.renderItem(frameItems[frameItemIndex], originX + ConduitScreenBase.FRAME_SLOT_X, originY + ConduitScreenBase.FRAME_SLOT_Y);
        } else {
            guiGraphics.blit(BG_TEXTURE_EMPTY, originX, originY, 0.0f, 0.0f, imageWidth, imageHeight, imageWidth, imageHeight);
            ConduitScreenBase.drawTitle(guiGraphics, "click to select", 0xFFFFFFFF, originX, originY);
        }

        // Fuel.
        int fuelMax = this.menu.conduitData.get(DATA_FUEL_TIMER_MAX);
        if(fuelMax > 0) {
            int fuelRemaining = this.menu.conduitData.get(DATA_FUEL_REMAINING_TICKS);
            if (fuelRemaining > 0) {
                int progressOffset = (int) ((1.0 - (float) fuelRemaining / fuelMax) * 15);
                guiGraphics.blit(FUEL_TEXTURE_FULL, originX + 67, originY + 46, 0.0f, 0.0f, 42 - progressOffset, 9, 42, 9);
            }
        }
    }

    private static class RecipesMenu extends ScrollPanel {
        private static final ResourceLocation ENTRY_BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_effect_entry_bg.png");
        private static final ResourceLocation ENTRY_BG_ACTIVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_effect_entry_bg_active.png");
        public static final int RECIPE_ENTRY_HEIGHT = 13;
        List<EffectConduitRecipe> recipes;
        ConduitScreen screen;
        public boolean open;
        public RecipesMenu(ConduitScreen screen, Minecraft client, int width, int height, int top, int left) {
            super(client, width, height, top, left);

            this.screen = screen;
            assert client.level != null;
            recipes = client.level.getRecipeManager().getAllRecipesFor(ModRecipes.EFFECT_CONDUIT_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        }

        @Override
        protected int getContentHeight() {
            if(!open){
                return 0;
            }

            return Math.max(height, recipes.size() * RECIPE_ENTRY_HEIGHT);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(!open){
                return false;
            }
            if(this.screen.minecraft != null && this.screen.minecraft.gameMode != null) {
                if(mouseX >= this.left && mouseX <= this.left + 86){
                    int id = (int) (mouseY - this.top + this.scrollDistance) / RECIPE_ENTRY_HEIGHT;
                    if (id >= 0 && id < recipes.size()) {
                        this.screen.minecraft.gameMode.handleInventoryButtonClick(this.screen.menu.containerId, id);
                    }
                }
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        protected void drawBackground(GuiGraphics guiGraphics, Tesselator tess, float partialTick) {
            if(open){
                super.drawBackground(guiGraphics, tess, partialTick);
            }
            // Don't draw the default translucent dark rectangle :)
        }

        @Override
        protected void drawPanel(@NotNull GuiGraphics guiGraphics, int entryRight, int relativeY, @NotNull Tesselator tess, int mouseX, int mouseY) {
            if(!open){
                return;
            }

            for(int i=0; i<recipes.size(); ++i){
                EffectConduitRecipe recipe = recipes.get(i);
                int entryY = top - (int) scrollDistance + i * RECIPE_ENTRY_HEIGHT;
                int color = recipe.color().toHexArgb();

                int selectedRecipe = this.screen.menu.conduitData.get(DATA_SELECTED_RECIPE);
                boolean selected = i == selectedRecipe;

                ResourceLocation backgroundTexture = selected ? ENTRY_BG_ACTIVE_TEXTURE : ENTRY_BG_TEXTURE;
                guiGraphics.blit(backgroundTexture, left, entryY, 0, 0.0f, 0.0f, 86, RECIPE_ENTRY_HEIGHT, 86, RECIPE_ENTRY_HEIGHT);
                guiGraphics.drawCenteredString(
                        this.screen.font,
                        recipe.displayName(),
                        left + 46,
                        entryY + (RECIPE_ENTRY_HEIGHT - this.screen.font.lineHeight) / 2,
                        color
                );

            }
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.HOVERED;
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {

        }
    }
}
