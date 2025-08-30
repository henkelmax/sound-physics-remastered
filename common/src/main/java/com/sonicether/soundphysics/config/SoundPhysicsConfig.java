package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.SoundPhysics;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;

public class SoundPhysicsConfig {

    public final ConfigEntry<Boolean> enabled;

    public final ConfigEntry<Float> attenuationFactor;
    public final ConfigEntry<Float> reverbAttenuationDistance;
    public final ConfigEntry<Float> reverbGain;
    public final ConfigEntry<Float> reverbBrightness;
    public final ConfigEntry<Float> reverbDistance;
    public final ConfigEntry<Float> blockAbsorption;
    public final ConfigEntry<Float> occlusionVariation;
    public final ConfigEntry<Float> defaultBlockReflectivity;
    public final ConfigEntry<Float> defaultBlockOcclusionFactor;
    public final ConfigEntry<Float> soundDistanceAllowance;
    public final ConfigEntry<Float> airAbsorption;
    public final ConfigEntry<Float> underwaterFilter;
    public final ConfigEntry<Boolean> evaluateAmbientSounds;

    public final ConfigEntry<Integer> environmentEvaluationRayCount;
    public final ConfigEntry<Integer> environmentEvaluationRayBounces;
    public final ConfigEntry<Float> nonFullBlockOcclusionFactor;
    public final ConfigEntry<Integer> maxOcclusionRays;
    public final ConfigEntry<Float> maxOcclusion;
    public final ConfigEntry<Boolean> strictOcclusion;
    public final ConfigEntry<Boolean> soundDirectionEvaluation;
    public final ConfigEntry<Boolean> redirectNonOccludedSounds;
    public final ConfigEntry<Boolean> updateMovingSounds;
    public final ConfigEntry<Integer> soundUpdateInterval;
    public final ConfigEntry<Double> maxSoundProcessingDistance;
    public final ConfigEntry<Boolean> unsafeLevelAccess;
    public final ConfigEntry<Integer> levelCloneRange;
    public final ConfigEntry<Integer> levelCloneMaxRetainTicks;
    public final ConfigEntry<Integer> levelCloneMaxRetainBlockDistance;

    public final ConfigEntry<Boolean> debugLogging;
    public final ConfigEntry<Boolean> occlusionLogging;
    public final ConfigEntry<Boolean> environmentLogging;
    public final ConfigEntry<Boolean> performanceLogging;
    public final ConfigEntry<Boolean> renderSoundBounces;
    public final ConfigEntry<Boolean> renderOcclusion;

    public final ConfigEntry<Boolean> simpleVoiceChatIntegration;
    public final ConfigEntry<Boolean> hearSelf;

