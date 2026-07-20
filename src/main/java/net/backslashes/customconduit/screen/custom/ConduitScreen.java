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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.backslashes.customconduit.block.entity.EffectConduitBlockEntity.DATA_FRAME_PROGRESS;
import static net.backslashes.customconduit.block.entity.EffectConduitBlockEntity.DATA_SELECTED_RECIPE;

public class ConduitScreen extends AbstractContainerScreen<ConduitMenu> {
    private static final ResourceLocation BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_bg.png");
    private static final ResourceLocation CORNER_TEXTURE_BOTTOM_LEFT = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_corner_bottom_left.png");
    private static final ResourceLocation CORNER_TEXTURE_BOTTOM_RIGHT = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_corner_bottom_right.png");
    private static final ResourceLocation CORNER_TEXTURE_TOP_LEFT = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_corner_top_left.png");
    private static final ResourceLocation CORNER_TEXTURE_TOP_RIGHT = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_corner_top_right.png");
    private RecipesMenu recipesMenu;

    public ConduitScreen(ConduitMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.recipesMenu = new RecipesMenu(
                this,
                this.minecraft,
                79,
                76,
                (height - imageHeight)/2 + 5,
                (width - imageWidth)/2 + 8
        );

        this.addRenderableWidget(this.recipesMenu);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics  guiGraphics, int mouseX, int mouseY) {
        // Don't render the labels :3
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BG_TEXTURE);

        int x = (width - imageWidth)/2;
        int y = (height - imageHeight)/2;
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int frameLevel = this.menu.conduitData.get(DATA_FRAME_PROGRESS);
        if(frameLevel >= 1){
            RenderSystem.setShaderTexture(0, CORNER_TEXTURE_BOTTOM_LEFT);
            guiGraphics.blit(CORNER_TEXTURE_BOTTOM_LEFT, x + 93, y + 45, 0.0f, 0.0f, 35, 35, 35, 35);
        }
        if(frameLevel >= 2){
            guiGraphics.blit(CORNER_TEXTURE_BOTTOM_RIGHT, x + 131, y + 45, 0.0f, 0.0f, 35, 35, 35, 35);
        }
        if(frameLevel >= 3){
            guiGraphics.blit(CORNER_TEXTURE_TOP_LEFT, x + 93, y + 7, 0.0f, 0.0f, 35, 35, 35, 35);
        }
        if(frameLevel >= 4){
            guiGraphics.blit(CORNER_TEXTURE_TOP_RIGHT, x + 131, y + 7, 0.0f, 0.0f, 35, 35, 35, 35);
        }
    }

    private static class RecipesMenu extends ScrollPanel {
        private static final ResourceLocation ENTRY_BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_effect_entry_bg.png");
        private static final ResourceLocation ENTRY_BG_ACTIVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_effect_entry_bg_active.png");
        public static final int RECIPE_ENTRY_HEIGHT = 15;
        List<EffectConduitRecipe> recipes;
        ConduitScreen screen;
        public RecipesMenu(ConduitScreen screen, Minecraft client, int width, int height, int top, int left) {
            super(client, width, height, top, left);

            this.screen = screen;
            assert client.level != null;
            recipes = client.level.getRecipeManager().getAllRecipesFor(ModRecipes.EFFECT_CONDUIT_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        }

        @Override
        protected int getContentHeight() {
            return Math.max(height, recipes.size() * RECIPE_ENTRY_HEIGHT);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(this.screen.minecraft != null && this.screen.minecraft.gameMode != null) {
                int id = (int) (mouseY - this.top + this.scrollDistance) / RECIPE_ENTRY_HEIGHT;
                if (id >= 0 && id < recipes.size()) {
                    this.screen.minecraft.gameMode.handleInventoryButtonClick(this.screen.menu.containerId, id);
                }
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        protected void drawBackground(GuiGraphics guiGraphics, Tesselator tess, float partialTick) {
            // Don't draw the default translucent dark rectangle :)
        }

        @Override
        protected void drawPanel(@NotNull GuiGraphics guiGraphics, int entryRight, int relativeY, @NotNull Tesselator tess, int mouseX, int mouseY) {
            for(int i=0; i<recipes.size(); ++i){
                EffectConduitRecipe recipe = recipes.get(i);
                int entryY = top - (int) scrollDistance + i * RECIPE_ENTRY_HEIGHT;
                int color = recipe.color().toHexArgb();

                int selectedRecipe = this.screen.menu.conduitData.get(DATA_SELECTED_RECIPE);
                boolean selected = i == selectedRecipe;

                ResourceLocation backgroundTexture = selected ? ENTRY_BG_ACTIVE_TEXTURE : ENTRY_BG_TEXTURE;
                guiGraphics.blit(backgroundTexture, left, entryY, 0, 0.0f, 0.0f, 73, 15, 73, 15);
                guiGraphics.drawString(
                        this.screen.font,
                        recipe.displayName(),
                        left + 2,
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
