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

public class OcclusionConfig extends CommentedPropertyConfig {

    private Map<SoundType, Float> occlusion;

    public OcclusionConfig(Path path) {
        super(new CommentedProperties(false));
        this.path = path;
        reload();
    }

    @Override
    public void load() throws IOException {
        super.load();

        occlusion = createDefaultMap();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            float value;
            try {
                value = Float.parseFloat(entry.getValue());
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
        saveSync();
    }

    @Override
    public void saveSync() {
        properties.clear();

        for (Map.Entry<SoundType, Float> entry : occlusion.entrySet()) {
            properties.set(SoundTypes.getName(entry.getKey()), String.valueOf(entry.getValue()));
        }

        super.saveSync();
    }

    public Map<SoundType, Float> getOcclusionFactors() {
        return occlusion;
    }

    public double getOcclusionFactor(SoundType soundType) {
        return occlusion.getOrDefault(soundType, SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get());
    }

    public OcclusionConfig setOcclusionFactor(SoundType soundType, float value) {
        occlusion.put(soundType, value);
        return this;
    }

    public Map<SoundType, Float> createDefaultMap() {
        Map<SoundType, Float> map = new HashMap<>();
        for (SoundType type : SoundTypes.getTranslationMap().keySet()) {
            map.put(type, SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get());
        }

        map.put(SoundType.WOOL, 1.5F);
        map.put(SoundType.MOSS, 0.75F);
        map.put(SoundType.HONEY_BLOCK, 0.5F);
        map.put(SoundType.GLASS, 0.1F);
        map.put(SoundType.SNOW, 0.1F);
        map.put(SoundType.POWDER_SNOW, 0.1F);
        map.put(SoundType.BAMBOO, 0.1F);
        map.put(SoundType.BAMBOO_SAPLING, 0.1F);
        map.put(SoundType.WET_GRASS, 0.1F);
        map.put(SoundType.MOSS_CARPET, 0.1F);
        map.put(SoundType.WEEPING_VINES, 0F);
        map.put(SoundType.TWISTING_VINES, 0F);
        map.put(SoundType.VINE, 0F);
        map.put(SoundType.SWEET_BERRY_BUSH, 0F);
        map.put(SoundType.SPORE_BLOSSOM, 0F);
        map.put(SoundType.SMALL_DRIPLEAF, 0F);
        map.put(SoundType.ROOTS, 0F);
        map.put(SoundType.POINTED_DRIPSTONE, 0F);
        map.put(SoundType.SCAFFOLDING, 0F);
        map.put(SoundType.GLOW_LICHEN, 0F);
        map.put(SoundType.CROP, 0F);
        map.put(SoundType.FUNGUS, 0F);
        map.put(SoundType.LILY_PAD, 0F);
        map.put(SoundType.LARGE_AMETHYST_BUD, 0F);
        map.put(SoundType.MEDIUM_AMETHYST_BUD, 0F);
        map.put(SoundType.SMALL_AMETHYST_BUD, 0F);
        map.put(SoundType.LADDER, 0F);
        map.put(SoundType.CHAIN, 0F);

        return map;
    }

}
