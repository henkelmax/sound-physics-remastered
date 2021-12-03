package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysics;
import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.ConfigEntry;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SoundPhysicsConfig {

    public final ConfigEntry<Boolean> enabled;

    public final ConfigEntry<Double> attenuationFactor;
    public final ConfigEntry<Double> reverbGain;
    public final ConfigEntry<Double> reverbBrightness;
    public final ConfigEntry<Double> blockAbsorption;
    public final ConfigEntry<Double> occlusionVariation;
    public final ConfigEntry<Double> defaultBlockReflectivity;
    public final ConfigEntry<Double> defaultBlockOcclusionFactor;
    public final ConfigEntry<Double> soundDistanceAllowance;
    public final ConfigEntry<Double> airAbsorption;
    public final ConfigEntry<Double> underwaterFilter;
    public final ConfigEntry<String> soundBlacklistRegex;

    public final ConfigEntry<Integer> environmentEvaluationRayCount;
    public final ConfigEntry<Integer> environmentEvaluationRayBounces;
    public final ConfigEntry<Double> nonFullBlockOcclusionFactor;
    public final ConfigEntry<Integer> maxOcclusionRays;
    public final ConfigEntry<Double> maxOcclusion;
    public final ConfigEntry<Boolean> strictOcclusion;
    public final ConfigEntry<Boolean> soundDirectionEvaluation;
    public final ConfigEntry<Boolean> redirectNonOccludedSounds;

    public final ConfigEntry<Boolean> debugLogging;
    public final ConfigEntry<Boolean> occlusionLogging;
    public final ConfigEntry<Boolean> environmentLogging;
    public final ConfigEntry<Boolean> performanceLogging;
    public final ConfigEntry<Boolean> renderSoundBounces;
    public final ConfigEntry<Boolean> renderOcclusion;

    public Pattern soundBlacklist;

    public SoundPhysicsConfig(ConfigBuilder builder) {
        enabled = builder.booleanEntry("enabled", true);

        attenuationFactor = builder.doubleEntry("attenuation_factor", 1D, 0.1D, 1D);
        reverbGain = builder.doubleEntry("reverb_gain", 1D, 0.1D, 2D);
        reverbBrightness = builder.doubleEntry("reverb_brightness", 1D, 0.1D, 2D);
        blockAbsorption = builder.doubleEntry("block_absorption", 1D, 0.1D, 4D);
        occlusionVariation = builder.doubleEntry("occlusion_variation", 0.35D, 0D, 16D);
        defaultBlockReflectivity = builder.doubleEntry("default_block_reflectivity", 0.5D, 0.1D, 4D);
        defaultBlockOcclusionFactor = builder.doubleEntry("default_block_occlusion_factor", 1D, 0D, 10D);
        soundDistanceAllowance = builder.doubleEntry("sound_distance_allowance", 4D, 1D, 6D);
        airAbsorption = builder.doubleEntry("air_absorption", 1D, 0D, 5D);
        underwaterFilter = builder.doubleEntry("underwater_filter", 1D, 0D, 1D);
        soundBlacklistRegex = builder.stringEntry("sound_blacklist_regex", ".*rain.*");

        environmentEvaluationRayCount = builder.integerEntry("environment_evaluation_ray_count", 32, 8, 64);
        environmentEvaluationRayBounces = builder.integerEntry("environment_evaluation_ray_bounces", 4, 2, 64);
        nonFullBlockOcclusionFactor = builder.doubleEntry("non_full_block_occlusion_factor", 0.25D, 0D, 1D);
        maxOcclusionRays = builder.integerEntry("max_occlusion_rays", 16, 1, 128);
        maxOcclusion = builder.doubleEntry("max_occlusion", 64D, 0D, 1024D);
        strictOcclusion = builder.booleanEntry("strict_occlusion", false);
        soundDirectionEvaluation = builder.booleanEntry("sound_direction_evaluation", true);
        redirectNonOccludedSounds = builder.booleanEntry("redirect_non_occluded_sounds", true);

        debugLogging = builder.booleanEntry("debug_logging", false);
        occlusionLogging = builder.booleanEntry("occlusion_logging", false);
        environmentLogging = builder.booleanEntry("environment_logging", false);
        performanceLogging = builder.booleanEntry("performance_logging", false);
        renderSoundBounces = builder.booleanEntry("render_sound_bounces", false);
        renderOcclusion = builder.booleanEntry("render_occlusion", false);
    }

    public void reload() {
        SoundPhysics.LOGGER.info("Reloading config");
        try {
            soundBlacklist = Pattern.compile(soundBlacklistRegex.get());
        } catch (PatternSyntaxException e) {
            SoundPhysics.LOGGER.warn("Failed to parse sound blacklist regex '{}'", soundBlacklistRegex.get());
            soundBlacklist = Pattern.compile(soundBlacklistRegex.getDefault());
        }
        SoundPhysics.LOGGER.info("Reloading reverb parameters");
        SoundPhysics.syncReverbParams();
    }

}
