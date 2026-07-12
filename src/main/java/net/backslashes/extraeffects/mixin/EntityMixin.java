package net.backslashes.extraeffects.mixin;

import net.backslashes.extraeffects.effect.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    public void skipMakeStuckInBlockWhenUnimpeded(BlockState state, Vec3 motionMultiplier, CallbackInfo ci) {
        if((Object) this instanceof LivingEntity entity){
            if(entity.hasEffect(ModEffects.UNIMPEDED.effect)){
                entity.resetFallDistance();
                ci.cancel();
            }
        }
    }

    @Inject(method="getBlockJumpFactor", at=@At("RETURN"), cancellable = true)
    public void skipBlockJumpFactorWhenUnimpeded(CallbackInfoReturnable<Float> cir){
        if((Object) this instanceof LivingEntity entity){
            if(entity.hasEffect(ModEffects.UNIMPEDED.effect)){
                float value = cir.getReturnValue();
                cir.setReturnValue(Float.max(1.0f, value));
            }
        }
    }
}
