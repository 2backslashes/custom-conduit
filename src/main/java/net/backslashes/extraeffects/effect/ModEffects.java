package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ExtraEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final net.neoforged.neoforge.registries.DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(Registries.MOB_EFFECT, ExtraEffects.MODID);
    public static final net.neoforged.neoforge.registries.DeferredRegister<Potion> POTIONS
            = DeferredRegister.create(Registries.POTION, ExtraEffects.MODID);

    public static final DeferredHolder<MobEffect, Flight> FLIGHT = MOB_EFFECTS.register("flight", Flight::new);
    public static final Holder<Potion> FLIGHT_POTION = POTIONS.register("flight_potion", registryName -> new Potion(
            registryName.getPath(),
            new MobEffectInstance(FLIGHT, 600)
    ));

    public static final DeferredHolder<MobEffect, BlockReach> BLOCK_REACH = MOB_EFFECTS.register("block_reach", BlockReach::new);
    public static final Holder<Potion> BLOCK_REACH_POTION = POTIONS.register("block_reach_potion", registryName -> new Potion(
            registryName.getPath(),
            new MobEffectInstance(BLOCK_REACH, 5400)
    ));

    public static final DeferredHolder<MobEffect, EntityReach> ENTITY_REACH = MOB_EFFECTS.register("entity_reach", EntityReach::new);
    public static final Holder<Potion> ENTITY_REACH_POTION = POTIONS.register("entity_reach_potion", registryName -> new Potion(
            registryName.getPath(),
            new MobEffectInstance(ENTITY_REACH, 2400)
    ));

    public static final DeferredHolder<MobEffect, Unimpeded> UNIMPEDED = MOB_EFFECTS.register("unimpeded", Unimpeded::new);
    public static final Holder<Potion> UNIMPEDED_POTION = POTIONS.register("unimpeded_potion", registryName -> new Potion(
            registryName.getPath(),
            new MobEffectInstance(UNIMPEDED, 3600)
    ));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
    }

}