package net.backslashes.customconduit.particle;

import com.mojang.serialization.MapCodec;
import net.backslashes.customconduit.CustomConduit;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, CustomConduit.MODID);

    public static final Supplier<ParticleType<EffectConduitParticles.EffectConduitParticleOptions>> EFFECT_CONDUIT_PARTICLES = PARTICLE_TYPES.register("effect_conduit_particles", () -> new EffectConduitParticles.EffectConduitParticleType(false));

    public static void register(IEventBus bus){
        PARTICLE_TYPES.register(bus);
    }
}
