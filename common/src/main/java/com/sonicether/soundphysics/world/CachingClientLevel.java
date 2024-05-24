package com.sonicether.soundphysics.world;

public interface CachingClientLevel {

    public ClonedClientLevel getCachedClone();

    public void setCachedClone(ClonedClientLevel cachedClone);

}
