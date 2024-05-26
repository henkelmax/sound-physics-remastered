package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Entity {

    public LocalPlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    private void playSound(SoundEvent soundEvent, float volume, float pitch, CallbackInfo ci) {
        if (!SoundPhysicsMod.CONFIG.enabled.get()) {
            return;
        }
        ci.cancel();
        level().playSound((LocalPlayer) (Object) this, this, soundEvent, getSoundSource(), volume, pitch);
    }

}
