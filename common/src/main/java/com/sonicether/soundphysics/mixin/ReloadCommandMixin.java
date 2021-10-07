package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.config.ConfigManager;
import net.minecraft.server.commands.ReloadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadCommand.class)
public class ReloadCommandMixin {

    @Inject(method = "reloadPacks", at = @At("HEAD"))
    private static void reloadPacks(CallbackInfo ci) {
        ConfigManager.reload(true);
    }
}
