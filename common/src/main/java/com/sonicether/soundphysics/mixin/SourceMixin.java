package com.sonicether.soundphysics.mixin;

import com.mojang.blaze3d.audio.Channel;
import com.sonicether.soundphysics.SoundPhysics;
import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Channel.class)
public class SourceMixin {

    @Shadow
    @Final
    private int source;

    private Vec3 pos;

    @Inject(method = "setSelfPosition", at = @At("HEAD"))
    private void setSelfPosition(Vec3 poss, CallbackInfo ci) {
        this.pos = poss;
    }

    @Inject(method = "play", at = @At("HEAD"))
    private void play(CallbackInfo ci) {
        SoundPhysics.onPlaySound(pos.x, pos.y, pos.z, source);
        SoundPhysics.logALError("Sound play injector");
    }

    @ModifyArg(method = "linearAttenuation", at = @At(value = "INVOKE", target = "org/lwjgl/openal/AL10.alSourcef(IIF)V", ordinal = 0, remap = false), index = 2)
    private float linearAttenuation(int pointer2, int param_id, float attenuation) {
        if (!SoundPhysicsMod.CONFIG.enabled.get()) {
            return attenuation;
        }
        if (param_id != AL11.AL_MAX_DISTANCE) {
            throw new IllegalArgumentException("Tried modifying wrong field. No attenuation here.");
        }
        return attenuation / SoundPhysicsMod.CONFIG.attenuationFactor.get().floatValue();
    }

}
