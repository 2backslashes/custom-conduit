package net.backslashes.extraeffects.event;

import net.backslashes.extraeffects.ServerConfig;
import net.backslashes.extraeffects.ExtraEffects;
import net.backslashes.extraeffects.effect.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


@EventBusSubscriber(modid = ExtraEffects.MODID)
public class EffectEventHandler {
    private final static float CREATIVE_FLY_SPEED = 0.05f;
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event){
        MobEffectInstance effect = event.getEffectInstance();
        if(effect.getEffect().is(ModEffects.FLIGHT.getId())){
            Entity entity = event.getEntity();
            if(entity instanceof ServerPlayer player) {
                Abilities abilities = player.getAbilities();
                abilities.mayfly = true; // it may be deprecated, but it's the only thing that works :)
                float naturalFlightSpeed = player.isCreative() ? CREATIVE_FLY_SPEED : 0.0f;
                float potionFlightSpeed = ServerConfig.FLIGHT_SPEED_PER_LEVEL.get().floatValue() * (1 + effect.getAmplifier());
                abilities.setFlyingSpeed(Float.max(naturalFlightSpeed, potionFlightSpeed));
                player.onUpdateAbilities();
            }
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event){
        if(event.getEffectInstance() == null){
            return;
        }
        effectEnded(event.getEffectInstance(), event.getEntity());
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event){
        if(event.getEffectInstance() == null){
            return;
        }
        effectEnded(event.getEffectInstance(), event.getEntity());
    }

    public static void effectEnded(MobEffectInstance instance, Entity entity){
        if(instance.getEffect().is(ModEffects.FLIGHT.getId())){
            if(entity instanceof ServerPlayer player) {
                player.getAbilities().setFlyingSpeed(CREATIVE_FLY_SPEED);
                if (!player.isCreative()) {
                    player.getAbilities().mayfly = false;
                }
                player.onUpdateAbilities();
            }
        }
    }
}