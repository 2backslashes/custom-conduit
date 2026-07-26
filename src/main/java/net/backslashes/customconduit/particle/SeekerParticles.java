package net.backslashes.customconduit.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.backslashes.customconduit.MathUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.backslashes.customconduit.MathUtil.easeOutCubic;
import static net.backslashes.customconduit.MathUtil.lerpf;

public class SeekerParticles extends TextureSheetParticle {
    private final Vec3 origin;
    private final Vec3 destination;
    private final SpriteSet spriteSet;
    protected SeekerParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, Vec3 destination) {
        super(level, x, y, z);
        this.setSpriteFromAge(spriteSet);
        this.origin = new Vec3(x,y,z);
        this.spriteSet = spriteSet;
        this.destination = destination;
        this.lifetime = 140;
        this.quadSize = 0.05f;
        this.roll = (float) (Math.round(level.getRandom().nextFloat() * 4) * Math.PI / 2);
        this.oRoll = roll;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float factor = easeOutCubic(Math.clamp((float) this.age / (this.lifetime - 120), 0.0f, 1.0f));
        return this.quadSize * factor;
    }

    @Override
    public void tick(){
        super.tick();
        this.setSpriteFromAge(this.spriteSet);
        float factor = easeOutCubic(Math.clamp((float) this.age / (this.lifetime - 120), 0.0f, 1.0f));
        this.x = lerpf((float)origin.x, (float)destination.x, factor);
        this.y = lerpf((float)origin.y, (float)destination.y, factor);
        this.z = lerpf((float)origin.z, (float)destination.z, factor);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record SeekerParticleOptions(
            Vec3 destination
    ) implements ParticleOptions {
        @Override
        public @NotNull ParticleType<?> getType() {
            return ModParticles.SEEKER_PARTICLES.get();
        }
    }

    public static class SeekerParticleType extends ParticleType<SeekerParticleOptions> {
        protected SeekerParticleType(boolean overrideLimitter) {
            super(overrideLimitter);
        }

        public static final MapCodec<SeekerParticleOptions> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Vec3.CODEC.fieldOf("destination").forGetter(SeekerParticleOptions::destination)
        ).apply(inst, SeekerParticleOptions::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SeekerParticleOptions> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, SeekerParticleOptions>() {
            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buffer, SeekerParticleOptions options) {
                buffer.writeVec3(options.destination);
            }

            @Override
            public @NotNull SeekerParticleOptions decode(@NotNull RegistryFriendlyByteBuf buffer) {
                Vec3 dest = buffer.readVec3();
                return new SeekerParticleOptions(dest);
            }
        };


        @Override
        public @NotNull MapCodec<SeekerParticleOptions> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, SeekerParticleOptions> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static class Provider implements ParticleProvider<SeekerParticleOptions> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet){
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(
                @NotNull SeekerParticles.SeekerParticleOptions options,
                @NotNull ClientLevel clientLevel,
                double px,
                double py,
                double pz,
                double vx,
                double vy,
                double vz
        ) {
            return new SeekerParticles(clientLevel, px, py, pz, spriteSet, options.destination);
        }
    }
}
