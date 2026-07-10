package net.backslashes.flighteffect.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class Flight extends MobEffect {
    protected Flight() {
        super(MobEffectCategory.BENEFICIAL, 0xEEEEAA);
    }
}
