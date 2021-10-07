package com.sonicether.soundphysics;

import com.sonicether.soundphysics.config.SoundPhysicsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.ConfigGuiHandler;

@Mod(SoundPhysicsMod.MODID)
public class ForgeSoundPhysicsMod extends SoundPhysicsMod {

    public ForgeSoundPhysicsMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        init();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((client, parent) -> {
            return AutoConfig.getConfigScreen(SoundPhysicsConfig.class, parent).get();
        }));
    }

}