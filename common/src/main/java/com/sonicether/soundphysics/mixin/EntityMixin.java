package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.SoundPhysicsMod;
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

    private static final Pattern STEP_PATTERN = Pattern.compile(".*step.*");

    @Shadow
    public abstract float getEyeHeight();

    @ModifyArg(method = "playSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"), index = 2)
    private double playSound(@Nullable Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {
        if (sound == null) {
            return y;
        }
        if (!SoundPhysicsMod.CONFIG.enabled.get()) {
            return y;
        }
        return y + calculateEntitySoundOffset(getEyeHeight(), sound);
    }

    private static double calculateEntitySoundOffset(float standingEyeHeight, SoundEvent sound) {
        if (STEP_PATTERN.matcher(sound.getLocation().getPath()).matches()) {
            return 0D;
        }
        return standingEyeHeight;
    }
}
