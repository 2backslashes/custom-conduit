package net.backslashes.extraeffects.event;

import net.backslashes.extraeffects.ExtraEffects;
import net.backslashes.extraeffects.effect.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ExtraEffects.MODID)
public class FlightEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event){
        if(event.getEffectInstance().getEffect().is(ModEffects.FLIGHT.getId())){
            Entity entity = event.getEntity();
            if(entity instanceof ServerPlayer player && !player.isCreative() && !player.isSpectator()) {
                // player.getAbilities().flying = true;
                player.getAbilities().mayfly = true; // it may be deprecated, but it's the only thing that works :)
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
            if(entity instanceof ServerPlayer player && !player.isCreative() && !player.isSpectator()) {
                // player.getAbilities().flying = true;
                player.getAbilities().mayfly = false; // it may be deprecated, but it's the only thing that works :)
                player.onUpdateAbilities();
            }
        }
    }
}