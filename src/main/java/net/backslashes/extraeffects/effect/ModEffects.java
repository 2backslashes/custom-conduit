package net.backslashes.extraeffects.effect;

import net.backslashes.extraeffects.ExtraEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@EventBusSubscriber(modid = ExtraEffects.MODID)
public class ModEffects {
    static List<EffectItems> effectItemsList = new ArrayList<>();

    public static <T extends MobEffect> EffectItems registerEffectItems(EffectItems items) {
        effectItemsList.add(items);
        return items;
    }

    public static class EffectItems {
        public final DeferredHolder<MobEffect, MobEffect> effect;
        public final String effectId;
        public final String displayName;
        public final Holder<Potion> potion;
        public final Holder<Potion> strongPotion;
        public final Holder<Potion> longPotion;
        public final String description;

        public EffectItems(String effectId, String displayName, Supplier<MobEffect> supplier, int duration, int longDuration, int strongDuration, String description) {
            this.displayName = displayName;
            this.effectId = effectId;
            this.description = description;
            effect = MOB_EFFECTS.register(effectId, supplier);

            potion = POTIONS.register(effectId + "_potion", registryName -> new Potion(
                    registryName.getPath(),
                    new MobEffectInstance(effect, duration)
            ));

            if(longDuration == 0){
                longPotion = null;
            } else {
                longPotion = POTIONS.register("long_" + effectId + "_potion", registryName -> new Potion(
                        registryName.getPath(),
                        new MobEffectInstance(effect, longDuration)
                ));
            }

            if(strongDuration == 0){
                strongPotion = null;
            } else {
                strongPotion = POTIONS.register("strong_" + effectId + "_potion", registryName -> new Potion(
                        registryName.getPath(),
                        new MobEffectInstance(effect, strongDuration, 1)
                ));
            }
        }
    }
    public static final net.neoforged.neoforge.registries.DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(Registries.MOB_EFFECT, ExtraEffects.MODID);
    public static final net.neoforged.neoforge.registries.DeferredRegister<Potion> POTIONS
            = DeferredRegister.create(Registries.POTION, ExtraEffects.MODID);

    public static final EffectItems FLIGHT = registerEffectItems(new EffectItems(
            Flight.EFFECT_ID,
            "Flight",
            Flight::new,
            400,
            800,
            200,
            "Grants creative flight. Higher levels provide faster flying speed."
    ));
    public static final EffectItems BLOCK_REACH = registerEffectItems(new EffectItems(
            BlockReach.EFFECT_ID,
            "Miner's Reach",
            BlockReach::new,
            4000,
            6000,
            3000,
            "Allows mining & placing blocks farther away. Higher levels increase interaction distance."
    ));
    public static final EffectItems ENTITY_REACH = registerEffectItems(new EffectItems(
            EntityReach.EFFECT_ID,
            "Warrior's Reach",
            EntityReach::new,
            2400,
            3600,
            1800,
            "Allows hitting or interacting with entities from farther away. Higher levels increase interaction distance."
    ));
    public static final EffectItems UNIMPEDED = registerEffectItems(new EffectItems(
            Unimpeded.EFFECT_ID,
            "Unimpeded Travel",
            Unimpeded::new,
            3600,
            5000,
            0,
            "Removes the movement/jump/damage penalties from blocks like Sweet Berry Bushes, Magma, Cobwebs, etc."
    ));
    public static final EffectItems RABBIT_FORM = registerEffectItems(new EffectItems(
            RabbitForm.EFFECT_ID,
            "Rabbit Form",
            RabbitForm::new,
            2400,
            3600,
            1200,
            "Greatly increases agility and removes fall damage, but decreases health, attack damage, and armor in turn."
    ));
    public static final EffectItems EXPLOSIVE = registerEffectItems(new EffectItems(
            Explosive.EFFECT_ID,
            "Volatility",
            Explosive::new,
            600,
            1200,
            400,
            "On death, violently explode. The larger the mob, the bigger the boom."
    ));
    public static final EffectItems EXPIRATION = registerEffectItems(new EffectItems(
            Expiration.EFFECT_ID,
            "Expiration",
            Expiration::new,
            24000,
            0,
            12000,
            "When the effect expires, so do you. Consider drinking milk if you can."
    ));

    public static final EffectItems PEACEFUL_MINER = registerEffectItems(new EffectItems(
            PeacefulMiner.EFFECT_ID,
            "the Peaceful Miner",
            PeacefulMiner::new,
            4000,
            6000,
            2000,
            "Increases mining speed, but decreases attack damage."
    ));

    public static final EffectItems SunSensitivity = registerEffectItems(new EffectItems(
            net.backslashes.extraeffects.effect.SunSensitivity.EFFECT_ID,
            "Sun Sensitivity",
            SunSensitivity::new,
            6000,
            12000,
            0,
            "Catch fire in direct sunlight. Headwear won't protect you!"
    ));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
    }

    @SubscribeEvent // on the game event bus
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        for (EffectItems effectItems : effectItemsList) {
            if(effectItems.longPotion != null){
                builder.addMix(
                        effectItems.potion,
                        Items.REDSTONE,
                        effectItems.longPotion
                );
            }

            if(effectItems.strongPotion != null){
                builder.addMix(
                        effectItems.potion,
                        Items.GLOWSTONE_DUST,
                        effectItems.strongPotion
                );
            }
        }
    }

    public static class EffectLangProvider extends LanguageProvider {
        public EffectLangProvider(PackOutput output) {
            super(
                    output,
                    ExtraEffects.MODID,
                    "en_us"
            );
        }

        @Override
        protected void addTranslations() {
            for (EffectItems effectItems : effectItemsList) {
                addEffect(effectItems.effect, effectItems.displayName);
                add("item.minecraft.potion.effect." + effectItems.effectId + "_potion", "Potion of " +effectItems.displayName);
                add("item.minecraft.splash_potion.effect." + effectItems.effectId + "_potion", "Splash Potion of " +effectItems.displayName);
                add("item.minecraft.lingering_potion.effect." + effectItems.effectId + "_potion", "Lingering Potion of " +effectItems.displayName);

                if(effectItems.longPotion != null){
                    add("item.minecraft.potion.effect.long_" + effectItems.effectId + "_potion", "Potion of " +effectItems.displayName);
                    add("item.minecraft.splash_potion.effect.long_" + effectItems.effectId + "_potion", "Splash Potion of " +effectItems.displayName);
                    add("item.minecraft.lingering_potion.effect.long_" + effectItems.effectId + "_potion", "Lingering Potion of " +effectItems.displayName);
                }

                if(effectItems.strongPotion != null){
                    add("item.minecraft.potion.effect.strong_" + effectItems.effectId + "_potion", "Potion of " + effectItems.displayName + " II");
                    add("item.minecraft.splash_potion.effect.strong_" + effectItems.effectId + "_potion", "Splash Potion of " + effectItems.displayName + " II");
                    add("item.minecraft.lingering_potion.effect.strong_" + effectItems.effectId + "_potion", "Lingering Potion of " + effectItems.displayName + " II");
                }

                add("effect." + ExtraEffects.MODID + "." + effectItems.effectId + ".description", effectItems.description);
            }
        }
    }

    @SubscribeEvent // on the mod event bus
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new EffectLangProvider(generator.getPackOutput()));
    }
}