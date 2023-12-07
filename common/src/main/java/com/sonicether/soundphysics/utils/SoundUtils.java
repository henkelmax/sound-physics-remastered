package com.sonicether.soundphysics.utils;

import net.minecraft.sounds.SoundEvent;

import java.util.regex.Pattern;

public class SoundUtils {

    private static final Pattern STEP_PATTERN = Pattern.compile(".*step.*");

    public static double calculateEntitySoundYOffset(float standingEyeHeight, SoundEvent sound) {
        if (STEP_PATTERN.matcher(sound.getLocation().getPath()).matches()) {
            return 0D;
        }
        return standingEyeHeight;
    }

}
