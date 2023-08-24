package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysics;
import com.sonicether.soundphysics.SoundPhysicsMod;
import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import net.minecraft.world.level.block.SoundType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ReflectivityConfig extends CommentedPropertyConfig {

    private Map<SoundType, Float> reflectivity;

    public ReflectivityConfig(Path path) {
        super(new CommentedProperties(false));
        this.path = path;
        reload();
    }

    @Override
    public void load() throws IOException {
        super.load();

        reflectivity = createDefaultMap();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            float value;
            try {
                value = Float.parseFloat(entry.getValue());
            } catch (NumberFormatException e) {
                SoundPhysics.LOGGER.warn("Failed to parse reflectivity of {}", key);
                continue;
            }
            SoundType soundType = SoundTypes.getSoundType(key);
            if (soundType == null) {
                SoundPhysics.LOGGER.warn("Sound type {} not found", key);
                continue;
            }

            reflectivity.put(soundType, value);
        }
        saveSync();
    }

    @Override
    public void saveSync() {
        properties.clear();

        for (Map.Entry<SoundType, Float> entry : reflectivity.entrySet()) {
            properties.set(SoundTypes.getName(entry.getKey()), String.valueOf(entry.getValue()));
        }

        super.saveSync();
    }

    public Map<SoundType, Float> getReflectivities() {
        return reflectivity;
    }

    public double getReflectivity(SoundType soundType) {
        return reflectivity.getOrDefault(soundType, SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get());
    }

    public ReflectivityConfig setReflectivity(SoundType soundType, float value) {
        reflectivity.put(soundType, value);
        return this;
    }

    public Map<SoundType, Float> createDefaultMap() {
        Map<SoundType, Float> map = new HashMap<>();
        for (SoundType type : SoundTypes.getTranslationMap().keySet()) {
            map.put(type, SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get());
        }

        map.put(SoundType.STONE, 1.5F);
        map.put(SoundType.NETHERITE_BLOCK, 1.5F);
        map.put(SoundType.TUFF, 1.5F);
        map.put(SoundType.AMETHYST, 1.5F);
        map.put(SoundType.BASALT, 1.5F);
        map.put(SoundType.CALCITE, 1.5F);
        map.put(SoundType.BONE_BLOCK, 1.5F);
        map.put(SoundType.COPPER, 1.25F);
        map.put(SoundType.DEEPSLATE, 1.5F);
        map.put(SoundType.DEEPSLATE_BRICKS, 1.5F);
        map.put(SoundType.DEEPSLATE_TILES, 1.5F);
        map.put(SoundType.POLISHED_DEEPSLATE, 1.5F);
        map.put(SoundType.NETHER_BRICKS, 1.5F);
        map.put(SoundType.NETHERRACK, 1.1F);
        map.put(SoundType.NETHER_GOLD_ORE, 1.1F);
        map.put(SoundType.NETHER_ORE, 1.1F);
        map.put(SoundType.STEM, 0.4F);
        map.put(SoundType.WOOL, 0.1F);
        map.put(SoundType.HONEY_BLOCK, 0.1F);
        map.put(SoundType.MOSS, 0.1F);
        map.put(SoundType.SOUL_SAND, 0.2F);
        map.put(SoundType.SOUL_SOIL, 0.2F);
        map.put(SoundType.CORAL_BLOCK, 0.2F);
        map.put(SoundType.METAL, 1.25F);
        map.put(SoundType.WOOD, 0.4F);
        map.put(SoundType.GRAVEL, 0.3F);
        map.put(SoundType.GRASS, 0.3F);
        map.put(SoundType.GLASS, 0.75F);
        map.put(SoundType.SAND, 0.2F);
        map.put(SoundType.SNOW, 0.15F);

        return map;
    }

}
