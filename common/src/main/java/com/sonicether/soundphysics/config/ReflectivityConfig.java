package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysics;
import com.sonicether.soundphysics.SoundPhysicsMod;
import de.maxhenkel.configbuilder.PropertyConfig;
import net.minecraft.world.level.block.SoundType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ReflectivityConfig extends PropertyConfig {

    private Map<SoundType, Double> reflectivity;

    public ReflectivityConfig(Path path) {
        super(path);
        save();
    }

    @Override
    public void load() throws IOException {
        super.load();

        reflectivity = createDefaultMap();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            double value;
            try {
                value = Double.parseDouble((String) entry.getValue());
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
    }

    @Override
    public void saveSync() {
        properties.clear();

        for (Map.Entry<SoundType, Double> entry : reflectivity.entrySet()) {
            properties.put(SoundTypes.getName(entry.getKey()), String.valueOf(entry.getValue()));
        }

        super.saveSync();
    }

    public Map<SoundType, Double> getReflectivities() {
        return reflectivity;
    }

    public double getReflectivity(SoundType soundType) {
        return reflectivity.getOrDefault(soundType, SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get());
    }

    public ReflectivityConfig setReflectivity(SoundType soundType, double value) {
        reflectivity.put(soundType, value);
        return this;
    }

    public Map<SoundType, Double> createDefaultMap() {
        Map<SoundType, Double> map = new HashMap<>();
        for (SoundType type : SoundTypes.getTranslationMap().keySet()) {
            map.put(type, SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get());
        }

        map.put(SoundType.STONE, 1.5D);
        map.put(SoundType.NETHERITE_BLOCK, 1.5D);
        map.put(SoundType.TUFF, 1.5D);
        map.put(SoundType.AMETHYST, 1.5D);
        map.put(SoundType.BASALT, 1.5D);
        map.put(SoundType.CALCITE, 1.5D);
        map.put(SoundType.BONE_BLOCK, 1.5D);
        map.put(SoundType.COPPER, 1.25D);
        map.put(SoundType.DEEPSLATE, 1.5D);
        map.put(SoundType.DEEPSLATE_BRICKS, 1.5D);
        map.put(SoundType.DEEPSLATE_TILES, 1.5D);
        map.put(SoundType.POLISHED_DEEPSLATE, 1.5D);
        map.put(SoundType.NETHER_BRICKS, 1.5D);
        map.put(SoundType.NETHERRACK, 1.1D);
        map.put(SoundType.NETHER_GOLD_ORE, 1.1D);
        map.put(SoundType.NETHER_ORE, 1.1D);
        map.put(SoundType.STEM, 0.4D);
        map.put(SoundType.WOOL, 0.1D);
        map.put(SoundType.HONEY_BLOCK, 0.1D);
        map.put(SoundType.MOSS, 0.1D);
        map.put(SoundType.SOUL_SAND, 0.2D);
        map.put(SoundType.SOUL_SOIL, 0.2D);
        map.put(SoundType.CORAL_BLOCK, 0.2D);
        map.put(SoundType.METAL, 1.25D);
        map.put(SoundType.WOOD, 0.4D);
        map.put(SoundType.GRAVEL, 0.3D);
        map.put(SoundType.GRASS, 0.3D);
        map.put(SoundType.GLASS, 0.75D);
        map.put(SoundType.SAND, 0.2D);
        map.put(SoundType.SNOW, 0.15D);

        return map;
    }

}
