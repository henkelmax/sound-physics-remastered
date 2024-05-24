package com.sonicether.soundphysics.utils;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.profiling.TaskProfiler;
import com.sonicether.soundphysics.world.CachingClientLevel;
import com.sonicether.soundphysics.world.ClientLevelProxy;
import com.sonicether.soundphysics.world.ClonedClientLevel;
import com.sonicether.soundphysics.world.UnsafeClientLevel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/**
 * Utility module to manage creation, invalidation, and updating of client level clones.
 * <p>
 * Level clones are created on a client tick basis and retained for some time.
 * Any module on any thread may access the cached level clone for read-only world operations.
 *
 * @author Saint (@augustsaintfreytag)
 */
public class LevelAccessUtils {

    private static final TaskProfiler profiler = new TaskProfiler("Level Caching");

    // Cache Write

    public static void tickLevelCache(ClientLevel clientLevel, Player player) {
        if (SoundPhysicsMod.CONFIG.unsafeLevelAccess.get()) {
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

            Loggers.logDebug("Creating new level cache, no existing level clone found in client cache.");
            updateLevelCache(clientLevel, origin, SoundPhysicsMod.CONFIG.levelCloneMaxRetainTicks.get());
            return;
        }

        var ticksSinceLastClone = currentTick - clientLevelClone.getTick();
        var distanceSinceLastClone = origin.distSqr(clientLevelClone.getOrigin());

        if (ticksSinceLastClone >= SoundPhysicsMod.CONFIG.levelCloneMaxRetainTicks.get() || distanceSinceLastClone >= SoundPhysicsMod.CONFIG.levelCloneMaxRetainBlockDistance.get()) {
            // Cache expired or player travelled too far from last clone origin point, update cache.

            Loggers.logDebug(
                    "Updating level cache, cache expired ({}/{} ticks) or player moved too far ({}/{} block(s)) from last clone origin.",
                    ticksSinceLastClone, SoundPhysicsMod.CONFIG.levelCloneMaxRetainTicks.get(), distanceSinceLastClone, SoundPhysicsMod.CONFIG.levelCloneMaxRetainBlockDistance.get()
            );

            updateLevelCache(clientLevel, origin, currentTick);
        }
    }

    private static void updateLevelCache(ClientLevel clientLevel, BlockPos origin, long tick) {
        Loggers.logDebug("Updating level cache, creating new level clone with origin {} on tick {}.", origin.toShortString(), tick);

        var profile = profiler.profile();
        var cachingClientLevel = (CachingClientLevel) (Object) clientLevel;
        var clientLevelClone = new ClonedClientLevel(clientLevel, origin, tick, SoundPhysicsMod.CONFIG.levelCloneRange.get());

        cachingClientLevel.setCachedClone(clientLevelClone);

        profile.finish();

        Loggers.logProfiling("Updated client level clone in cache in {} ms", profile.getDuration());
        profiler.onTally(() -> profiler.logResults());
    }

    // Cache Read

    public static ClientLevelProxy getClientLevelProxy(Minecraft client) {
        var clientLevel = client.level;

        if (clientLevel == null) {
            Loggers.warn("Can not return client level proxy, client level does not exist.");
            return null;
        }

        if (SoundPhysicsMod.CONFIG.unsafeLevelAccess.get()) {
            return new UnsafeClientLevel(clientLevel);
        }

        var cachingClientLevel = (CachingClientLevel) (Object) clientLevel;
        var clientLevelClone = cachingClientLevel.getCachedClone();

        if (clientLevelClone == null) {
            Loggers.warn("Can not return client level proxy, client level clone has not been cached. This might only occur once on load.");
            return null;
        }

        return clientLevelClone;
    }

    // Utilities

    private static BlockPos levelOriginFromPlayer(Player player) {
        var playerPos = player.position();
        return new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
    }

}
