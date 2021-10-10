package com.sonicether.soundphysics;

import com.sonicether.soundphysics.config.SoundPhysicsConfig;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricSoundPhysicsMod extends SoundPhysicsMod implements ModInitializer {

    @Override
    public void onInitialize() {
        init();
    }

    @Override
    public SoundPhysicsConfig createConfig() {
        return ConfigBuilder.build(getConfigFolder().resolve(MODID).resolve("soundphysics.properties"), true, SoundPhysicsConfig::new);
    }

    @Override
    public Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
