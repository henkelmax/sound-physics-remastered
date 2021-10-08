package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.SoundPhysics;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SoundEngine.class)
public class SoundSystemMixin {

    @Inject(method = "loadLibrary", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Listener;reset()V"))
    private void loadLibrary(CallbackInfo ci) {
        SoundPhysics.init();
    }

    @Inject(method = "play", at = @At(value = "FIELD", target = "Lnet/minecraft/client/sounds/SoundEngine;instanceBySource:Lcom/google/common/collect/Multimap;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void play(SoundInstance sound, CallbackInfo ci, WeighedSoundEvents weightedSoundSet, ResourceLocation identifier, Sound sound2, float f, float g, SoundSource soundCategory) {
        SoundPhysics.setLastSoundCategoryAndName(soundCategory, sound.getLocation().getPath());
    }

}
