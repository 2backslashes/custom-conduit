package net.backslashes.customconduit.block.entity;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.*;
import java.util.function.Consumer;

import net.backslashes.customconduit.MathUtil;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
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

    public record ActiveRecipe(
            EffectConduitRecipe recipe,
            List<BlockPos> activeFrameBlocks
    ){}

    private int lastFrameHash = 0;
    public int tickCount;
    public int color = 0xFFFFFF;
    private float activeRotation;
    private boolean isActive;
    private long nextAmbientSoundActivation;

    private ResourceLocation pendingSelectedRecipe = null;
    private SelectedRecipe selectedRecipe = null;
    private final List<ActiveEffect> activeEffects = new ArrayList<>();
    private final List<ActiveRecipe> activeRecipes = new ArrayList<>();

    public record SelectedRecipe(
            int index,
            ResourceLocation id,
            EffectConduitRecipe recipe
    ){}

    public static final int FUEL_SLOT = 0;
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

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case DATA_SELECTED_RECIPE -> EffectConduitBlockEntity.this.selectedRecipe == null ? -1 : EffectConduitBlockEntity.this.selectedRecipe.index;
                case DATA_FRAME_PROGRESS -> EffectConduitBlockEntity.this.computeFrameProgressLevel();
                default -> 0;
            };
        }

        @Override
        public void set(int i, int value) {
            switch(i){
                case DATA_SELECTED_RECIPE:
                   EffectConduitBlockEntity.this.setSelectedRecipe(value);
                   break;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public int computeFrameProgressLevel(){
        if(activeRecipes.isEmpty()){
            return 0;
        }

        double maxFactor = 0.0;
        for(var recipe : activeRecipes){
            double factor = recipe.recipe.computePowerFactor(recipe.activeFrameBlocks.size());
            maxFactor = Math.max(maxFactor, factor);
        }
        return 1 + (int) (maxFactor * 3);
    }

    public void setSelectedRecipe(int recipeIndex){
        if(level == null){
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

    private static final String INVENTORY_TAG = "inventory";
    private static final String SELECTED_RECIPE_ID = "selected_recipe_id";
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(INVENTORY_TAG, inventory.serializeNBT(registries));
        if(selectedRecipe != null){
            tag.putString(SELECTED_RECIPE_ID, selectedRecipe.id().toString());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound(INVENTORY_TAG));
        if(tag.contains(SELECTED_RECIPE_ID)){
            String[] parts = tag.getString(SELECTED_RECIPE_ID).split(":");
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
                selectedRecipe = new SelectedRecipe(
                        i,
                        recipe.id(),
                        recipe.value()
                );
                break;
            }
        }
        pendingSelectedRecipe = null;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, EffectConduitBlockEntity blockEntity) {
        ++blockEntity.tickCount;
        blockEntity.loadPendingSelectedRecipe();
        long i = level.getGameTime();
        if (i % 40L == 0L) {
            computeActiveEffects(level, pos, blockEntity);
            blockEntity.isActive = !blockEntity.activeEffects.isEmpty();
        }

        animationTick(level, pos, blockEntity);
        if (blockEntity.isActive) {
            ++blockEntity.activeRotation;
        }

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

        if (i % ServerConfig.CONDUIT_TICKS_PER_REFRESH.get() == 0L) {
            // Compute active effects.
            computeActiveEffects(level, pos, blockEntity);
            boolean shouldBeActive = !blockEntity.activeEffects.isEmpty();

            // Toggle active state.
            if (shouldBeActive != blockEntity.isActive) {
                blockEntity.isActive = shouldBeActive;
                if(blockEntity.isActive){
                    onActivated(level, pos);
                } else {
                    onDeactivated(level, pos);
                }
            }

            if (blockEntity.isActive) {
                applyEffects(level, pos, blockEntity.activeEffects);
            }
        }

        if (blockEntity.isActive) {
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

    private static void computeActiveEffects(Level level, BlockPos center, EffectConduitBlockEntity blockEntity) {
        if(blockEntity.selectedRecipe == null){
            blockEntity.activeEffects.clear();
            blockEntity.activeRecipes.clear();
            blockEntity.color = 0xFFFFFF;
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
        int newFrameHash = blockEntity.selectedRecipe.hashCode();
        for(Map.Entry<Block, List<BlockPos>> entry : frameBlocksByType.entrySet()){
            // Compute a special hash that only cares about the number of blocks, not their positions.
            newFrameHash += entry.getKey().hashCode() * entry.getValue().size();
        }
        if(newFrameHash == blockEntity.lastFrameHash){
            return;
        }

        blockEntity.lastFrameHash = newFrameHash;

        blockEntity.activeEffects.clear();
        blockEntity.activeRecipes.clear();
        blockEntity.color = 0xFFFFFF;

        float colorTotalInfluence = 0.2f;
        float r = colorTotalInfluence;
        float g = colorTotalInfluence;
        float b = colorTotalInfluence;

        // Gather active effects.
        EffectConduitRecipe recipe = blockEntity.selectedRecipe.recipe();
        List<BlockPos> validFrameBlocks = recipe.computeValidFrameBlocks(frameBlocksByType);

        if(validFrameBlocks.size() < recipe.minFrameBlockCount()){
            blockEntity.setChanged();
            return;
        }

        double powerFactor = recipe.computePowerFactor(validFrameBlocks.size());

        // Accumulate color;
        float colorInfluence = (float) (powerFactor * 0.5 + 0.5);
        r += recipe.color().r() * colorInfluence;
        g += recipe.color().g() * colorInfluence;
        b += recipe.color().b() * colorInfluence;
        colorTotalInfluence += colorInfluence;

        blockEntity.activeRecipes.add(new ActiveRecipe(recipe, validFrameBlocks));

        // Accumulate effects.
        List<EffectConduitRecipe.ConduitEffect> outEffects = recipe.outEffects();
        for (EffectConduitRecipe.ConduitEffect effect : outEffects) {
            double range = effect.computeEffectRange(powerFactor);
            blockEntity.activeEffects.add(new ActiveEffect(
                    effect.effect(),
                    effect.amplifier(),
                    range
            ));
        }

        blockEntity.color = new MathUtil.RgbColor(r / colorTotalInfluence, g / colorTotalInfluence, b / colorTotalInfluence).toHexArgb();

        // Sort effects by range, high-to-low.
        blockEntity.activeEffects.sort(Comparator.comparingDouble((ActiveEffect a) -> a.rangeLimit).reversed());
        blockEntity.setChanged();
    }

    private static void applyEffects(Level level, BlockPos pos, List<ActiveEffect> activeEffects) {
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

    private static void animationTick(Level level, BlockPos pos, EffectConduitBlockEntity blockEntity) {
        RandomSource randomsource = level.random;
        double d0 = Mth.sin((float)(blockEntity.tickCount + 35) * 0.1F) / 2.0F + 0.5F;
        d0 = (d0 * d0 + d0) * (double)0.3F;
        Vec3 vec3 = new Vec3((double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)1.5F + d0, (double)pos.getZ() + (double)0.5F);

        for(ActiveRecipe recipe : blockEntity.activeRecipes){
            for(BlockPos blockpos : recipe.activeFrameBlocks) {
                if (randomsource.nextInt(100) == 0) {
                    level.addParticle(
                            new EffectConduitParticles.EffectConduitParticleOptions(
                                pos.getCenter(),
                                recipe.recipe.color()
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

    public boolean isActive() {
        return this.isActive;
    }

    public float getActiveRotation(float partialTick) {
        return (this.activeRotation + partialTick) * -0.0375F;
    }
}
