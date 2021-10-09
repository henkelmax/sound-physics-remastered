package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysics;
import com.sonicether.soundphysics.SoundPhysicsMod;
import de.maxhenkel.configbuilder.PropertyConfig;
import net.minecraft.world.level.block.SoundType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class OcclusionConfig extends PropertyConfig {

    private Map<SoundType, Double> occlusion;

    public OcclusionConfig(Path path) {
        super(path);
        save();
    }

    @Override
    public void load() throws IOException {
        super.load();

        occlusion = createDefaultMap();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            double value;
            try {
                value = Double.parseDouble((String) entry.getValue());
            } catch (NumberFormatException e) {
                SoundPhysics.LOGGER.warn("Failed to parse occlusion factor of {}", key);
                continue;
            }
            SoundType soundType = SoundTypes.getSoundType(key);
            if (soundType == null) {
                SoundPhysics.LOGGER.warn("Sound type {} not found", key);
                continue;
            }

            occlusion.put(soundType, value);
        }
    }

    @Override
    public void saveSync() {
        properties.clear();

        for (Map.Entry<SoundType, Double> entry : occlusion.entrySet()) {
            properties.put(SoundTypes.getName(entry.getKey()), String.valueOf(entry.getValue()));
        }

        super.saveSync();
    }

    public Map<SoundType, Double> getOcclusionFactors() {
        return occlusion;
    }

    public double getOcclusionFactor(SoundType soundType) {
        return occlusion.getOrDefault(soundType, SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get());
    }

    public OcclusionConfig setOcclusionFactor(SoundType soundType, double value) {
        occlusion.put(soundType, value);
        return this;
    }

    public Map<SoundType, Double> createDefaultMap() {
        Map<SoundType, Double> map = new HashMap<>();
        for (SoundType type : SoundTypes.getTranslationMap().keySet()) {
            map.put(type, SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get());
        }

        map.put(SoundType.WOOL, 1.5D);
        map.put(SoundType.MOSS, 0.75D);
        map.put(SoundType.HONEY_BLOCK, 0.5D);
        map.put(SoundType.GLASS, 0.1D);
        map.put(SoundType.SNOW, 0.1D);
        map.put(SoundType.POWDER_SNOW, 0.1D);
        map.put(SoundType.BAMBOO, 0.1D);
        map.put(SoundType.BAMBOO_SAPLING, 0.1D);
        map.put(SoundType.WET_GRASS, 0.1D);
        map.put(SoundType.MOSS_CARPET, 0.1D);
        map.put(SoundType.WEEPING_VINES, 0D);
        map.put(SoundType.TWISTING_VINES, 0D);
        map.put(SoundType.VINE, 0D);
        map.put(SoundType.SWEET_BERRY_BUSH, 0D);
        map.put(SoundType.SPORE_BLOSSOM, 0D);
        map.put(SoundType.SMALL_DRIPLEAF, 0D);
        map.put(SoundType.ROOTS, 0D);
        map.put(SoundType.POINTED_DRIPSTONE, 0D);
        map.put(SoundType.SCAFFOLDING, 0D);
        map.put(SoundType.GLOW_LICHEN, 0D);
        map.put(SoundType.CROP, 0D);
        map.put(SoundType.FUNGUS, 0D);
        map.put(SoundType.LILY_PAD, 0D);
        map.put(SoundType.LARGE_AMETHYST_BUD, 0D);
        map.put(SoundType.MEDIUM_AMETHYST_BUD, 0D);
        map.put(SoundType.SMALL_AMETHYST_BUD, 0D);
        map.put(SoundType.LADDER, 0D);
        map.put(SoundType.CHAIN, 0D);

        return map;
    }

}
