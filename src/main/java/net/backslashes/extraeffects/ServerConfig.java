package net.backslashes.extraeffects;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue FLIGHT_SPEED_PER_LEVEL = BUILDER
            .comment("The flight speed per level of Flight. Vanilla creative flight speed is 0.05.")
            .defineInRange("FlightSpeedPerLevel", 0.025, 0.0, 100.0);

    static final ModConfigSpec SPEC = BUILDER.build();
}
