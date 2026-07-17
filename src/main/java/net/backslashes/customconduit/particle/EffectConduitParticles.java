package net.backslashes.customconduit.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.backslashes.customconduit.MathUtil;
import net.backslashes.customconduit.recipe.EffectConduitRecipe;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffectConduitParticles extends TextureSheetParticle {
    protected EffectConduitParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, MathUtil.RgbColor color) {
        super(level, x, y, z);

        this.friction = -0.4f;
        this.lifetime = 40;
        this.setSpriteFromAge(spriteSet);
        this.rCol = color.r();
        this.gCol = color.g();
        this.bCol = color.b();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record EffectConduitParticleOptions(
            Vec3 destination,
            MathUtil.RgbColor color
    ) implements ParticleOptions {

        @Override
        public @NotNull ParticleType<?> getType() {
            return ModParticles.EFFECT_CONDUIT_PARTICLES.get();
        }
    }

    public static class EffectConduitParticleType extends ParticleType<EffectConduitParticles.EffectConduitParticleOptions> {
        protected EffectConduitParticleType(boolean overrideLimitter) {
            super(overrideLimitter);
        }

        public static final MapCodec<EffectConduitParticleOptions> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Vec3.CODEC.fieldOf("destination").forGetter(EffectConduitParticleOptions::destination),
                MathUtil.RgbColor.CODEC.fieldOf("color").forGetter(EffectConduitParticleOptions::color)
        ).apply(inst, EffectConduitParticleOptions::new));

        @Override
        public MapCodec<EffectConduitParticleOptions> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, EffectConduitParticleOptions> streamCodec() {
            return null;
        }
    }

    public static class Provider implements ParticleProvider<EffectConduitParticleOptions>{
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet){
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(
                @NotNull EffectConduitParticleOptions options,
                @NotNull ClientLevel clientLevel,
                double px,
                double py,
                double pz,
                double vx,
                double vy,
                double vz
        ) {
            return new EffectConduitParticles(clientLevel, px, py, pz, spriteSet, options.color);
        }
    }
}
