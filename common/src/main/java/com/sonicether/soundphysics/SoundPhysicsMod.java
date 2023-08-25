package com.sonicether.soundphysics;

import com.sonicether.soundphysics.config.OcclusionConfig;
import com.sonicether.soundphysics.config.ReflectivityConfig;
import com.sonicether.soundphysics.config.AllowedSoundConfig;
import com.sonicether.soundphysics.config.SoundPhysicsConfig;
import de.maxhenkel.configbuilder.ConfigBuilder;

import java.nio.file.Path;

public abstract class SoundPhysicsMod {

    public static final String MODID = "sound_physics_remastered";

    public static SoundPhysicsConfig CONFIG;
    public static ReflectivityConfig REFLECTIVITY_CONFIG;
    public static OcclusionConfig OCCLUSION_CONFIG;
    public static AllowedSoundConfig ALLOWED_SOUND_CONFIG;

    public void init() {
        initConfig();
    }

    public void initClient() {
        initConfig();
        CONFIG.reloadClient();

        REFLECTIVITY_CONFIG = new ReflectivityConfig(getConfigFolder().resolve(MODID).resolve("reflectivity.properties"));
        OCCLUSION_CONFIG = new OcclusionConfig(getConfigFolder().resolve(MODID).resolve("occlusion.properties"));
        ALLOWED_SOUND_CONFIG = new AllowedSoundConfig(getConfigFolder().resolve(MODID).resolve("allowed_sounds.properties"));
    }

    private void initConfig() {
        if (CONFIG == null) {
            CONFIG = ConfigBuilder.builder(SoundPhysicsConfig::new).path(getConfigFolder().resolve(MODID).resolve("soundphysics.properties")).build();
        }
    }

    public abstract Path getConfigFolder();

}
