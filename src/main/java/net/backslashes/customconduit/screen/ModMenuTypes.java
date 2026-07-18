package net.backslashes.customconduit.screen;

import net.backslashes.customconduit.CustomConduit;
import net.backslashes.customconduit.screen.custom.ConduitMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, CustomConduit.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ConduitMenu>> CONDUIT_MENU = registerMenuType("conduit_menu", ConduitMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(
            String name,
            IContainerFactory<T> factory
    ){
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus bus){
        MENUS.register(bus);
    }
}
