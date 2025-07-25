package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.integration.voicechat.AudioChannel;
import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SoundReflectivityConfig extends CommentedPropertyConfig {

    private Map<String, Float> soundReflectivities;

    public SoundReflectivityConfig(Path path) {
        super(new CommentedProperties(false));
        this.path = path;
        reload();
    }

    @Override
    public void load() throws IOException {
        super.load();

        Map<String, Float> map = createDefaultMap();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            float value;
            try {
                value = Float.parseFloat(entry.getValue());
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

            map.put(resourceLocation.toString(), value);
        }

        soundReflectivities = ConfigUtils.sortMap(map);

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

        properties.addHeaderComment("Sound specific absorption multipliers");

        for (Map.Entry<String, Float> entry : soundReflectivities.entrySet()) {
            properties.set(entry.getKey(), String.valueOf(entry.getValue()));
        }

        super.saveSync();
    }

    public Map<String, Float> getMap() {
        return soundReflectivities;
    }

    public float getValue(String soundEvent) {
        return soundReflectivities.getOrDefault(soundEvent, 1.0F);
    }

    public Map<String, Float> createDefaultMap() {
        Map<String, Float> map = new HashMap<>();
        for (SoundEvent event : BuiltInRegistries.SOUND_EVENT) {
            map.put(event.getLocation().toString(), 1.0F);
        }

        map.put(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString(), 2.5F);
        map.put(SoundEvents.GENERIC_EXPLODE.getLocation().toString(), 2.5F);
        SoundEvents.GOAT_HORN_SOUND_VARIANTS.forEach(r -> map.put(r.key().location().toString(), 1.5F));

        return map;
    }

    public void setValue(String key, float value) {
        Map<String, Float> map = soundReflectivities;
        map.put(key, value);
        soundReflectivities = map;
    }

}
