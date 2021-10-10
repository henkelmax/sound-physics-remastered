package com.sonicether.soundphysics.integration;

import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.config.SoundTypes;
import de.maxhenkel.configbuilder.ConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.SoundType;

import java.util.Map;

public class ClothConfigIntegration {

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder
                .create()
                .setParentScreen(parent)
                .setTitle(new TranslatableComponent("cloth_config.sound_physics_remastered.settings"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(new TranslatableComponent("cloth_config.sound_physics_remastered.category.general"));

        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.enabled"), SoundPhysicsMod.CONFIG.enabled));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.attenuation_factor"), SoundPhysicsMod.CONFIG.attenuationFactor));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.reverb_gain"), SoundPhysicsMod.CONFIG.reverbGain));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.reverb_brightness"), SoundPhysicsMod.CONFIG.reverbBrightness));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.block_absorption"), SoundPhysicsMod.CONFIG.blockAbsorption));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.occlusion_variation"), SoundPhysicsMod.CONFIG.occlusionVariation));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.default_block_reflectivity"), SoundPhysicsMod.CONFIG.defaultBlockReflectivity));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.default_block_occlusion_factor"), SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.sound_distance_allowance"), SoundPhysicsMod.CONFIG.soundDistanceAllowance));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.air_absorption"), SoundPhysicsMod.CONFIG.airAbsorption));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.underwater_filter"), SoundPhysicsMod.CONFIG.underwaterFilter));
        general.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.sound_blacklist_regex"), SoundPhysicsMod.CONFIG.soundBlacklistRegex));

        ConfigCategory performance = builder.getOrCreateCategory(new TranslatableComponent("cloth_config.sound_physics_remastered.category.performance"));

        performance.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.environment_evaluation_ray_count"), SoundPhysicsMod.CONFIG.environmentEvaluationRayCount));
        performance.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.environment_evaluation_ray_bounces"), SoundPhysicsMod.CONFIG.environmentEvaluationRayBounces));
        performance.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.non_full_block_occlusion_factor"), SoundPhysicsMod.CONFIG.nonFullBlockOcclusionFactor));
        performance.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.max_occlusion_rays"), SoundPhysicsMod.CONFIG.maxOcclusionRays));
        performance.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.max_occlusion"), SoundPhysicsMod.CONFIG.maxOcclusion));
        performance.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.strict_occlusion"), SoundPhysicsMod.CONFIG.strictOcclusion));
        performance.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.sound_direction_evaluation"), SoundPhysicsMod.CONFIG.soundDirectionEvaluation));
        performance.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.redirect_non_occluded_sounds"), SoundPhysicsMod.CONFIG.redirectNonOccludedSounds));


        ConfigCategory reflectivity = builder.getOrCreateCategory(new TranslatableComponent("cloth_config.sound_physics_remastered.category.reflectivity"));

        Map<SoundType, Double> defaultReflectivityMap = SoundPhysicsMod.REFLECTIVITY_CONFIG.createDefaultMap();

        for (Map.Entry<SoundType, Double> entry : SoundPhysicsMod.REFLECTIVITY_CONFIG.getReflectivities().entrySet()) {
            DoubleListEntry e = entryBuilder
                    .startDoubleField(SoundTypes.getNameComponent(entry.getKey()), entry.getValue())
                    .setMin(0.01F)
                    .setMax(10F)
                    .setDefaultValue(defaultReflectivityMap.getOrDefault(entry.getKey(), SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get()))
                    .setSaveConsumer(value -> SoundPhysicsMod.REFLECTIVITY_CONFIG.setReflectivity(entry.getKey(), value).save()).build();
            reflectivity.addEntry(e);
        }

        ConfigCategory occlusion = builder.getOrCreateCategory(new TranslatableComponent("cloth_config.sound_physics_remastered.category.occlusion"));

        Map<SoundType, Double> defaultOcclusionMap = SoundPhysicsMod.OCCLUSION_CONFIG.createDefaultMap();

        for (Map.Entry<SoundType, Double> entry : SoundPhysicsMod.OCCLUSION_CONFIG.getOcclusionFactors().entrySet()) {
            DoubleListEntry e = entryBuilder
                    .startDoubleField(SoundTypes.getNameComponent(entry.getKey()), entry.getValue())
                    .setMin(0F)
                    .setMax(10F)
                    .setDefaultValue(defaultOcclusionMap.getOrDefault(entry.getKey(), SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get()))
                    .setSaveConsumer(value -> SoundPhysicsMod.OCCLUSION_CONFIG.setOcclusionFactor(entry.getKey(), value).save()).build();
            occlusion.addEntry(e);
        }

        ConfigCategory logging = builder.getOrCreateCategory(new TranslatableComponent("cloth_config.sound_physics_remastered.category.debug"));

        logging.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.debug_logging"), SoundPhysicsMod.CONFIG.debugLogging));
        logging.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.occlusion_logging"), SoundPhysicsMod.CONFIG.occlusionLogging));
        logging.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.environment_logging"), SoundPhysicsMod.CONFIG.environmentLogging));
        logging.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.performance_logging"), SoundPhysicsMod.CONFIG.performanceLogging));
        logging.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.render_sound_bounces"), SoundPhysicsMod.CONFIG.renderSoundBounces));
        logging.addEntry(fromConfigEntry(entryBuilder, new TranslatableComponent("cloth_config.sound_physics_remastered.render_occlusion"), SoundPhysicsMod.CONFIG.renderOcclusion));

        return builder.build();
    }

    private static <T> AbstractConfigListEntry<T> fromConfigEntry(ConfigEntryBuilder entryBuilder, Component name, ConfigEntry<T> entry) {
        if (entry instanceof de.maxhenkel.configbuilder.ConfigBuilder.DoubleConfigEntry e) {
            return (AbstractConfigListEntry<T>) entryBuilder
                    .startDoubleField(name, e.get())
                    .setMin(e.getMin())
                    .setMax(e.getMax())
                    .setDefaultValue(e::getDefault)
                    .setSaveConsumer(d -> {
                        e.set(d).save();
                        SoundPhysicsMod.CONFIG.reload();
                    })
                    .build();
        } else if (entry instanceof de.maxhenkel.configbuilder.ConfigBuilder.IntegerConfigEntry e) {
            return (AbstractConfigListEntry<T>) entryBuilder
                    .startIntField(name, e.get())
                    .setMin(e.getMin())
                    .setMax(e.getMax())
                    .setDefaultValue(e::getDefault)
                    .setSaveConsumer(d -> {
                        e.set(d).save();
                        SoundPhysicsMod.CONFIG.reload();
                    })
                    .build();
        } else if (entry instanceof de.maxhenkel.configbuilder.ConfigBuilder.BooleanConfigEntry e) {
            return (AbstractConfigListEntry<T>) entryBuilder
                    .startBooleanToggle(name, e.get())
                    .setDefaultValue(e::getDefault)
                    .setSaveConsumer(d -> {
                        e.set(d).save();
                        SoundPhysicsMod.CONFIG.reload();
                    })
                    .build();
        } else if (entry instanceof de.maxhenkel.configbuilder.ConfigBuilder.StringConfigEntry e) {
            return (AbstractConfigListEntry<T>) entryBuilder
                    .startStrField(name, e.get())
                    .setDefaultValue(e::getDefault)
                    .setSaveConsumer(d -> {
                        e.set(d).save();
                        SoundPhysicsMod.CONFIG.reload();
                    })
                    .build();
        }

        return null;
    }

}
