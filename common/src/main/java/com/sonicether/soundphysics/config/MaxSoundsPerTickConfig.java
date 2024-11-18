package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.Loggers;
import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MaxSoundsPerTickConfig extends CommentedPropertyConfig {

    private Map<String, Integer> maxSoundsPerTick;

    public MaxSoundsPerTickConfig(Path path) {
        super(new CommentedProperties(false));
        this.path = path;
        reload();
    }

    @Override
    public void load() throws IOException {
        super.load();

        maxSoundsPerTick = createDefaultMap();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            int value;
            try {
                value = Integer.parseInt(entry.getValue());
            } catch (NumberFormatException ignored) {
                try {
                    boolean is_enabled = Boolean.parseBoolean(entry.getValue()); // Convert allowedSounds to maxSoundsPerTick
                    value = is_enabled ? -1 : 0;
                } catch (Exception e) {
                    Loggers.warn("Failed to set max sounds per tick entry {}", key);
                    continue;
                }
            }
            SoundEvent soundEvent = null;
            try {
                soundEvent = Registry.SOUND_EVENT.get(new ResourceLocation(key));
            } catch (Exception e) {
                Loggers.warn("Failed to set max sounds per tick entry {}", key, e);
            }
            if (soundEvent == null) {
                Loggers.warn("Sound event {} not found", key);
                continue;
            }

            setMaxCount(soundEvent, value);
        }
        saveSync();
    }

    @Override
    public void saveSync() {
        properties.clear();

        properties.addHeaderComment("Max sounds per tick");
        properties.addHeaderComment("Set to '-1' for an unlimited number of sounds per tick rendered via the sound physics mod for that sound.");
        properties.addHeaderComment("Set to '0' to disable sound physics for that sound.");
        properties.addHeaderComment("Set to '>=1' to configure the maximum number of sounds per tick rendered via the sound physics mod for that sound.");
        properties.addHeaderComment("This can help prevent lag when some mod or mechanism produces hundreds of sounds per tick, such as large Create mod mining contraptions.");

        for (Map.Entry<String, Integer> entry : maxSoundsPerTick.entrySet()) {
            properties.set(entry.getKey(), String.valueOf(entry.getValue()));
        }

        super.saveSync();
    }

    public Map<String, Integer> getMaxSoundsPerTick() {
        return maxSoundsPerTick;
    }

    public Integer getMaxCount(String soundEvent) {
        int count = maxSoundsPerTick.getOrDefault(soundEvent, -1);
        return count <= -1 ? Integer.MAX_VALUE : count;
    }

    public MaxSoundsPerTickConfig setMaxCount(String soundEvent, Integer count) {
        maxSoundsPerTick.put(soundEvent, count);
        return this;
    }

    public MaxSoundsPerTickConfig setMaxCount(SoundEvent soundEvent, Integer count) {
        return setMaxCount(soundEvent.getLocation().toString(), count);
    }

    public Map<String, Integer> createDefaultMap() {
        Map<String, Integer> map = new HashMap<>();
        for (SoundEvent event : Registry.SOUND_EVENT) {
            map.put(event.getLocation().toString(), -1);
        }

        map.put(SoundEvents.WEATHER_RAIN.getLocation().toString(), 0);
        map.put(SoundEvents.WEATHER_RAIN_ABOVE.getLocation().toString(), 0);
        map.put(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString(), 0);
        SoundEvents.GOAT_HORN_SOUND_VARIANTS.forEach(r -> map.put(r.getLocation().toString(), 0));

        return map;
    }

}
