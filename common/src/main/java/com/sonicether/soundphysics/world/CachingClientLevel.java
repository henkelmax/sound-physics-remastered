package com.sonicether.soundphysics.world;

import javax.annotation.Nullable;

public interface CachingClientLevel {

    @Nullable
    ClonedClientLevel sound_physics_remastered$getCachedClone();

    void sound_physics_remastered$setCachedClone(@Nullable ClonedClientLevel cachedClone);

}
