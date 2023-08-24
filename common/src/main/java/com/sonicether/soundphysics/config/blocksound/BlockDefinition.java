package com.sonicether.soundphysics.config.blocksound;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public abstract class BlockDefinition {

    public abstract String getConfigString();

    @Nullable
    public abstract String getConfigComment();

    public abstract Component getName();

}
