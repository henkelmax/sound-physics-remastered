package com.sonicether.soundphysics.utils;

import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoundRateCounter {

    // Static map to store sound counts, shared across the application
    private static final Map<ResourceLocation, Integer> soundCounts = new ConcurrentHashMap<>();

    private SoundRateCounter() {
    }

    /**
     * @param sound the sound to increment
     * @return if the sound rate is above the limit
     */
    public static boolean incrementAndCheckLimit(ResourceLocation sound) {
        int count = soundCounts.getOrDefault(sound, 0);
        int max = SoundPhysicsMod.SOUND_RATE_CONFIG.getMaxCount(sound);
        if (count >= max) {
            return true;
        }
        soundCounts.put(sound, count + 1);
        return false;
    }

    public static void onClientTick(ClientLevel level) {
        // Clear all sound counts every second
        if (level.getGameTime() % 20 == 0) {
            clear();
        }
    }

    public static void clear() {
        soundCounts.clear();
    }

}
