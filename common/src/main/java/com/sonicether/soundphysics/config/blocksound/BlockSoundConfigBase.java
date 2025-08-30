package com.sonicether.soundphysics.config.blocksound;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import javax.annotation.Nullable;

import com.sonicether.soundphysics.Loggers;

import com.sonicether.soundphysics.config.ConfigUtils;
import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockSoundConfigBase extends CommentedPropertyConfig {

    private Map<BlockDefinition, Float> configMap;

    @Nullable
    private Map<TagKey<Block>, Float> blockTagCache;
    @Nullable
    private Map<Block, Float> blockCache;
    @Nullable
    private Map<SoundType, Float> soundTypeCache;

    public BlockSoundConfigBase(Path path) {
        super(new CommentedProperties(false));
        this.path = path;
        reload();
    }

    @Override
    public void load() throws IOException {
        Map<BlockDefinition, Float> map = new HashMap<>();
        addDefaults(map);

        super.load();

        for (String key : properties.keySet()) {
            String valueString = properties.get(key);
            if (valueString == null) {
                continue;
            }
            float value;
            try {
                value = Float.parseFloat(valueString);
            } catch (NumberFormatException e) {
                Loggers.warn("Failed to parse value of {}", key);
                continue;
            }
            BlockDefinition blockDefinition = loadBlockDefinition(key);
            if (blockDefinition == null) {
                Loggers.warn("Block definition {} not found", key);
                continue;
            }

            map.put(blockDefinition, value);
        }
        configMap = ConfigUtils.sortMap(map);
        invalidateCaches();
        saveSync();
    }

    public static BlockDefinition loadBlockDefinition(String configString) {
        BlockDefinition blockDefinition = BlockTagDefinition.fromConfigString(configString);
        if (blockDefinition != null) {
            return blockDefinition;
        }
        blockDefinition = BlockIdDefinition.fromConfigString(configString);
        if (blockDefinition != null) {
            return blockDefinition;
        }
        blockDefinition = BlockSoundTypeDefinition.fromConfigString(configString);
        return blockDefinition;
    }

    @Override
    public void saveSync() {
        properties.clear();

        properties.setHeaderComments(List.of(
                "Values for blocks can be defined as follows:",
                "",
                "By sound type:",
                "WOOD=1.0",
                "",
                "By block tag:",
                "\\#minecraft\\:logs=1.0",
                "",
                "By block ID:",
                "minecraft\\:oak_log=1.0"
        ));

        for (Map.Entry<BlockDefinition, Float> entry : configMap.entrySet()) {
            String configKey = entry.getKey().getConfigString();
            properties.set(configKey, String.valueOf(entry.getValue()));
            String configComment = entry.getKey().getConfigComment();
            if (configComment != null) {
                properties.setComments(configKey, Collections.singletonList(configComment));
            } else {
                properties.setComments(configKey, Collections.emptyList());
            }
        }

        super.saveSync();
    }

    public Map<BlockDefinition, Float> getBlockDefinitions() {
        return Collections.unmodifiableMap(configMap);
    }

    public float getBlockDefinitionValue(BlockState blockState) {
        Float value = getBlocks().get(blockState.getBlock());
        if (value != null) {
            return value;
        }
        for (Map.Entry<TagKey<Block>, Float> entry : getBlockTags().entrySet()) {
            if (isTagIn(entry.getKey(), blockState.getBlock())) {
                return entry.getValue();
            }
        }
        value = getSoundTypes().get(blockState.getSoundType());
        if (value != null) {
            return value;
        }
        return getDefaultValue();
    }

    public static <T> boolean isTagIn(TagKey<T> tagKey, T entry) {
        Optional<? extends Registry<?>> registryOptional = BuiltInRegistries.REGISTRY.getOptional(tagKey.registry().location());
        if (registryOptional.isPresent()) {
            if (tagKey.isFor(registryOptional.get().key())) {
                Registry<T> registry = (Registry<T>) registryOptional.get();
                Optional<ResourceKey<T>> maybeKey = registry.getResourceKey(entry);
                if (maybeKey.isPresent()) {
                    return registry.getHolderOrThrow(maybeKey.get()).is(tagKey);
                }
            }
        }
        return false;
    }

    private void invalidateCaches() {
        blockTagCache = null;
        blockCache = null;
        soundTypeCache = null;
    }

    private Map<TagKey<Block>, Float> getBlockTags() {
        if (blockTagCache == null) {
            blockTagCache = new LinkedHashMap<>();
            for (Map.Entry<BlockDefinition, Float> entry : configMap.entrySet()) {
                if (entry.getKey() instanceof BlockTagDefinition def) {
                    blockTagCache.put(def.getBlockTag(), entry.getValue());
                }
            }
        }
        return blockTagCache;
    }

    private Map<Block, Float> getBlocks() {
        if (blockCache == null) {
            blockCache = new LinkedHashMap<>();
            for (Map.Entry<BlockDefinition, Float> entry : configMap.entrySet()) {
                if (entry.getKey() instanceof BlockIdDefinition def) {
                    blockCache.put(def.getBlock(), entry.getValue());
                }
            }
        }
        return blockCache;
    }

    private Map<SoundType, Float> getSoundTypes() {
        if (soundTypeCache == null) {
            soundTypeCache = new LinkedHashMap<>();
            for (Map.Entry<BlockDefinition, Float> entry : configMap.entrySet()) {
                if (entry.getKey() instanceof BlockSoundTypeDefinition def) {
                    soundTypeCache.put(def.getSoundType(), entry.getValue());
                }
            }
        }
        return soundTypeCache;
    }

    public BlockSoundConfigBase setBlockDefinitionValue(BlockDefinition blockDefinition, float value) {
        configMap.put(blockDefinition, value);
        invalidateCaches();
        return this;
    }

    public abstract void addDefaults(Map<BlockDefinition, Float> map);

    public abstract Float getDefaultValue();

    protected static void putSoundType(Map<BlockDefinition, Float> map, SoundType soundType, float value) {
        map.put(new BlockSoundTypeDefinition(soundType), value);
    }

}
