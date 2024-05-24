package com.sonicether.soundphysics.utils;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.world.CachingClientLevel;
import com.sonicether.soundphysics.world.ClientLevelProxy;
import com.sonicether.soundphysics.world.ClonedClientLevel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/**
 * Utility module to manage creation, invalidation, and updating of client level clones.
 * 
 * Level clones are created on a client tick basis and retained for some time.
 * Any module on any thread may access the cached level clone for read-only world operations.
 * 
 * @author Saint (@augustsaintfreytag)
 */
public class LevelAccessUtils {

    // Configuration

    private static final boolean USE_UNSAFE_LEVEL_ACCESS = false;           // Disable level clone and cache and fall back to original main thread access. (Default: false)
    private static final int LEVEL_CLONE_RANGE = 4;                         // Cloned number of chunks in radius around player position. (Default: 4 chunks)
    private static final long LEVEL_CLONE_MAX_RETAIN_TICKS = 20;            // Maximum number of ticks to retain level clone in cache. (Default: 20 ticks / 1 second)
    private static final long LEVEL_CLONE_MAX_RETAIN_BLOCK_DISTANCE = 16;   // Maximum distance player can move from cloned origin before invalidation. (Default: 25% clone radius)

    // Cache Write

    public static void tickLevelCache(ClientLevel clientLevel, Player player) {
        if (USE_UNSAFE_LEVEL_ACCESS) {
            // Disable all level cloning, use direct unsafe main thread access (original behavior).
            return;
        }

        var currentTick = clientLevel.getGameTime();
        var origin = levelOriginFromPlayer(player);
        
        // Cast client level reference to interface to access injected level cache property.
        var cachingClientLevel = (CachingClientLevel) (Object) clientLevel;
        var clientLevelClone = cachingClientLevel.getCachedClone();
        
        if (clientLevelClone == null) {
            // No cache exists, cache first level clone.

            Loggers.LOGGER.info("Creating new level cache, no existing level clone found in client cache.");
            updateLevelCache(clientLevel, origin, LEVEL_CLONE_MAX_RETAIN_TICKS);
            return;
        }

        var ticksSinceLastClone = currentTick - clientLevelClone.getTick();
        var distanceSinceLastClone = origin.distSqr(clientLevelClone.getOrigin());

        if (ticksSinceLastClone >= LEVEL_CLONE_MAX_RETAIN_TICKS || distanceSinceLastClone >= LEVEL_CLONE_MAX_RETAIN_BLOCK_DISTANCE) {
            // Cache expired or player travelled too far from last clone origin point, update cache.
            
            Loggers.LOGGER.info("Updating level cache, cache expired ({} ticks) or player moved too far ({} block(s)) from last clone origin.", ticksSinceLastClone, distanceSinceLastClone);
            updateLevelCache(clientLevel, origin, currentTick);
        } else {
            Loggers.LOGGER.info("Retaining level cache, cache still valid ({} ticks) and player within range ({} block(s)) from last clone origin.", ticksSinceLastClone, distanceSinceLastClone);
        }
    }

    private static void updateLevelCache(ClientLevel clientLevel, BlockPos origin, long tick) {
        Loggers.LOGGER.info("Updating level cache, creating new level clone with origin {} on tick {}.", origin.toString(), tick);

        var cachingClientLevel = (CachingClientLevel) (Object) clientLevel;
        var clientLevelClone = new ClonedClientLevel(clientLevel, origin, tick, LEVEL_CLONE_RANGE);

        cachingClientLevel.setCachedClone(clientLevelClone);
    }

    // Cache Read

    public static ClientLevelProxy getClientLevelProxy(Minecraft client) {
        var clientLevel = client.level;

        if (clientLevel == null) {
            Loggers.LOGGER.warn("Can not return client level proxy, client level does not exist.");
            return null;
        }

        if (USE_UNSAFE_LEVEL_ACCESS) {
            return (ClientLevelProxy) clientLevel;
        }

        var cachingClientLevel = (CachingClientLevel) (Object) clientLevel;
        var clientLevelClone = cachingClientLevel.getCachedClone();

        if (clientLevelClone == null) {
            Loggers.LOGGER.info("Can not return client level proxy, client level clone has not been cached.");
            return null;
        }

        Loggers.LOGGER.info("Returning client level proxy from cache.");
        return clientLevelClone;
    }

    // Utilities

    private static BlockPos levelOriginFromPlayer(Player player) {
        var playerPos = player.position();
        return new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
    }

}
