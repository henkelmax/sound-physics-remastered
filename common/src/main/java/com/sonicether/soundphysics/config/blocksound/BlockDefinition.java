package com.sonicether.soundphysics.config.blocksound;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class BlockDefinition implements Comparable<BlockDefinition> {

    public abstract String getConfigString();

    @Nullable
    public abstract String getConfigComment();

    public abstract Component getName();

    @Override
    public int compareTo(@NotNull BlockDefinition o) {
        return getConfigString().compareTo(o.getConfigString());
    }
}
