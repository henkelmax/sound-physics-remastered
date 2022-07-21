package com.sonicether.soundphysics.integration.voicechat;

import com.sonicether.soundphysics.SoundPhysics;
import com.sonicether.soundphysics.SoundPhysicsMod;
import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.audiochannel.ClientLocationalAudioChannel;
import de.maxhenkel.voicechat.api.events.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.EXTThreadLocalContext;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ForgeVoicechatPlugin
public class SimpleVoiceChatPlugin implements VoicechatPlugin {

    private final Map<UUID, AudioChannel> audioChannels;
    private ClientLocationalAudioChannel locationalAudioChannel;

    public SimpleVoiceChatPlugin() {
        audioChannels = new HashMap<>();
    }

    @Override
    public String getPluginId() {
        return SoundPhysicsMod.MODID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        SoundPhysics.LOGGER.info("Initializing Simple Voice Chat integration");
        audioChannels.clear();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(CreateOpenALContextEvent.class, this::onCreateALContext);
        registration.registerEvent(OpenALSoundEvent.class, this::onOpenALSound);
        registration.registerEvent(ClientVoicechatConnectionEvent.class, this::onConnection);
        registration.registerEvent(ClientSoundEvent.class, this::onClientSound);
    }

    private void onClientSound(ClientSoundEvent event) {
        if (locationalAudioChannel == null) {
            return;
        }
        if (!SoundPhysicsMod.CONFIG.hearSelf.get()) {
            return;
        }
        Vec3 position = Minecraft.getInstance().player.position();
        locationalAudioChannel.setLocation(event.getVoicechat().createPosition(position.x, position.y, position.z));
        locationalAudioChannel.play(event.getRawAudio());
    }

    private void onCreateALContext(CreateOpenALContextEvent event) {
        long oldContext = EXTThreadLocalContext.alcGetThreadContext();
        EXTThreadLocalContext.alcSetThreadContext(event.getContext());

        SoundPhysics.LOGGER.info("Initializing sound physics for voice chat audio");
        SoundPhysics.init();

        EXTThreadLocalContext.alcSetThreadContext(oldContext);
    }

    private void onConnection(ClientVoicechatConnectionEvent event) {
        SoundPhysics.DEBUG_LOGGER.info("Clearing unused audio channels");
        audioChannels.values().removeIf(AudioChannel::canBeRemoved);
        locationalAudioChannel = event.getVoicechat().createLocationalAudioChannel(UUID.randomUUID(), event.getVoicechat().createPosition(0D, 0D, 0D));
    }

    private void onOpenALSound(OpenALSoundEvent event) {
        if (!SoundPhysicsMod.CONFIG.simpleVoiceChatIntegration.get()) {
            return;
        }

        @Nullable Position position = event.getPosition();
        @Nullable UUID channelId = event.getChannelId();

        if (channelId == null) {
            return;
        }

        @Nullable AudioChannel audioChannel = audioChannels.get(channelId);

        if (audioChannel == null) {
            audioChannel = new AudioChannel(channelId);
            audioChannels.put(channelId, audioChannel);
        }

        audioChannel.onSound(event.getSource(), position == null ? null : new Vec3(position.getX(), position.getY(), position.getZ()));
    }

}
