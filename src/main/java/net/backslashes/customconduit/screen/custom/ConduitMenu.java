package net.backslashes.customconduit.screen.custom;

import net.backslashes.customconduit.block.ModBlocks;
import net.backslashes.customconduit.block.entity.EffectConduitBlockEntity;
import net.backslashes.customconduit.screen.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import static net.backslashes.customconduit.block.entity.EffectConduitBlockEntity.DATA_SELECTED_RECIPE;

public class ConduitMenu extends AbstractContainerMenu {
    public final BlockPos pos;
    public final ContainerData conduitData;
    private final Level level;
    private final int fuelSlot;

    public ConduitMenu(int containerId, Inventory inv, BlockPos pos, ItemStackHandler containerInv, ContainerData conduitData){
        super(ModMenuTypes.CONDUIT_MENU.get(), containerId);
        this.conduitData = conduitData;
        this.pos = pos;

        this.level = inv.player.level();
        this.fuelSlot = this.slots.size();

        addPlayerInventory(inv);

        this.addSlot(new SlotItemHandler(containerInv, 0, 121, 35));
        this.addDataSlots(conduitData);
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        this.conduitData.set(DATA_SELECTED_RECIPE, id);
        this.broadcastChanges();
        level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0f);
        return super.clickMenuButton(player, id);
    }

    private void addPlayerInventory(Inventory playerInventory){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    public ConduitMenu(int containerId, Inventory inv, FriendlyByteBuf buf) {
        this(containerId, inv, (EffectConduitBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public ConduitMenu(int containerId, Inventory inv, EffectConduitBlockEntity blockEntity) {
        this(containerId, inv, blockEntity.getBlockPos(), blockEntity.inventory, blockEntity.dataAccess);
    }


    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        // TODO
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, this.pos), player, ModBlocks.EFFECT_CONDUIT.get());
    }
}
