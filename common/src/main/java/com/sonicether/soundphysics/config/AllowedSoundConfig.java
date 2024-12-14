package com.sonicether.soundphysics.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.sonicether.soundphysics.Loggers;

import com.sonicether.soundphysics.integration.voicechat.AudioChannel;
import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

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
                Loggers.warn("Failed to set allowed sound entry {}", key);
                continue;
            }
            ResourceLocation resourceLocation;
            try {
                resourceLocation = new ResourceLocation(key);
            } catch (Exception e) {
                Loggers.warn("Failed to set allowed sound entry {}", key);
                continue;
            }

            if (!resourceLocation.getNamespace().equals(AudioChannel.CATEGORY_VOICECHAT)) {
                logIfUnknownSound(resourceLocation);
            }

            setAllowed(resourceLocation.toString(), value);
        }
        saveSync();
    }

    private void logIfUnknownSound(ResourceLocation resourceLocation) {
        try {
            SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(resourceLocation);
            if (soundEvent == null) {
                Loggers.log("Unknown sound in allowed sound config: {}", resourceLocation);
            }
        } catch (Exception e) {
            Loggers.warn("Failed to parse allowed sound entry {}", resourceLocation, e);
        }
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

    public Map<String, Boolean> createDefaultMap() {
        Map<String, Boolean> map = new HashMap<>();
        for (SoundEvent event : BuiltInRegistries.SOUND_EVENT) {
            map.put(event.getLocation().toString(), true);
        }

        map.put(SoundEvents.WEATHER_RAIN.getLocation().toString(), false);
        map.put(SoundEvents.WEATHER_RAIN_ABOVE.getLocation().toString(), false);
        map.put(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString(), false);
        SoundEvents.GOAT_HORN_SOUND_VARIANTS.forEach(r -> map.put(r.key().location().toString(), false));

        return map;
    }

}
