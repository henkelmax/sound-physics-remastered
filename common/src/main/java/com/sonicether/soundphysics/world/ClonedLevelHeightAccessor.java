package com.sonicether.soundphysics.world;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;

/**
 * Read-only sparse clone of a client level height accessor.
 *
 * @author Saint (@augustsaintfreytag)
 */
public class ClonedLevelHeightAccessor implements LevelHeightAccessor {

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