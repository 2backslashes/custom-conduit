package net.backslashes.customconduit.item;

import net.backslashes.customconduit.CustomConduit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CustomConduit.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
