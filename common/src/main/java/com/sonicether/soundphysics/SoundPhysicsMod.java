package com.sonicether.soundphysics;

import com.sonicether.soundphysics.config.SoundRateConfig;
import com.sonicether.soundphysics.config.OcclusionConfig;
import com.sonicether.soundphysics.config.ReflectivityConfig;
import com.sonicether.soundphysics.config.SoundPhysicsConfig;
import de.maxhenkel.configbuilder.ConfigBuilder;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public abstract class SoundPhysicsMod {

    public static final String MODID = "sound_physics_remastered";

    public static SoundPhysicsConfig CONFIG;
    public static ReflectivityConfig REFLECTIVITY_CONFIG;
    public static OcclusionConfig OCCLUSION_CONFIG;
    public static SoundRateConfig SOUND_RATE_CONFIG;

    public void init() {
        initConfig();
    }

    public void initClient() {
        initConfig();
        CONFIG.reloadClient();

        renameAllowedSounds();

        REFLECTIVITY_CONFIG = new ReflectivityConfig(getConfigFolder().resolve(MODID).resolve("reflectivity.properties"));
        OCCLUSION_CONFIG = new OcclusionConfig(getConfigFolder().resolve(MODID).resolve("occlusion.properties"));
        SOUND_RATE_CONFIG = new SoundRateConfig(getConfigFolder().resolve(MODID).resolve("sound_rates.properties"));
    }

    private void initConfig() {
        if (CONFIG == null) {
            CONFIG = ConfigBuilder.builder(SoundPhysicsConfig::new).path(getConfigFolder().resolve(MODID).resolve("soundphysics.properties")).build();
        }
    }

    private void renameAllowedSounds() {
        Path oldPath = getConfigFolder().resolve(MODID).resolve("allowed_sounds.properties");
        Path newPath = getConfigFolder().resolve(MODID).resolve("sound_rates.properties");

        try {
            Files.move(oldPath, newPath);
            Loggers.log("{} file renamed to {}", oldPath.getFileName(), newPath.getFileName());
        } catch (NoSuchFileException | FileAlreadyExistsException ignored) {
        } catch (IOException e) {
            Loggers.error("Error renaming allowed_sounds config", e);
        }
    }

    public abstract Path getConfigFolder();

}
