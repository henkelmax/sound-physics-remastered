package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysics;
import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AllowedSoundConfig extends CommentedPropertyConfig {

    private Map<String, Boolean> allowedSounds;

    public AllowedSoundConfig(Path path) {
        super(new CommentedProperties(false));
        this.path = path;
        reload();
    }

    @Override
    public void load() throws IOException {
        super.load();

        allowedSounds = createDefaultMap();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            boolean value;
            try {
                value = Boolean.parseBoolean(entry.getValue());
            } catch (Exception e) {
                SoundPhysics.LOGGER.warn("Failed to set allowed sound entry {}", key);
                continue;
            }
            SoundEvent soundEvent = null;
            try {
                soundEvent = Registry.SOUND_EVENT.get(new ResourceLocation(key));
            } catch (Exception e) {
                SoundPhysics.LOGGER.warn("Failed to set allowed sound entry {}", key, e);
            }
            if (soundEvent == null) {
                SoundPhysics.LOGGER.warn("Sound event {} not found", key);
                continue;
            }

            setAllowed(soundEvent, value);
        }
        saveSync();
    }

    @Override
    public void saveSync() {
        properties.clear();

        properties.addHeaderComment("Allowed sounds");
        properties.addHeaderComment("Set to 'false' to disable sound physics for that sound");

        for (Map.Entry<String, Boolean> entry : allowedSounds.entrySet()) {
            properties.set(entry.getKey(), String.valueOf(entry.getValue()));
        }

        super.saveSync();
    }

    public Map<String, Boolean> getAllowedSounds() {
        return allowedSounds;
    }

    public boolean isAllowed(String soundEvent) {
        return allowedSounds.getOrDefault(soundEvent, true);
    }

    public AllowedSoundConfig setAllowed(String soundEvent, boolean allowed) {
        allowedSounds.put(soundEvent, allowed);
        return this;
    }

    public AllowedSoundConfig setAllowed(SoundEvent soundEvent, boolean allowed) {
        return setAllowed(soundEvent.getLocation().toString(), allowed);
    }

    public Map<String, Boolean> createDefaultMap() {
        Map<String, Boolean> map = new HashMap<>();
        for (SoundEvent event : Registry.SOUND_EVENT) {
            map.put(event.getLocation().toString(), true);
        }

        map.put(SoundEvents.WEATHER_RAIN.getLocation().toString(), false);
        map.put(SoundEvents.WEATHER_RAIN_ABOVE.getLocation().toString(), false);

        return map;
    }

}
