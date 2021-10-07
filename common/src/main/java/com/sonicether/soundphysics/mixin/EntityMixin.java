package com.sonicether.soundphysics.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.regex.Pattern;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract float getEyeHeight();

    @ModifyArg(method = "playSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"), index = 2)
    private double EyeHeightOffsetInjector(@Nullable Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {
        return y + calculateEntitySoundOffset(getEyeHeight(), sound);
    }

    private static final Pattern stepPattern = Pattern.compile(".*step.*");

    private static double calculateEntitySoundOffset(float standingEyeHeight, SoundEvent sound) {
        if (stepPattern.matcher(sound.getLocation().getPath()).matches()) {
            return 0.0;
        }
        return standingEyeHeight;
    }
}
