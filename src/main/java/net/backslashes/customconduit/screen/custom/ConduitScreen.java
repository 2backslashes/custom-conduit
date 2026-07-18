package net.backslashes.customconduit.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import net.backslashes.customconduit.CustomConduit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import org.jetbrains.annotations.NotNull;

public class ConduitScreen extends AbstractContainerScreen<ConduitMenu> {
    private static final ResourceLocation BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "textures/gui/conduit/conduit_bg.png");
    private RecipesMenu recipesList;

    public ConduitScreen(ConduitMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);


    }

    @Override
    protected void init() {
        super.init();
        this.recipesList = new RecipesMenu(
                this.minecraft,
                79,
                76,
                (height - imageHeight)/2 + 5,
                (width - imageWidth)/2 + 8
        );
        this.addRenderableWidget(this.recipesList);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BG_TEXTURE);

        int x = (width - imageWidth)/2;
        int y = (height - imageHeight)/2;
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    private static class RecipesMenu extends ScrollPanel {

        public RecipesMenu(Minecraft client, int width, int height, int top, int left) {
            super(client, width, height, top, left);
        }

        @Override
        protected int getContentHeight() {
            return 2000;
        }

        @Override
        protected void drawPanel(@NotNull GuiGraphics guiGraphics, int entryRight, int relativeY, @NotNull Tesselator tess, int mouseX, int mouseY) {


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
