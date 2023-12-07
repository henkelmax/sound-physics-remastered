package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V"), cancellable = true)
    private void playSeededSound(Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float g, float h, long l, CallbackInfo ci) {
        if (!SoundPhysicsMod.CONFIG.enabled.get()) {
            return;
        }
        if (SoundEvents.BOAT_PADDLE_WATER.equals(soundEvent)) {
            ci.cancel();
            playSound(x, y + 0.5D, z, soundEvent, soundSource, g, h, false, l);
        }
    }

    @Shadow
    protected abstract void playSound(double d, double e, double f, SoundEvent soundEvent, SoundSource soundSource, float g, float h, boolean bl, long l);

}