    public SoundPhysicsConfig(ConfigBuilder builder) {
        enabled = builder.booleanEntry("enabled", true)
                .comment("Enables/Disables all sound effects");

        attenuationFactor = builder
                .floatEntry("attenuation_factor", 1F, 0.1F, 1F)
                .comment(
                        "Affects how quiet a sound gets based on distance",
                        "Lower values mean distant sounds are louder",
                        "This setting requires you to be in singleplayer or having the mod installed on the server",
                        "1.0 is the physically correct value"
                );
        reverbAttenuationDistance = builder
                .floatEntry("reverb_attenuation_distance", 0F, 0F, 512F)
                .comment(
                        "The ray distance at which reverb starts",
                        "0.0 disables reverb attenuation"
                );
        reverbGain = builder
                .floatEntry("reverb_gain", 1F, 0.1F, 2F)
                .comment("The volume of simulated reverberations");
        reverbBrightness = builder.floatEntry("reverb_brightness", 1F, 0.1F, 2F)
                .comment(
                        "The brightness of reverberation",
                        "Higher values result in more high frequencies in reverberation",
                        "Lower values give a more muffled sound to the reverb"
                );
        reverbDistance = builder
                .floatEntry("reverb_distance", 1.5F, 0.1F, 16F)
                .comment("The distance of reverb relative to the sound distance");
        blockAbsorption = builder.floatEntry("block_absorption", 1F, 0.1F, 4F)
                .comment("The amount of sound that will be absorbed when traveling through blocks");
        occlusionVariation = builder.floatEntry("occlusion_variation", 0.35F, 0F, 16F)
                .comment("Higher values mean smaller objects won't be considered as occluding");
        defaultBlockReflectivity = builder.floatEntry("default_block_reflectivity", 0.5F, 0.1F, 4F)
                .comment(
                        "The default amount of sound reflectance energy for all blocks",
                        "Lower values result in more conservative reverb simulation with shorter reverb tails",
                        "Higher values result in more generous reverb simulation with higher reverb tails"
                );
        defaultBlockOcclusionFactor = builder.floatEntry("default_block_occlusion_factor", 1F, 0F, 10F)
                .comment(
                        "The default amount of occlusion for all blocks",
                        "Lower values will result in sounds being less muffled through walls",
                        "Higher values mean sounds will be not audible though thicker walls"
                );
        soundDistanceAllowance = builder.floatEntry("sound_distance_allowance", 4F, 1F, 6F)
                .comment(
                        "Minecraft won't allow sounds to play past a certain distance",
                        "This parameter is a multiplier for how far away a sound source is allowed to be in order for it to actually play",
                        "This setting only takes affect in singleplayer worlds and when installed on the server"
                );
        airAbsorption = builder.floatEntry("air_absorption", 1F, 0F, 5F)
                .comment(
                        "A value controlling the amount that air absorbs high frequencies with distance",
                        "A value of 1.0 is physically correct for air with normal humidity and temperature",
                        "Higher values mean air will absorb more high frequencies with distance",
                        "0 disables this effect"
                );
        underwaterFilter = builder.floatEntry("underwater_filter", 0.9F, 0F, 1F)
                .comment(
                        "How much sound is filtered when the player is underwater",
                        "0.0 means no filter",
                        "1.0 means fully filtered"
                );
        evaluateAmbientSounds = builder.booleanEntry("evaluate_ambient_sounds", false)
                .comment(
                        "Whether sounds like cave, nether or underwater ambient sounds should have sound physics"
                );

        environmentEvaluationRayCount = builder.integerEntry("environment_evaluation_ray_count", 32, 8, 64)
                .comment(
                        "The number of rays to trace to determine reverberation for each sound source",
                        "More rays provides more consistent tracing results but takes more time to calculate",
                        "Decrease this value if you experience lag spikes when sounds play"
                );
        environmentEvaluationRayBounces = builder.integerEntry("environment_evaluation_ray_bounces", 4, 2, 64)
                .comment(
                        "The number of rays bounces to trace to determine reverberation for each sound source",
                        "More bounces provides more echo and sound ducting but takes more time to calculate",
                        "Decrease this value if you experience lag spikes when sounds play"
                );
        nonFullBlockOcclusionFactor = builder.floatEntry("non_full_block_occlusion_factor", 0.25F, 0F, 1F)
                .comment("If sound hits a non-full-square side, block occlusion is multiplied by this");
        maxOcclusionRays = builder.integerEntry("max_occlusion_rays", 16, 1, 128)
                .comment(
                        "The maximum amount of rays to determine occlusion",
                        "Directly correlates to the amount of blocks between walls that are considered"
                );
        maxOcclusion = builder.floatEntry("max_occlusion", 64F, 0F, 1024F)
                .comment("The amount at which occlusion is capped");
        strictOcclusion = builder.booleanEntry("strict_occlusion", false)
                .comment("If enabled, the occlusion calculation only uses one path between the sound source and the listener instead of 9");
        soundDirectionEvaluation = builder.booleanEntry("sound_direction_evaluation", true)
                .comment("Whether to try calculating where the sound should come from based on reflections");
        redirectNonOccludedSounds = builder.booleanEntry("redirect_non_occluded_sounds", true)
                .comment("Skip redirecting non-occluded sounds (the ones you can see directly)");
        updateMovingSounds = builder.booleanEntry("update_moving_sounds", false)
                .comment("If music discs or other longer sounds should be frequently reevaluated");
        soundUpdateInterval = builder.integerEntry("sound_update_interval", 5, 1, Integer.MAX_VALUE)
                .comment(
                        "The interval in ticks that moving sounds are reevaluated",
                        "Lower values mean more frequent reevaluation but also more lag",
                        "This option only takes effect if update_moving_sounds is enabled"
                );
        maxSoundProcessingDistance = builder.doubleEntry("max_sound_processing_distance", 512D, 0D, Double.MAX_VALUE)
                .comment(
                        "The maximum distance a sound can be processed"
                );
        unsafeLevelAccess = builder.booleanEntry("unsafe_level_access", false)
                .comment(
                        "Disable level clone and cache. This will fall back to original main thread access.",
                        "WARNING! Enabling this will cause instability and issues with other mods."
                );
        levelCloneRange = builder.integerEntry("level_clone_range", 4, 2, 16)
                .comment("The radius of chunks to clone for level access");
        levelCloneMaxRetainTicks = builder.integerEntry("level_clone_max_retain_ticks", 20, 1, Integer.MAX_VALUE,
                "The maximum number of ticks to retain the cloned level in the cache"
        );
        levelCloneMaxRetainBlockDistance = builder.integerEntry("level_clone_max_retain_block_distance", 16, 1, Integer.MAX_VALUE,
                "The maximum distance a player can move from the cloned origin before invalidation"
        );


        debugLogging = builder.booleanEntry("debug_logging", false)
                .comment("Enables debug logging");
        occlusionLogging = builder.booleanEntry("occlusion_logging", false)
                .comment("Provides more information about occlusion in the logs");
        environmentLogging = builder.booleanEntry("environment_logging", false)
                .comment("Provides more information about the environment calculation in the logs");
        performanceLogging = builder.booleanEntry("performance_logging", false)
                .comment("Provides more information about how long computations take");
        renderSoundBounces = builder.booleanEntry("render_sound_bounces", false)
                .comment("If enabled, the path of the sound will be rendered in game");
        renderOcclusion = builder.booleanEntry("render_occlusion", false)
                .comment("If enabled, occlusion will be visualized in game");

        simpleVoiceChatIntegration = builder.booleanEntry("simple_voice_chat_integration", true)
                .comment("Enables/Disables sound effects for Simple Voice Chat audio");
        hearSelf = builder.booleanEntry("simple_voice_chat_hear_self", false)
                .comment("Enables/Disables hearing your own echo with Simple Voice Chat");
    }

    public void reloadClient() {
        Loggers.log("Reloading reverb parameters");
        SoundPhysics.syncReverbParams();
    }

}
