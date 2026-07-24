package net.backslashes.customconduit.screen.custom;

import net.backslashes.customconduit.recipe.EffectConduitRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ConduitScreenBase {
    public static int TEXT_COLOR = 0xFFFFFF;
    public static Font FONT = Minecraft.getInstance().font;
    public static int BG_COLOR = 0xBEC3ED;

    public static int FUEL_SLOT_X = 80;
    public static int FUEL_SLOT_Y = 28;
    public static int FRAME_SLOT_X = 8;
    public static int FRAME_SLOT_Y = 28;
    private static String effectAmplifierToString(int amplifier){
        final String[] numerals = {"I", "II", "III", "IV", "V", "VI"};
        if(amplifier < numerals.length){
            return numerals[amplifier];
        }

        return "x" +(amplifier + 1);
    }

    private static String blockCountToString(int count){
        if(count >= 100000){
            return "???";
        }
        if(count >= 10000){
            return (count / 1000) + "k";
        }
        if(count >= 1000){
            return (count / 1000) + "." + ((count % 1000) / 100) + "k";
        }
        return Integer.toString(count);
    }

    public static void drawTitle(GuiGraphics guiGraphics, String title, int color, int baseX, int baseY){
        guiGraphics.drawCenteredString(FONT, title, baseX + 87, baseY + 7, color);
    }

    public static void draw(EffectConduitRecipe recipe, GuiGraphics guiGraphics, int activeLevel, int baseX, int baseY){
        drawTitle(guiGraphics, recipe.displayName(), recipe.color().toHexArgb(), baseX, baseY);

        // Effects.
        for(int i=0; i<recipe.outEffects().size(); ++i){
            var effect = recipe.outEffects().get(i);
            int y = baseY + 27 + 11 * i;
            guiGraphics.drawString(FONT, effectAmplifierToString(effect.amplifier()), baseX + 115, y, TEXT_COLOR);
            guiGraphics.drawString(FONT, effect.effect().value().getDisplayName(), baseX + 129, y, TEXT_COLOR);
        }

        // Frame ingredient.
        int frameDiameter = (recipe.frameSize() + 1) * 2 + 1;
        guiGraphics.drawString(FONT, frameDiameter + "x" + frameDiameter, baseX + 26, baseY + 32, TEXT_COLOR);

        // Frame counts.
        for(int i=0; i<4; ++i){
            EffectConduitRecipe.ConduitTier tier = EffectConduitRecipe.ConduitTier.DEFAULT_TIERS.get(recipe.frameSize()).get(i);
            int blockCount = tier.frameBlockThreshold();
            int range = tier.effectRange();
            int y = baseY + 48 + 12 * i;
            boolean active = activeLevel > i;

            int textColor = active ? TEXT_COLOR : (TEXT_COLOR | 0x80000000);

            guiGraphics.drawCenteredString(FONT, blockCountToString(blockCount), baseX + 26, y, textColor);
            guiGraphics.drawCenteredString(FONT, blockCountToString(range), baseX + 52, y, textColor);

            if(!active){
                guiGraphics.fill(baseX +7, baseY + 47 + 12 * i, baseX + 60, baseY + 58 + 12 * i, BG_COLOR | 0xB0000000);
            }
        }
    }
}
