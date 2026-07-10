package net.backslashes.flighteffect;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue POTION_DURATION = BUILDER
            .comment("The duration (in seconds) of a flight potion.")
            .defineInRange("potionDuration", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> POTION_INGREDIENT = BUILDER
            .comment("The potion ingredient. If left blank, the potion will have no recipe by default.")
            .define("potionIngredient", "minecraft:nether_star");

    static final ModConfigSpec SPEC = BUILDER.build();
}
