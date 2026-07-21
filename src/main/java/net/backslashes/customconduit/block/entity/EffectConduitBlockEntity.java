package net.backslashes.customconduit.block.entity;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.*;
import java.util.function.Consumer;

import net.backslashes.customconduit.ServerConfig;
import net.backslashes.customconduit.block.ModBlocks;
import net.backslashes.customconduit.particle.EffectConduitParticles;
import net.backslashes.customconduit.recipe.EffectConduitRecipe;
import net.backslashes.customconduit.recipe.ModRecipes;
import net.backslashes.customconduit.screen.custom.ConduitMenu;
import net.backslashes.customconduit.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffectConduitBlockEntity extends BlockEntity implements MenuProvider {
    @Override
    public Component getDisplayName() {
        return Component.literal("Conduit");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new ConduitMenu(i, inventory, this);
    }

    public record ActiveEffect(
            Holder<MobEffect> effect,
            int amplifier,
            double rangeLimit
    ){}

    private int lastFrameHash = 0;
    public int tickCount;
    public int color = 0xFFFFFF;
    private long nextAmbientSoundActivation;

    private int activeLevel = 0;
    private int fuelRemainingTicks;
    private ResourceLocation pendingSelectedRecipe = null;
    private SelectedRecipe selectedRecipe = null;
    private final List<BlockPos> validFrameBlocks = new ArrayList<>();
    private final List<ActiveEffect> activeEffects = new ArrayList<>();

    public record SelectedRecipe(
            int index,
            ResourceLocation id,
            EffectConduitRecipe recipe
    ){}

    public static final int INV_SLOT_FUEL = 0;
    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
            if(level == null || level.isClientSide){
                return;
            }

            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    public EffectConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.EFFECT_CONDUIT_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static final int DATA_SELECTED_RECIPE = 0;
    public static final int DATA_FRAME_PROGRESS = 1;
    public static final int DATA_FUEL_TIMER_MAX = 2;
    public static final int DATA_FUEL_REMAINING_TICKS = 3;

    public int getActiveLevel(){
        return activeLevel;
    }

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int i) {
            var self = EffectConduitBlockEntity.this;
            switch (i) {
                case DATA_SELECTED_RECIPE:
                    return self.selectedRecipe != null ? self.selectedRecipe.index : -1;
                case DATA_FRAME_PROGRESS:
                    return self.activeLevel;
                case DATA_FUEL_TIMER_MAX:
                    if (!self.requiresFuel()) {
                        return -1;
                    }
                    return self.selectedRecipe.recipe.fuelBurnTime();
                case DATA_FUEL_REMAINING_TICKS:
                    return self.fuelRemainingTicks;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int i, int value) {
            var self = EffectConduitBlockEntity.this;
            switch(i){
                case DATA_SELECTED_RECIPE:
                   self.setSelectedRecipe(value);
                   break;
                case DATA_FUEL_REMAINING_TICKS:
                   self.fuelRemainingTicks = value;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public void setSelectedRecipe(int recipeIndex){
        if(level == null){
            return;
        }

        if(this.selectedRecipe != null && recipeIndex == this.selectedRecipe.index){
            return;
        }

        var allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.EFFECT_CONDUIT_RECIPE_TYPE.get());
        if(recipeIndex >= allRecipes.size() || recipeIndex < 0){
            System.err.println("Player selected invalid conduit recipe with index " + recipeIndex + "!");
            return;
        }
        var recipe = allRecipes.get(recipeIndex);
        this.selectedRecipe = new SelectedRecipe(
                recipeIndex,
                recipe.id(),
                recipe.value()
        );

        this.fuelRemainingTicks = 0;
        this.color = this.selectedRecipe.recipe.color().toHexArgb();
        setChanged();
    }

    public SelectedRecipe getSelectedRecipe(){
        return this.selectedRecipe;
    }

    public void drops() {
        if(this.level == null){
            return;
        }

        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i=0; i<inventory.getSlots(); ++i){
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    private static final String TAG_INVENTORY = "inventory";
    private static final String TAG_SELECTED_RECIPE_ID = "selected_recipe_id";
    private static final String TAG_FUEL_REMAINING = "fuel_remaining";
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(TAG_INVENTORY, inventory.serializeNBT(registries));
        tag.putInt(TAG_FUEL_REMAINING, this.fuelRemainingTicks);
        if(selectedRecipe != null){
            tag.putString(TAG_SELECTED_RECIPE_ID, selectedRecipe.id().toString());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound(TAG_INVENTORY));
        this.fuelRemainingTicks = tag.getInt(TAG_FUEL_REMAINING);
        if(tag.contains(TAG_SELECTED_RECIPE_ID)){
            String[] parts = tag.getString(TAG_SELECTED_RECIPE_ID).split(":");
            if(parts.length == 2){
                this.pendingSelectedRecipe = ResourceLocation.fromNamespaceAndPath(
                    parts[0],
                    parts[1]
                );
                this.selectedRecipe = null;
            }
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveCustomOnly(registries);
    }

    private void loadPendingSelectedRecipe(){
        if(pendingSelectedRecipe == null){
            return;
        }

        if(level == null){
            return;
        }

        var allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.EFFECT_CONDUIT_RECIPE_TYPE.get());
        for(int i=0; i<allRecipes.size(); ++i){
            var recipe = allRecipes.get(i);
            if(recipe.id().equals(pendingSelectedRecipe)) {
                setSelectedRecipe(i);
                break;
            }
        }
        pendingSelectedRecipe = null;
    }

    public boolean isActive(){
        return activeLevel > 0;
    }

    private void recomputeActiveLevel(BlockPos pos){
        if(level == null){
            return;
        }

        int newActiveLevel = computeActiveLevel();
        if(newActiveLevel == activeLevel){
            return;
        }

        // Toggle active state.
        if(newActiveLevel > 0 && activeLevel == 0) {
            onActivated(level, pos);
        }else if(newActiveLevel == 0 && activeLevel != 0){
            onDeactivated(level, pos);
        }

        // Update active effects.
        activeEffects.clear();

        activeLevel = newActiveLevel;
        assert selectedRecipe != null;
        for(var effect : selectedRecipe.recipe.outEffects()){
            activeEffects.add(new ActiveEffect(
                effect.effect(),
                effect.amplifier(),
                effect.computeEffectRange(activeLevel / 3.0f)
            ));
        }

        // Sort effects by range, high-to-low.
        activeEffects.sort(Comparator.comparingDouble((ActiveEffect a) -> a.rangeLimit).reversed());
    }


    // Ranges from 0 to 4, where 0 is inactive.
    private int computeActiveLevel(){
        if(selectedRecipe == null){
            return 0;
        }

        if(requiresFuel() && fuelRemainingTicks == 0){
            return 0;
        }

        if(validFrameBlocks.size() < selectedRecipe.recipe.minFrameBlockCount()){
            return 0;
        }

        double frameFactor = selectedRecipe.recipe.computePowerFactor(validFrameBlocks.size());
        return 1 + (int) (frameFactor * 3);
    }

    public boolean requiresFuel(){
        return selectedRecipe != null && !selectedRecipe.recipe.fuelIngredient().isEmpty();
    }

    private void fuelTick(){
        if(!requiresFuel()){
            fuelRemainingTicks = 0;
            return;
        }

        // Don't waste fuel if there aren't enough frame blocks.
        if(validFrameBlocks.size() >= this.selectedRecipe.recipe.minFrameBlockCount()) {
            if (fuelRemainingTicks > 0) {
                fuelRemainingTicks--;
            }
        }

        if(fuelRemainingTicks == 0){
            ItemStack fuelItem = inventory.getStackInSlot(INV_SLOT_FUEL);
            if(!fuelItem.isEmpty() && selectedRecipe.recipe.fuelIngredient().test(fuelItem)){
                fuelItem.setCount(fuelItem.getCount() - 1);
                fuelRemainingTicks = selectedRecipe.recipe.fuelBurnTime();
            }
        }
    }

    private void updateTick(BlockPos pos){
        if(this.level == null){
            return;
        }

        // Update fuel.
        this.fuelTick();

        // Check frame blocks.
        if (this.tickCount % ServerConfig.CONDUIT_TICKS_PER_REFRESH.get() == 0L) {
            this.recomputeValidFrameBlocks(pos);
        }

        // Update active state.
        this.recomputeActiveLevel(pos);

        // Apply effects.
        if (isActive()) {
            if(this.tickCount % ServerConfig.CONDUIT_TICKS_PER_REFRESH.get() == 0L){
                this.applyEffects(pos);
            }
        }

        setChanged();
    }

    private void recomputeValidFrameBlocks(BlockPos center){
        if(selectedRecipe == null){
            validFrameBlocks.clear();
            return;
        }

        if(level == null){
            return;
        }

        HashMap<Block, List<BlockPos>> frameBlocksByType = new HashMap<>();
        iterFrameCandidates(center, (pos) -> {
            Block block = level.getBlockState(pos).getBlock();
            List<BlockPos> blocksOfType = frameBlocksByType.get(block);
            if(blocksOfType == null){
                frameBlocksByType.put(block, new ArrayList<>(List.of(pos)));
            } else {
                blocksOfType.add(pos);
            }
        });

        // Early exit if the frame hasn't actually changed composition.
        int newFrameHash = selectedRecipe.hashCode();
        for(Map.Entry<Block, List<BlockPos>> entry : frameBlocksByType.entrySet()){
            // Compute a special hash that only cares about the number of blocks, not their positions.
            newFrameHash += entry.getKey().hashCode() * entry.getValue().size();
        }
        if(newFrameHash == lastFrameHash){
            return;
        }
        lastFrameHash = newFrameHash;

         selectedRecipe.recipe.computeValidFrameBlocks(frameBlocksByType, validFrameBlocks);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, EffectConduitBlockEntity blockEntity) {
        ++blockEntity.tickCount;
        blockEntity.loadPendingSelectedRecipe();
        blockEntity.updateTick(pos);

        blockEntity.animationTick(pos);
    }

    private static void onActivated(Level level, BlockPos pos){
        level.playSound(null, pos, ModSounds.CONDUIT_ACTIVATE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void onDeactivated(Level level, BlockPos pos){
        level.playSound(null, pos, ModSounds.CONDUIT_DEACTIVATE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EffectConduitBlockEntity blockEntity) {
        ++blockEntity.tickCount;
        long i = level.getGameTime();

        blockEntity.loadPendingSelectedRecipe();
        blockEntity.updateTick(pos);

        if (blockEntity.isActive()) {
            if (i % 100L == 0L) {
                level.playSound(null, pos, ModSounds.CONDUIT_AMBIENT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    private static void iterFrameCandidates(BlockPos pos, Consumer<BlockPos> consumer){
        for(int j1 = -2; j1 <= 2; ++j1) {
            for(int k1 = -2; k1 <= 2; ++k1) {
                for (int l1 = -2; l1 <= 2; ++l1) {
                    int i2 = Math.abs(j1);
                    int l = Math.abs(k1);
                    int i1 = Math.abs(l1);
                    if ((j1 == 0 && (l == 2 || i1 == 2) || k1 == 0 && (i2 == 2 || i1 == 2) || l1 == 0 && (i2 == 2 || l == 2))) {
                        consumer.accept(pos.offset(j1, k1, l1));
                    }
                }
            }
        }
    }

    private void applyEffects(BlockPos pos) {
        if(activeEffects.isEmpty()){
            return;
        }
        int maxSearchRange = (int) Math.ceil(activeEffects.getFirst().rangeLimit);
        int j = maxSearchRange / 7 * 16;
        int k = pos.getX();
        int l = pos.getY();
        int i1 = pos.getZ();
        AABB aabb = (new AABB(k, l, i1, (k + 1), (l + 1), (i1 + 1))).inflate(j).expandTowards(0.0F, level.getHeight(), 0.0F);
        List<Player> players = level.getEntitiesOfClass(Player.class, aabb);
        for(Player player : players){
            double dist = player.position().distanceTo(pos.getCenter());
            for(ActiveEffect effect : activeEffects){
                if(dist > effect.rangeLimit){
                    // Effects are sorted by range, so we can break if the player is too far for this effect.
                    break;
                }

                player.addEffect(new MobEffectInstance(effect.effect(), ServerConfig.CONDUIT_EFFECT_DURATION_TICKS.get(), effect.amplifier, true, true));
            }
        }
    }

    private void animationTick(BlockPos pos) {
        if(level == null){
            return;
        }

        RandomSource randomsource = level.random;
        double d0 = Mth.sin((float)(tickCount + 35) * 0.1F) / 2.0F + 0.5F;
        d0 = (d0 * d0 + d0) * (double)0.3F;
        Vec3 vec3 = new Vec3((double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)1.5F + d0, (double)pos.getZ() + (double)0.5F);

        if(!isActive()){
            return;
        }
        for(BlockPos blockpos : validFrameBlocks) {
            if (randomsource.nextInt(100) == 0) {
                level.addParticle(
                        new EffectConduitParticles.EffectConduitParticleOptions(
                            pos.getCenter(),
                            selectedRecipe.recipe().color()
                        ),
                        (double) blockpos.getX() + level.random.nextFloat(),
                        (double) blockpos.getY() + level.random.nextFloat(),
                        (double) blockpos.getZ() + level.random.nextFloat(),
                        0.0,
                        0.0,
                        0.0
                );
            }
        }
    }
}
