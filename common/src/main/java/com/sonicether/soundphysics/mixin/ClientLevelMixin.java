package com.sonicether.soundphysics.mixin;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.utils.LevelAccessUtils;
import com.sonicether.soundphysics.world.CachingClientLevel;
import com.sonicether.soundphysics.world.ClonedClientLevel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements CachingClientLevel {

    // Properties

    @Unique
    private AtomicReference<ClonedClientLevel> cachedClone = new AtomicReference<>();

    @Unique
    public ClonedClientLevel getCachedClone() {
        return this.cachedClone.get();
    }

    @Unique
    public void setCachedClone(ClonedClientLevel cachedClone) {
        this.cachedClone.set(cachedClone);

    }

    // Tick & Cache

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"))
    private void tick(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        // Note: Mods may use mixins to inject logic that runs after this level clone operation,
        // any changes made on tick would not be included. Sound and level caching mixins could be
        // split and assigned different priorities to address this.

        var client = Minecraft.getInstance();
        var clientLevel = (ClientLevel) (Object) this;
        var player = client.player;

        LevelAccessUtils.tickLevelCache(clientLevel, player);
    }

    // Sounds

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V"), cancellable = true)
    private void playSeededSound(Player player, double x, double y, double z, Holder<SoundEvent> holder, SoundSource soundSource, float g, float h, long l, CallbackInfo ci) {
        if (!SoundPhysicsMod.CONFIG.enabled.get()) {
            return;
        }

        SoundEvent value = holder.value();
        if (SoundEvents.BOAT_PADDLE_WATER.equals(value)) {
            ci.cancel();
            playSound(x, y + 0.5D, z, value, soundSource, g, h, false, l);
        }
    }

    @Shadow
    protected abstract void playSound(double d, double e, double f, SoundEvent soundEvent, SoundSource soundSource, float g, float h, boolean bl, long l);

}