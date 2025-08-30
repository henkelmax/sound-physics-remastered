package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.integration.voicechat.AudioChannel;
import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
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

        Map<String, Integer> map = createDefaultMap();

        for (String key : properties.keySet()) {
            String valueString = properties.get(key);
            if (valueString == null) {
                continue;
            }
            int value;
            try {
                value = Integer.parseInt(valueString);
            } catch (NumberFormatException ignored) {
                try {
                    boolean enabled = Boolean.parseBoolean(valueString); // Convert allowedSounds to maxSoundsPerTick
                    value = enabled ? -1 : 0;
                } catch (Exception e) {
                    Loggers.warn("Failed to set max sounds per tick entry {}", key);
                    continue;
                }
            }
            ResourceLocation resourceLocation = ResourceLocation.tryParse(key);
            if (resourceLocation == null) {
                Loggers.warn("Failed to set allowed sound entry {}", key);
                continue;
            }

            if (!resourceLocation.getNamespace().equals(AudioChannel.CATEGORY_VOICECHAT)) {
                logIfUnknownSound(resourceLocation);
            }

            map.put(resourceLocation.toString(), value);
        }
        maxSoundsPerTick = ConfigUtils.sortMap(map);

        saveSync();
    }

    private void logIfUnknownSound(ResourceLocation resourceLocation) {
        try {
            SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(resourceLocation).map(Holder.Reference::value).orElse(null);
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

    public Map<String, Integer> createDefaultMap() {
        Map<String, Integer> map = new HashMap<>();
        for (SoundEvent event : BuiltInRegistries.SOUND_EVENT) {
            map.put(event.location().toString(), -1);
        }

        map.put(SoundEvents.WEATHER_RAIN.location().toString(), 0);
        map.put(SoundEvents.WEATHER_RAIN_ABOVE.location().toString(), 0);
        map.put(SoundEvents.LIGHTNING_BOLT_THUNDER.location().toString(), 0);
        SoundEvents.GOAT_HORN_SOUND_VARIANTS.forEach(r -> map.put(r.key().location().toString(), 0));

        return map;
    }

}