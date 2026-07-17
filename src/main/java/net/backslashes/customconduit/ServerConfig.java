package net.backslashes.customconduit;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue CONDUIT_TICKS_PER_REFRESH = BUILDER
            .comment("The Effect Conduit will check its frame and reapply effects every N ticks. Lower values may hurt performance.")
            .defineInRange("ConduitTicksPerRefresh", 40, 1, 1200);

    public static final ModConfigSpec.IntValue CONDUIT_EFFECT_DURATION_TICKS = BUILDER
            .comment("How long effects granted by the Effect Conduit should last in ticks. This value should always be higher than the refresh rate. Note that night vision will flicker with less than 10 seconds remaining.")
            .defineInRange("ConduitEffectDurationTicks", 260, 1, 1200);

    static final ModConfigSpec SPEC = BUILDER.build();
}
