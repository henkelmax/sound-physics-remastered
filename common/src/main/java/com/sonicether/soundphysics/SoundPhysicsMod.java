package com.sonicether.soundphysics;

import com.sonicether.soundphysics.config.ReflectivityConfig;
import com.sonicether.soundphysics.config.SoundPhysicsConfig;
import net.minecraft.client.Minecraft;

public abstract class SoundPhysicsMod {

    public static final String MODID = "soundphysics";

    public static SoundPhysicsConfig CONFIG;
    public static ReflectivityConfig REFLECTIVITY_CONFIG;

    public void init() {

        CONFIG = createConfig();
        CONFIG.reload();

        REFLECTIVITY_CONFIG = new ReflectivityConfig(Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve(MODID).resolve("reflectivity.properties"));
    }

    public abstract SoundPhysicsConfig createConfig();

}
