package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.utils.LevelAccessUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void setLevel(ClientLevel clientLevel, CallbackInfo ci) {
        if (level != null) {
            LevelAccessUtils.onUnloadLevel(level);
        }
        if (player != null) {
            LevelAccessUtils.onLoadLevel(clientLevel);
        }
    }

    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    private void disconnect(Screen screen, CallbackInfo ci) {
        if (level != null) {
            LevelAccessUtils.onUnloadLevel(level);
        }
    }

}
