package net.backslashes.customconduit.sound;

import net.backslashes.customconduit.CustomConduit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CustomConduit.MODID);

    public static final Supplier<SoundEvent> CONDUIT_ACTIVATE = registerSoundEvent("conduit_activate");
    public static final Supplier<SoundEvent> CONDUIT_DEACTIVATE = registerSoundEvent("conduit_deactivate");
    public static final Supplier<SoundEvent> CONDUIT_AMBIENT = registerSoundEvent("conduit_ambient");
//    public static final Supplier<SoundEvent> CONDUIT_AMBIENT_SHORT = registerSoundEvent("conduit_ambient_short");

    private static Supplier<SoundEvent> registerSoundEvent(String name){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus bus){
        SOUND_EVENTS.register(bus);
    }
}
