package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.server.commands.PlaySoundCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlaySoundCommand.class)
public class PlaySoundCommandMixin {

    @ModifyConstant(method = "playSound", constant = @Constant(floatValue = 16F), expect = 1)
    private static float allowance1(float value) {
        if (!SoundPhysicsMod.CONFIG.enabled.get()) {
            return value;
        }
        return value * SoundPhysicsMod.CONFIG.soundDistanceAllowance.get().floatValue();
    }

    @ModifyConstant(method = "playSound", constant = @Constant(doubleValue = 16D), expect = 1)
    private static double allowance2(double value) {
        if (!SoundPhysicsMod.CONFIG.enabled.get()) {
            return value;
        }
        return value * SoundPhysicsMod.CONFIG.soundDistanceAllowance.get();
    }

}
