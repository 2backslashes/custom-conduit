package net.backslashes.extraeffects.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SunSensitivity extends MobEffect {
    public static final String EFFECT_ID = "sun_sensitivity";
    protected SunSensitivity() {
        super(MobEffectCategory.HARMFUL, 0xFFF945);
    }

    private static boolean isSunburnTick(LivingEntity livingEntity){
        if (livingEntity.level().isDay() && !livingEntity.level().isClientSide) {
            float f = livingEntity.getLightLevelDependentMagicValue();
            BlockPos blockpos = BlockPos.containing(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ());
            boolean flag = livingEntity.isInWaterRainOrBubble() || livingEntity.isInPowderSnow || livingEntity.wasInPowderSnow;
            if (f > 0.5F && livingEntity.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && livingEntity.level().canSeeSky(blockpos)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (isSunburnTick(livingEntity)) {
            livingEntity.igniteForSeconds(8.0F);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
