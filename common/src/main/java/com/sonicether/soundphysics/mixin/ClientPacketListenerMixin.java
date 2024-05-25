package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.utils.LevelAccessUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow
    private ClientLevel level;

    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void handleLogin(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci) {
        LevelAccessUtils.onLoadLevel(level);
    }

}
