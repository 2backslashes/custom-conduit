package net.backslashes.customconduit.block;

import net.backslashes.customconduit.CustomConduit;
import net.backslashes.customconduit.block.entity.EffectConduitBlockEntity;
import net.backslashes.customconduit.block.entity.EffectConduitRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = CustomConduit.MODID)
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CustomConduit.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CustomConduit.MODID);

    public static final DeferredBlock<EffectConduitBlock> EFFECT_CONDUIT = BLOCKS.register(
            "effect_conduit",
            registerName -> new EffectConduitBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DIAMOND)
                            .forceSolidOn()
                            .instrument(NoteBlockInstrument.HAT)
                            .strength(3.0F)
                            .lightLevel((p_152677_) -> 15)
                            .noOcclusion()
            ));

    public static final Supplier<BlockEntityType<EffectConduitBlockEntity>> EFFECT_CONDUIT_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "effect_conduit_block_entity",
            () -> BlockEntityType.Builder.of(
                            EffectConduitBlockEntity::new,
                            EFFECT_CONDUIT.get()
                    )
                    .build(null)
    );


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }

    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                // The block entity type to register the renderer for.
                EFFECT_CONDUIT_BLOCK_ENTITY.get(),
                // A function of BlockEntityRendererProvider.Context to BlockEntityRenderer.
                EffectConduitRenderer::new
        );
    }
}
