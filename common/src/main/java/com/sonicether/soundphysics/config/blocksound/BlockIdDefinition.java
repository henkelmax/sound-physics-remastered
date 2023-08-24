package com.sonicether.soundphysics.config.blocksound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockIdDefinition extends BlockDefinition {

    private final Block block;

    public BlockIdDefinition(Block block) {
        this.block = block;
    }

    @Override
    public String getConfigString() {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    @Override
    @Nullable
    public String getConfigComment() {
        return getName().getString();
    }

    @Override
    public Component getName() {
        return block.getName().append(Component.literal(" (Block)"));
    }

    public Block getBlock() {
        return block;
    }

    @Nullable
    public static BlockIdDefinition fromConfigString(String configString) {
        if (!configString.contains(":")) {
            return null;
        }
        if (!ResourceLocation.isValidResourceLocation(configString)) {
            return null;
        }
        return new BlockIdDefinition(BuiltInRegistries.BLOCK.get(new ResourceLocation(configString)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockIdDefinition that = (BlockIdDefinition) o;
        return Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return block != null ? block.hashCode() : 0;
    }
}
