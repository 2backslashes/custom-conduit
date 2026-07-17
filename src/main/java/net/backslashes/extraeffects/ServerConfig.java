package net.backslashes.extraeffects;

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

    public static final ModConfigSpec.DoubleValue FLIGHT_SPEED_PER_LEVEL = BUILDER
            .comment("The flight speed per level of Flight. Vanilla creative flight speed is 0.05.")
            .defineInRange("FlightSpeedPerLevel", 0.025, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue REACH_BLOCKS_PER_LEVEL = BUILDER
            .comment("The distance in blocks added per effect level of Miner's Reach.")
            .defineInRange("ReachBlocksPerLevel", 4.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue REACH_ENTITIES_PER_LEVEL = BUILDER
            .comment("The distance in blocks added per effect level of Fighter's Reach.")
            .defineInRange("ReachEntitiesPerLevel", 2.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue RABBIT_SPEED_PER_LEVEL = BUILDER
            .comment("The speed added per level of Rabbit Form.")
            .defineInRange("RabbitFormSpeedPerLevel", 0.3, 0.0, 10.0);

    public static final ModConfigSpec.DoubleValue RABBIT_JUMP_PER_LEVEL = BUILDER
            .comment("The jump boost added per level of Rabbit Form.")
            .defineInRange("RabbitFormJumpPerLevel", 0.3, 0.0, 10.0);

    public static final ModConfigSpec.DoubleValue EXPLOSIVE_POWER_PER_LEVEL = BUILDER
            .comment("Explosion radius added per level of Volatility.")
            .defineInRange("ExplosivePowerPerLevel", 2.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue PEACEFUL_MINER_SPEED_PER_LEVEL = BUILDER
            .comment("Mining speed added per level of Peaceful Miner.")
            .defineInRange("PeacefulMinerSpeedPerLevel", 0.2, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue PEACEFUL_MINER_DAMAGE_PER_LEVEL = BUILDER
            .comment("Damage factor subtracted per level of Peaceful Miner.")
            .defineInRange("PeacefulMinerDamagePerLevel", 0.4, 0.0, 100.0);

    static final ModConfigSpec SPEC = BUILDER.build();
}
