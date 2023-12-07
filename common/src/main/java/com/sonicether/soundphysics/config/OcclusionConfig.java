package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockIdDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockSoundConfigBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

import java.nio.file.Path;
import java.util.Map;

public class OcclusionConfig extends BlockSoundConfigBase {

    public OcclusionConfig(Path path) {
        super(path);
    }

    @Override
    public void addDefaults(Map<BlockDefinition, Float> map) {
        for (SoundType type : SoundTypes.getTranslationMap().keySet()) {
            putSoundType(map, type, SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get());
        }

        putSoundType(map, SoundType.WOOL, 1.5F);
        putSoundType(map, SoundType.MOSS, 0.75F);
        putSoundType(map, SoundType.HONEY_BLOCK, 0.5F);
        putSoundType(map, SoundType.GLASS, 0.1F);
        putSoundType(map, SoundType.SNOW, 0.1F);
        putSoundType(map, SoundType.POWDER_SNOW, 0.1F);
        putSoundType(map, SoundType.BAMBOO, 0.1F);
        putSoundType(map, SoundType.BAMBOO_SAPLING, 0.1F);
        putSoundType(map, SoundType.WET_GRASS, 0.1F);
        putSoundType(map, SoundType.MOSS_CARPET, 0.1F);
        putSoundType(map, SoundType.WEEPING_VINES, 0F);
        putSoundType(map, SoundType.TWISTING_VINES, 0F);
        putSoundType(map, SoundType.VINE, 0F);
        putSoundType(map, SoundType.SWEET_BERRY_BUSH, 0F);
        putSoundType(map, SoundType.SPORE_BLOSSOM, 0F);
        putSoundType(map, SoundType.SMALL_DRIPLEAF, 0F);
        putSoundType(map, SoundType.ROOTS, 0F);
        putSoundType(map, SoundType.POINTED_DRIPSTONE, 0F);
        putSoundType(map, SoundType.SCAFFOLDING, 0F);
        putSoundType(map, SoundType.GLOW_LICHEN, 0F);
        putSoundType(map, SoundType.CROP, 0F);
        putSoundType(map, SoundType.FUNGUS, 0F);
        putSoundType(map, SoundType.LILY_PAD, 0F);
        putSoundType(map, SoundType.LARGE_AMETHYST_BUD, 0F);
        putSoundType(map, SoundType.MEDIUM_AMETHYST_BUD, 0F);
        putSoundType(map, SoundType.SMALL_AMETHYST_BUD, 0F);
        putSoundType(map, SoundType.LADDER, 0F);
        putSoundType(map, SoundType.CHAIN, 0F);

        map.put(new BlockIdDefinition(Blocks.WATER), 0.25F);
        map.put(new BlockIdDefinition(Blocks.LAVA), 0.75F);
        map.put(new BlockIdDefinition(Blocks.JUKEBOX), 0F);
    }

    @Override
    public Float getDefaultValue() {
        return SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get();
    }
}
