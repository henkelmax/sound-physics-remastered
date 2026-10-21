package com.sonicether.soundphysics.integration.voicechat;

import com.sonicether.soundphysics.SoundPhysics;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class AudioChannel {

    private static final String VOICECHAT = "voicechat";
    private static final Identifier VOICECHAT_SOUND = Identifier.fromNamespaceAndPath(VOICECHAT, VOICECHAT);

    private final UUID channelId;
    private long lastUpdate;
    private Vec3 lastPos;

    public AudioChannel(UUID channelId) {
        this.channelId = channelId;
    }

    public void onSound(int source, @Nullable Vec3 soundPos, boolean auxOnly, @Nullable String category) {
        float directGainScale = getDirectGainScale(auxOnly);

        if (soundPos == null) {
            SoundPhysics.setDefaultEnvironment(source, directGainScale);
            return;
        }

        long time = System.currentTimeMillis();

        if (time - lastUpdate < 500 && (lastPos != null && lastPos.distanceTo(soundPos) < 1D)) {
            return;
        }

        Identifier sound = category == null ? VOICECHAT_SOUND : Identifier.fromNamespaceAndPath(VOICECHAT, category);
        SoundPhysics.processSound(source, soundPos.x(), soundPos.y(), soundPos.z(), SoundSource.MASTER, sound, directGainScale);

        lastUpdate = time;
        lastPos = soundPos;
    }

    /**
     * The reference distance workaround doubles the gain of the direct sound.
     * Minecraft caps it with the default <code>AL_MAX_GAIN</code> of 1, but voice chat doesn't, so it needs to be compensated.
     */
    private static float getDirectGainScale(boolean auxOnly) {
        if (auxOnly) {
            return 0F;
        }
        return SoundPhysics.hasReferenceDistanceBug() ? 0.5F : 1F;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public boolean canBeRemoved() {
        return System.currentTimeMillis() - lastUpdate > 5_000L;
    }

    public static boolean isVoicechatSound(Identifier sound) {
        return sound.getNamespace().equals(VOICECHAT);
    }

}
