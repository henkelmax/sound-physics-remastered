package com.sonicether.soundphysics.world;

public interface CachingClientLevel {

    ClonedClientLevel getCachedClone();

    void setCachedClone(ClonedClientLevel cachedClone);

}
