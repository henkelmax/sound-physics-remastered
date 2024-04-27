package com.sonicether.soundphysics.models;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;

final class ClonedLevelHeightAccessor implements LevelHeightAccessor {

    private final int height;
    private final int minBuildHeight;

    public ClonedLevelHeightAccessor(Level level) {
        this.height = level.getHeight();
        this.minBuildHeight = level.getMinBuildHeight();
    }

    public int getHeight() {
        return this.height;
    }

    public int getMinBuildHeight() {
        return this.minBuildHeight;
    }
    
}