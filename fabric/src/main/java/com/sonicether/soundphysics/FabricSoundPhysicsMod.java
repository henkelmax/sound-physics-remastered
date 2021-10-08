package com.sonicether.soundphysics;

import com.sonicether.soundphysics.config.SoundPhysicsConfig;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;

public class FabricSoundPhysicsMod extends SoundPhysicsMod implements ModInitializer {

    @Override
    public void onInitialize() {
        init();
    }

    @Override
    public SoundPhysicsConfig createConfig() {
        return ConfigBuilder.build(Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve(MODID).resolve("soundphysics.properties"), true, SoundPhysicsConfig::new);
    }
}
