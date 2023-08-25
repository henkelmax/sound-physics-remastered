package com.sonicether.soundphysics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL11;

public class Loggers {

    private static final String LOG_PREFIX = "Sound Physics - %s";
    public static final Logger DEBUG_LOGGER = LogManager.getLogger(String.format(LOG_PREFIX, "Debug"));
    public static final Logger ENVIRONMENT_LOGGER = LogManager.getLogger(String.format(LOG_PREFIX, "Environment"));
    public static final Logger OCCLUSION_LOGGER = LogManager.getLogger(String.format(LOG_PREFIX, "Occlusion"));
    public static final Logger LOGGER = LogManager.getLogger(String.format(LOG_PREFIX, "General"));


    protected static void logOcclusion(String message, Object... args) {
        if (!SoundPhysicsMod.CONFIG.occlusionLogging.get()) {
            return;
        }
        OCCLUSION_LOGGER.info(message, args);
    }

    protected static void logEnvironment(String message, Object... args) {
        if (!SoundPhysicsMod.CONFIG.environmentLogging.get()) {
            return;
        }
        ENVIRONMENT_LOGGER.info(message, args);
    }

    protected static void logDebug(String message, Object... args) {
        if (!SoundPhysicsMod.CONFIG.debugLogging.get()) {
            return;
        }
        DEBUG_LOGGER.info(message, args);
    }

    public static void logALError(String errorMessage) {
        int error = AL11.alGetError();
        if (error == AL11.AL_NO_ERROR) {
            return;
        }

        String errorName = switch (error) {
            case AL11.AL_INVALID_NAME -> "AL_INVALID_NAME";
            case AL11.AL_INVALID_ENUM -> "AL_INVALID_ENUM";
            case AL11.AL_INVALID_VALUE -> "AL_INVALID_VALUE";
            case AL11.AL_INVALID_OPERATION -> "AL_INVALID_OPERATION";
            case AL11.AL_OUT_OF_MEMORY -> "AL_OUT_OF_MEMORY";
            default -> Integer.toString(error);
        };

        LOGGER.error("{}: OpenAL error {}", errorMessage, errorName);
    }

}
