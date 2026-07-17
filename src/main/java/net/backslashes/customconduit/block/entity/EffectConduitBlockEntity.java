package net.backslashes.customconduit.block.entity;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Consumer;

import net.backslashes.customconduit.MathUtil;
import net.backslashes.customconduit.ServerConfig;
import net.backslashes.customconduit.block.ModBlocks;
import net.backslashes.customconduit.recipe.EffectConduitRecipe;
import net.backslashes.customconduit.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EffectConduitBlockEntity extends BlockEntity {
    public record ActiveEffect(
            Holder<MobEffect> effect,
            int amplifier,
            double rangeLimit,
            List<BlockState> validFrameBlocks
    ){}

    private int lastFrameHash = 0;
    public int tickCount;
    public int color = 0xFFFFFFFF;
    private float activeRotation;
    private boolean isActive;
    private final List<BlockPos> effectBlocks = Lists.newArrayList();
    private long nextAmbientSoundActivation;
    List<ActiveEffect> activeEffects = new ArrayList<>();

    public EffectConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.EFFECT_CONDUIT_BLOCK_ENTITY.get(), pos, blockState);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveCustomOnly(registries);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, EffectConduitBlockEntity blockEntity) {
        ++blockEntity.tickCount;
        long i = level.getGameTime();
        if (i % 40L == 0L) {
            computeActiveEffects(level, pos, blockEntity);
            blockEntity.isActive = !blockEntity.activeEffects.isEmpty();
        }

        // TODO block list
        animationTick(level, pos, Collections.emptyList(), blockEntity.tickCount);
        if (blockEntity.isActive) {
            ++blockEntity.activeRotation;
        }

    }

    private static void onActivated(Level level, BlockPos pos){
        level.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void onDeactivated(Level level, BlockPos pos){
        level.playSound(null, pos, SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EffectConduitBlockEntity blockEntity) {
        ++blockEntity.tickCount;
        long i = level.getGameTime();

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
            if (i % 80L == 0L) {
                level.playSound(null, pos, SoundEvents.CONDUIT_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            if (i > blockEntity.nextAmbientSoundActivation) {
                blockEntity.nextAmbientSoundActivation = i + 60L + (long)level.getRandom().nextInt(40);
                level.playSound(null, pos, SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 1.0F, 1.0F);
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
        List<BlockState> frameBlocks = new ArrayList<>();
        iterFrameCandidates(center, (pos) -> {
             frameBlocks.add(level.getBlockState(pos));
        });

        HashMap<Block, List<BlockState>> frameBlocksByType = new HashMap<>();
        for (BlockState blockState : frameBlocks) {
            Block block = blockState.getBlock();
            List<BlockState> blocksOfType = frameBlocksByType.get(block);
            if(blocksOfType == null){
                frameBlocksByType.put(block, new ArrayList<>(List.of(blockState)));
            } else {
                blocksOfType.add(blockState);
            }
        }

        // Early exit if the frame hasn't actually changed composition.
        int newFrameHash = frameBlocksByType.hashCode();
        if(newFrameHash == blockEntity.lastFrameHash){
            return;
        }
        blockEntity.lastFrameHash = newFrameHash;

        float r = 0.3f;
        float g = 0.3f;
        float b = 0.3f;
        float colorTotalInfluence = 0.3f;

        // Gather active effects.
        blockEntity.activeEffects.clear();
        List<RecipeHolder<EffectConduitRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.EFFECT_CONDUIT_RECIPE_TYPE.get());
        for (RecipeHolder<EffectConduitRecipe> recipeHolder : allRecipes) {
            EffectConduitRecipe recipe = recipeHolder.value();
            List<BlockState> validFrameBlocks = recipe.computeValidFrameBlocks(frameBlocksByType);

            if(validFrameBlocks.size() < recipe.minFrameBlockCount()){
                continue;
            }

            double powerFactor = recipe.computePowerFactor(validFrameBlocks.size());

            // Accumulate color.
            r += recipe.colorR();
            g += recipe.colorG();
            b += recipe.colorB();
            colorTotalInfluence += (float) (powerFactor * 0.5 + 0.5);

            // Accumulate effects.
            List<EffectConduitRecipe.ConduitEffect> outEffects = recipe.outEffects();
            for (EffectConduitRecipe.ConduitEffect effect : outEffects) {
                double range = effect.computeEffectRange(powerFactor);
                blockEntity.activeEffects.add(new ActiveEffect(
                        effect.effect(),
                        effect.amplifier(),
                        range,
                        validFrameBlocks
                ));
            }
        }

        blockEntity.color = new MathUtil.RgbColor(r / colorTotalInfluence, g / colorTotalInfluence, b / colorTotalInfluence).toHexArgb();

        // Sort effects by range, high-to-low.
        blockEntity.activeEffects.sort(Comparator.comparingDouble((ActiveEffect a) -> a.rangeLimit).reversed());
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

    private static void animationTick(Level level, BlockPos pos, List<BlockPos> positions, int tickCount) {
        RandomSource randomsource = level.random;
        double d0 = Mth.sin((float)(tickCount + 35) * 0.1F) / 2.0F + 0.5F;
        d0 = (d0 * d0 + d0) * (double)0.3F;
        Vec3 vec3 = new Vec3((double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)1.5F + d0, (double)pos.getZ() + (double)0.5F);

        for(BlockPos blockpos : positions) {
            if (randomsource.nextInt(50) == 0) {
                BlockPos blockpos1 = blockpos.subtract(pos);
                float f = -0.5F + randomsource.nextFloat() + (float)blockpos1.getX();
                float f1 = -2.0F + randomsource.nextFloat() + (float)blockpos1.getY();
                float f2 = -0.5F + randomsource.nextFloat() + (float)blockpos1.getZ();
                level.addParticle(ParticleTypes.NAUTILUS, vec3.x, vec3.y, vec3.z, f, f1, f2);
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
