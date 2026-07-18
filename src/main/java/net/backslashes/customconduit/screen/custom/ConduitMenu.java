package net.backslashes.customconduit.screen.custom;

import net.backslashes.customconduit.block.ModBlocks;
import net.backslashes.customconduit.block.entity.EffectConduitBlockEntity;
import net.backslashes.customconduit.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ConduitMenu extends AbstractContainerMenu {
    public final EffectConduitBlockEntity blockEntity;
    private final Level level;
    private final int fuelSlot;

    public ConduitMenu(int containerId, Inventory inv, EffectConduitBlockEntity blockEntity){
        super(ModMenuTypes.CONDUIT_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);

        this.fuelSlot = this.slots.size();
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 122, 35));
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


    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        // TODO
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.EFFECT_CONDUIT.get());
    }
}
