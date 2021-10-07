package com.sonicether.soundphysics.config;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

import static com.sonicether.soundphysics.config.ConfigChanger.changeConfig;
import static java.util.Map.entry;

@SuppressWarnings("unused")
public enum ConfigPresets {
    LOAD_SUCCESS(null),
    DEFAULT((SoundPhysicsConfig c) -> changeConfig(c, true,

            1.0, 1.0, 1.0, 1.0,
            1.0, 4.0, 1.0, 0.8,

            true, 32, 4, false,

            Map.ofEntries(entry(".DEFAULT", 0.5), entry("STONE", 1.0), entry("WOOD", 0.4), entry("GRAVEL", 0.3),
            entry("GRASS", 0.5), entry("METAL", 1.0), entry("GLASS", 0.5), entry("WOOL", 0.05),
            entry("SAND", 0.2), entry("SNOW", 0.2), entry("LADDER", 0.4), entry("ANVIL", 1.0)),

            0.15, 10.0, true, true, 0.5, true,

            false, false, false, false
    )),
    RESET_MATERIALS((SoundPhysicsConfig c) -> changeConfig(c, true,

            null, null, null, null,
            null, null, null, null,

            null, null, null, null,

            Map.ofEntries(entry(".DEFAULT", 0.5), entry("STONE", 1.0), entry("WOOD", 0.4), entry("GRAVEL", 0.3),
            entry("GRASS", 0.5), entry("METAL", 1.0), entry("GLASS", 0.5), entry("WOOL", 0.05),
            entry("SAND", 0.2), entry("SNOW", 0.2), entry("LADDER", 0.4), entry("ANVIL", 1.0)),

            null, null, null,null, null, null,

            null, null, null, null
    )),
    SP1_0_SOUND_OCCLUSION((SoundPhysicsConfig c) -> changeConfig(c, true,

            null, null, null, 10.0,
            null, null, null, null,

            null, null, null, null,

            null,

            null, 10.0, null, null, null, null,

            null, null, null, null
    ));


    private final Consumer<SoundPhysicsConfig> configChanger;
    public void setConfig(){ if (configChanger != null) configChanger.accept(ConfigManager.getConfig());}

    ConfigPresets(@Nullable Consumer<SoundPhysicsConfig> c) { this.configChanger = c; }


}
