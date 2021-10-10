package com.sonicether.soundphysics;

import com.sonicether.soundphysics.config.SoundPhysicsConfig;
import com.sonicether.soundphysics.integration.ClothConfigIntegration;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fmlclient.ConfigGuiHandler;

import java.nio.file.Path;

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
        if (isClothConfigLoaded()) {
            ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((client, parent) -> {
                return ClothConfigIntegration.createConfigScreen(parent);
            }));
        }
    }

    private static boolean isClothConfigLoaded() {
        if (ModList.get().isLoaded("cloth-config2")) {
            try {
                Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
                SoundPhysics.LOGGER.info("Using Cloth Config GUI");
                return true;
            } catch (Exception e) {
                SoundPhysics.LOGGER.warn("Failed to load Cloth Config: {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public SoundPhysicsConfig createConfig() {
        return ConfigBuilder.build(getConfigFolder().resolve(MODID).resolve("soundphysics.properties"), true, SoundPhysicsConfig::new);
    }

    @Override
    public Path getConfigFolder() {
        return FMLLoader.getGamePath().resolve("config");
    }
}