package com.sonicether.soundphysics;

import com.sonicether.soundphysics.config.*;
import de.maxhenkel.configbuilder.ConfigBuilder;

import java.nio.file.Path;

public abstract class SoundPhysicsMod {

    public static final String MODID = "sound_physics_remastered";

    public static SoundPhysicsConfig CONFIG;
    public static ReflectivityConfig REFLECTIVITY_CONFIG;
    public static OcclusionConfig OCCLUSION_CONFIG;
    public static AllowedSoundConfig ALLOWED_SOUND_CONFIG;
    public static SoundAbsorptionConfig SOUND_ABSORPTION_CONFIG;
    public static SoundReflectivityConfig SOUND_REFLECTIVITY_CONFIG;

    public void init() {
        initConfig();
    }

    public void initClient() {
        initConfig();
        CONFIG.reloadClient();

        REFLECTIVITY_CONFIG = new ReflectivityConfig(getConfigFolder().resolve(MODID).resolve("reflectivity.properties"));
        OCCLUSION_CONFIG = new OcclusionConfig(getConfigFolder().resolve(MODID).resolve("occlusion.properties"));
        ALLOWED_SOUND_CONFIG = new AllowedSoundConfig(getConfigFolder().resolve(MODID).resolve("allowed_sounds.properties"));
        SOUND_ABSORPTION_CONFIG = new SoundAbsorptionConfig(getConfigFolder().resolve(MODID).resolve("sound_absorption.properties"));
        SOUND_REFLECTIVITY_CONFIG = new SoundReflectivityConfig(getConfigFolder().resolve(MODID).resolve("sound_reflectivity.properties"));
    }

    private void initConfig() {
        if (CONFIG == null) {
            CONFIG = ConfigBuilder.builder(SoundPhysicsConfig::new).path(getConfigFolder().resolve(MODID).resolve("soundphysics.properties")).build();
        }
    }

    public abstract Path getConfigFolder();

}
