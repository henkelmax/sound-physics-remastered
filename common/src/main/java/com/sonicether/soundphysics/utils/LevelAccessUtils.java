package com.sonicether.soundphysics.utils;

import java.util.concurrent.atomic.AtomicReference;

import com.sonicether.soundphysics.mixin.ClientLevelMixin;
import com.sonicether.soundphysics.world.ClientLevelProxy;
import com.sonicether.soundphysics.world.ClonedClientLevel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class LevelAccessUtils {

    // Configuration

    private static final boolean USE_UNSAFE_LEVEL_ACCESS = false;   // Disable level clone and cache and fall back to original main thread access. (Default: false)
    private static final int LEVEL_CLONE_RANGE = 4;                 // Cloned number of chunks in radius around player position. (Default: 4 chunks)
    private static final long LEVEL_CLONE_MAX_RETAIN_TICKS = 20;    // Maximum number of ticks to retain level clone in cache. (Default: 20 ticks / 1 second)
    private static final long LEVEL_CLONE_MAX_RETAIN_BLOCK_DISTANCE = (LEVEL_CLONE_RANGE * 16) / 4; // Maximum distance player can move from cloned origin before invalidation. (Default: 25% clone radius)

    // Cache Write

    public static void tickLevelCache(ClientLevel clientLevel, Player player) {
        if (USE_UNSAFE_LEVEL_ACCESS) {
            // Disable all level cloning, let sound physics use direct unsafe main thread access (original behavior).
            return;
        }
        
        // Cast client level reference to mixin to access injected level cache property.
        var clientLevelMixin = (ClientLevelMixin) (Object) clientLevel;

        var currentTick = clientLevel.getGameTime();
        var origin = levelOriginFromPlayer(player);
        
        if (clientLevelMixin.cachedClone == null) {
            // No cache exists, cache first level clone.
            updateLevelCache(clientLevel, origin, LEVEL_CLONE_MAX_RETAIN_TICKS);
            return;
        }

        var lastClonedLevel = clientLevelMixin.cachedClone.get();
        var ticksSinceLastClone = currentTick - lastClonedLevel.getTick();
        var distanceSinceLastClone = origin.distSqr(lastClonedLevel.getOrigin());

        if (ticksSinceLastClone >= LEVEL_CLONE_MAX_RETAIN_TICKS || distanceSinceLastClone >= LEVEL_CLONE_MAX_RETAIN_BLOCK_DISTANCE) {
            // Cache expired or player travelled too far from last clone origin point, update cache.
            updateLevelCache(clientLevel, origin, currentTick);
        }
    }

    private static void updateLevelCache(ClientLevel clientLevel, BlockPos origin, long tick) {
        var clientLevelMixin = (ClientLevelMixin) (Object) clientLevel;
        var clonedLevel = new ClonedClientLevel(clientLevel, origin, tick, LEVEL_CLONE_RANGE);

        clientLevelMixin.cachedClone = new AtomicReference<ClonedClientLevel>(clonedLevel);
    }

    // Cache Read

    public static ClientLevelProxy getClientLevelProxy(Minecraft client) {
        var clientLevel = client.level;

        if (clientLevel == null) {
            return null;
        }

        if (USE_UNSAFE_LEVEL_ACCESS) {
            return (ClientLevelProxy) clientLevel;
        }

        var clientLevelMixin = (ClientLevelMixin) (Object) clientLevel;

        if (clientLevelMixin.cachedClone == null) {
            return null;
        }

        return clientLevelMixin.cachedClone.get();
    }

    // Utilities

    private static BlockPos levelOriginFromPlayer(Player player) {
        var playerPos = player.position();
        return new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
    }

}
