package com.sonicether.soundphysics;

import java.nio.file.Path;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(SoundPhysicsMod.MODID)
public class ForgeSoundPhysicsMod extends SoundPhysicsMod {

    public ForgeSoundPhysicsMod(FMLJavaModLoadingContext context) {
        FMLCommonSetupEvent.getBus(context.getModBusGroup()).addListener(this::commonSetup);
        FMLClientSetupEvent.getBus(context.getModBusGroup()).addListener(this::clientSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        init();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        initClient();
    }

    @Override
    public Path getConfigFolder() {
        return FMLLoader.getGamePath().resolve("config");
    }
}