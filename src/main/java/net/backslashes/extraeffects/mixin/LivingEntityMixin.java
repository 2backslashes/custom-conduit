package net.backslashes.extraeffects.mixin;

import net.backslashes.extraeffects.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Inject(method="isInvulnerableTo", at=@At("HEAD"), cancellable = true)
    public void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir){
        if (hasEffect(ModEffects.UNIMPEDED)){
            if(source.is(DamageTypeTags.BURN_FROM_STEPPING)){
                cir.setReturnValue(true);
                cir.cancel();
            }

            if(source.is(DamageTypes.SWEET_BERRY_BUSH)){
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
