package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysicsMod;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.Map;
import java.util.stream.Collectors;

@Config(name = "sound_physics")
@Config.Gui.Background("minecraft:textures/block/note_block.png")
public class SoundPhysicsConfig implements ConfigData {

    @Comment("Enable reverb?")
    public boolean enabled = true;

    @Comment("Don't forget to make this true when you change the config")
    public boolean reloadReverb = true;

    @ConfigEntry.Gui.CollapsibleObject
    public General General = new General();

    @ConfigEntry.Gui.CollapsibleObject
    public Performance Performance = new Performance();

    @ConfigEntry.Gui.CollapsibleObject
    public Material_Properties Material_Properties = new Material_Properties();

    @ConfigEntry.Gui.CollapsibleObject
    public Vlads_Tweaks Vlads_Tweaks = new Vlads_Tweaks();

    @ConfigEntry.Gui.CollapsibleObject
    public Misc Misc = new Misc();

    public static class General{
        @Comment("Affects how quiet a sound gets based on distance. Lower values mean distant sounds are louder. 1.0 is the physically correct value.\n0.2 - 1.0 or just don't set it to 0")
        public double attenuationFactor = 1.0;
        @Comment("The global volume of simulated reverberations.\n0.1 - 2.0")
        public double globalReverbGain = 1.0;
        @Comment("The brightness of reverberation. Higher values result in more high frequencies in reverberation. Lower values give a more muffled sound to the reverb.\n0.1 - 2.0")
        public double globalReverbBrightness = 1.0;
        @Comment("The global amount of sound that will be absorbed when traveling through blocks.\n 0.1 - 4.0")
        public double globalBlockAbsorption = 1.0;
        @Comment("The global amount of sound reflectance energy of all blocks. Lower values result in more conservative reverb simulation with shorter reverb tails. Higher values result in more generous reverb simulation with higher reverb tails.\n0.1 - 4.0")
        public double globalBlockReflectance = 1.0;
        @Comment("Minecraft won't allow sounds to play past a certain distance. This parameter is a multiplier for how far away a sound source is allowed to be in order for it to actually play. Values too high can cause polyphony issues.\n1.0 - 6.0")
        public double soundDistanceAllowance = 4.0;
        @Comment("A value controlling the amount that air absorbs high frequencies with distance. A value of 1.0 is physically correct for air with normal humidity and temperature. Higher values mean air will absorb more high frequencies with distance. 0 disables this effect.\n0.0 - 5.0")
        public double airAbsorption = 1.0;
        @Comment("How much sound is filtered when the player is underwater. 0.0 means no filter. 1.0 means fully filtered.\n0.0 - 1.0")
        public double underwaterFilter = 0.8;
    }

    public static class Performance{
        @Comment("If true, rain sound sources won't trace for sound occlusion. This can help performance during rain.")
        public boolean skipRainOcclusionTracing = true;
        @Comment("The number of rays to trace to determine reverberation for each sound source. More rays provides more consistent tracing results but takes more time to calculate. Decrease this value if you experience lag spikes when sounds play.\n8 - 64")
        public int environmentEvaluationRays = 32;
        @Comment("The number of rays bounces to trace to determine reverberation for each sound source. More bounces provides more echo and sound ducting but takes more time to calculate. Decrease this value if you experience lag spikes when sounds play. Capped by max distance.\n4 - ?")
        public int environmentEvaluationRayBounces = 4;
        @Comment("If true, enables a simpler technique for determining when the player and a sound source share airspace. Might sometimes miss recognizing shared airspace, but it's faster to calculate.")
        public boolean simplerSharedAirspaceSimulation = false;
    }

    public static class Material_Properties {
        @Comment("Sound reflectivity for blocks.\n0.0 - 1.0")
        @ConfigEntry.Gui.CollapsibleObject
        public Map<String, Double> reflectivityMap;
        {
            Map<String, Double> map =
                        SoundPhysicsMod.blockSoundGroups.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getValue, (a) -> 0.5));
            map.put("STONE", 1.0);
            map.put("WOOD", 0.4);
            map.put("GRAVEL", 0.3);
            map.put("GRASS", 0.5);
            map.put("METAL", 1.0);
            map.put("GLASS", 0.5);
            map.put("WOOL", 0.05);
            map.put("SAND", 0.2);
            map.put("SNOW", 0.2);
            map.put("LADDER", 0.4);
            map.put("ANVIL", 1.0);
            map.put(".DEFAULT", 0.5); // TODO more
            reflectivityMap = map;
        }
    }

    public static class Vlads_Tweaks {
        @Comment("If sound hits non-full-square side, direct block occlusion is multiplied by this.\n0.0 - ")
        public double leakyBlocksOcclusionMultiplier = 0.15;
        @Comment("The amount at which this is capped. 10 * block_occlusion is the theoretical limit")
        public double maxDirectOcclusionFromBlocks = 10;
        @Comment("Calculate direct occlusion as the minimum of 9 rays from vertices of a block")
        public boolean _9RayDirectOcclusion = true;
        @Comment("Whether to try calculating where the sound should come from based on reflections")
        public boolean soundDirectionEvaluation = true;
        @Comment("Maximum direction variance of incoming reflections allowed to redirect sound\n0.0-1.0")
        public double maxDirVariance = 0.5;
        @Comment("Skip redirecting non-occluded sounds (the ones you can see directly)")
        public boolean notOccludedNoRedirect = true;
    }

    public static class Misc {
        @Comment("General debug logging")
        public boolean debugLogging = false;
        @Comment("Occlusion tracing information logging")
        public boolean occlusionLogging = false;
        @Comment("Environment evaluation information logging")
        public boolean environmentLogging = false;
        @Comment("Performance information logging")
        public boolean performanceLogging = false;
    }

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
    @Comment("Soft presets (preserve some settings). Press reloadReverb to apply. Presets: [DEFAULT, RESET_MATERIALS, SP1_0_SOUND_OCCLUSION]. (LOAD_SUCCESS = null)")
    public ConfigPresets preset = ConfigPresets.LOAD_SUCCESS;

}