package net.backslashes.customconduit.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;

import net.backslashes.customconduit.block.entity.EffectConduitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class EffectConduitBlock extends BaseEntityBlock {
    public static final MapCodec<EffectConduitBlock> CODEC = simpleCodec(EffectConduitBlock::new);
    protected static final VoxelShape SHAPE = Block.box(2.0F, 2.0F, 2.0F, 14.0F, 14.0, 14.0F);

    @Override
    public @NotNull MapCodec<EffectConduitBlock> codec() {
        return CODEC;
    }

    public EffectConduitBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EffectConduitBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlocks.EFFECT_CONDUIT_BLOCK_ENTITY.get(), level.isClientSide ? EffectConduitBlockEntity::clientTick : EffectConduitBlockEntity::serverTick);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()){
            if(level.getBlockEntity(pos) instanceof EffectConduitBlockEntity entity){
                entity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            @NotNull ItemStack stack,
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hitResult
    ) {
        if(level.getBlockEntity(pos) instanceof EffectConduitBlockEntity entity){
            if(player instanceof ServerPlayer serverPlayer){
                serverPlayer.openMenu(new SimpleMenuProvider(entity, Component.literal("Conduit")), pos);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    protected boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    protected int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof EffectConduitBlockEntity entity){
            if(!entity.requiresFuel()){
                return 15;
            }

            ItemStack stack = entity.getStackInSlot(0);
            if(stack.isEmpty()){
                return 0;
            }

            return (int) Math.ceil(15 * (float) stack.getCount() / stack.getMaxStackSize());
        }
        return 0;
    }
}
