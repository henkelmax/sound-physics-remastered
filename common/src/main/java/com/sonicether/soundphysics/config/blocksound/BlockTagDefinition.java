package com.sonicether.soundphysics.config.blocksound;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockTagDefinition extends BlockDefinition {

    private final TagKey<Block> blockTag;

    public BlockTagDefinition(TagKey<Block> blockTag) {
        this.blockTag = blockTag;
    }

    @Override
    public String getConfigString() {
        return "#%s".formatted(blockTag.location());
    }

    @Override
    @Nullable
    public String getConfigComment() {
        return getName().getString();
    }

    @Override
    public Component getName() {
        return Component.literal(getConfigString()).append(Component.literal(" (Block Tag)"));
    }

    public TagKey<Block> getBlockTag() {
        return blockTag;
    }

    @Nullable
    public static BlockTagDefinition fromConfigString(String configString) {
        if (!configString.startsWith("#")) {
            return null;
        }
        String id = configString.substring(1).trim();
        if (!ResourceLocation.isValidResourceLocation(id)) {
            return null;
        }
        return new BlockTagDefinition(TagKey.create(Registries.BLOCK, new ResourceLocation(id)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockTagDefinition that = (BlockTagDefinition) o;
        return Objects.equals(blockTag, that.blockTag);
    }

    @Override
    public int hashCode() {
        return blockTag != null ? blockTag.hashCode() : 0;
    }
}
