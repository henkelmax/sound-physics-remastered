package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockSoundConfigBase;
import net.minecraft.world.level.block.SoundType;

import java.nio.file.Path;
import java.util.Map;

public class ReflectivityConfig extends BlockSoundConfigBase {

    public ReflectivityConfig(Path path) {
        super(path);
    }

    @Override
    public void addDefaults(Map<BlockDefinition, Float> map) {
        for (SoundType type : SoundTypes.getTranslationMap().keySet()) {
            putSoundType(map, type, SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get());
        }

        putSoundType(map, SoundType.STONE, 1.5F);
        putSoundType(map, SoundType.NETHERITE_BLOCK, 1.5F);
        putSoundType(map, SoundType.TUFF, 1.5F);
        putSoundType(map, SoundType.AMETHYST, 1.5F);
        putSoundType(map, SoundType.BASALT, 1.5F);
        putSoundType(map, SoundType.CALCITE, 1.5F);
        putSoundType(map, SoundType.BONE_BLOCK, 1.5F);
        putSoundType(map, SoundType.COPPER, 1.25F);
        putSoundType(map, SoundType.DEEPSLATE, 1.5F);
        putSoundType(map, SoundType.DEEPSLATE_BRICKS, 1.5F);
        putSoundType(map, SoundType.DEEPSLATE_TILES, 1.5F);
        putSoundType(map, SoundType.POLISHED_DEEPSLATE, 1.5F);
        putSoundType(map, SoundType.NETHER_BRICKS, 1.5F);
        putSoundType(map, SoundType.NETHERRACK, 1.1F);
        putSoundType(map, SoundType.NETHER_GOLD_ORE, 1.1F);
        putSoundType(map, SoundType.NETHER_ORE, 1.1F);
        putSoundType(map, SoundType.STEM, 0.4F);
        putSoundType(map, SoundType.WOOL, 0.1F);
        putSoundType(map, SoundType.HONEY_BLOCK, 0.1F);
        putSoundType(map, SoundType.MOSS, 0.1F);
        putSoundType(map, SoundType.SOUL_SAND, 0.2F);
        putSoundType(map, SoundType.SOUL_SOIL, 0.2F);
        putSoundType(map, SoundType.CORAL_BLOCK, 0.2F);
        putSoundType(map, SoundType.METAL, 1.25F);
        putSoundType(map, SoundType.WOOD, 0.4F);
        putSoundType(map, SoundType.GRAVEL, 0.3F);
        putSoundType(map, SoundType.GRASS, 0.3F);
        putSoundType(map, SoundType.GLASS, 0.75F);
        putSoundType(map, SoundType.SAND, 0.2F);
        putSoundType(map, SoundType.SNOW, 0.15F);
    }

    @Override
    public Float getDefaultValue() {
        return SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get();
    }
}
