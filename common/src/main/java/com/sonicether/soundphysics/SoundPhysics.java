package com.sonicether.soundphysics;

import com.sonicether.soundphysics.config.ConfigManager;
import com.sonicether.soundphysics.config.ReverbParams;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static com.sonicether.soundphysics.RaycastFix.fixedRaycast;

public class SoundPhysics {

    public static final String LOG_PREFIX = "Sound Physics - %s";
    public static final Logger LOGGER = LogManager.getLogger(String.format(LOG_PREFIX, "General"));
    public static final Logger OCCLUSION_LOGGER = LogManager.getLogger(String.format(LOG_PREFIX, "Occlusion"));
    public static final Logger ENVIRONMENT_LOGGER = LogManager.getLogger(String.format(LOG_PREFIX, "Environment"));
    public static final Logger DEBUG_LOGGER = LogManager.getLogger(String.format(LOG_PREFIX, "Debug"));

    private static final Pattern rainPattern = Pattern.compile(".*rain.*");
    private static final Pattern stepPattern = Pattern.compile(".*step.*");
    private static final Pattern blockPattern = Pattern.compile(".*block..*");

    private static int auxFXSlot0;
    private static int auxFXSlot1;
    private static int auxFXSlot2;
    private static int auxFXSlot3;
    private static int reverb0;
    private static int reverb1;
    private static int reverb2;
    private static int reverb3;
    private static int directFilter0;
    private static int sendFilter0;
    private static int sendFilter1;
    private static int sendFilter2;
    private static int sendFilter3;
    private static Minecraft mc;

    private static SoundSource lastSoundCategory;
    private static String lastSoundName;

    public static float globalVolumeMultiplier = 4.0f;

    public static void init() {
        LOGGER.info("Initializing Sound Physics");
        setupEFX();
        LOGGER.info("EFX ready");
        mc = Minecraft.getInstance();
    }

    public static void syncReverbParams() {
        if (auxFXSlot0 != 0) {
            //Set the global reverb parameters and apply them to the effect and effectslot
            setReverbParams(ReverbParams.getReverb0(), auxFXSlot0, reverb0);
            setReverbParams(ReverbParams.getReverb1(), auxFXSlot1, reverb1);
            setReverbParams(ReverbParams.getReverb2(), auxFXSlot2, reverb2);
            setReverbParams(ReverbParams.getReverb3(), auxFXSlot3, reverb3);
        }
    }

    static void setupEFX() {
        //Get current context and device
        long currentContext = ALC10.alcGetCurrentContext();
        long currentDevice = ALC10.alcGetContextsDevice(currentContext);
        if (ALC10.alcIsExtensionPresent(currentDevice, "ALC_EXT_EFX")) {
            LOGGER.info("EFX Extension recognized");
        } else {
            LOGGER.error("EFX Extension not found on current device. Aborting.");
            return;
        }

        // Create auxiliary effect slots
        auxFXSlot0 = EXTEfx.alGenAuxiliaryEffectSlots();
        LOGGER.info("Aux slot {} created", auxFXSlot0);
        EXTEfx.alAuxiliaryEffectSloti(auxFXSlot0, EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, AL10.AL_TRUE);

        auxFXSlot1 = EXTEfx.alGenAuxiliaryEffectSlots();
        LOGGER.info("Aux slot {} created", auxFXSlot1);
        EXTEfx.alAuxiliaryEffectSloti(auxFXSlot1, EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, AL10.AL_TRUE);

        auxFXSlot2 = EXTEfx.alGenAuxiliaryEffectSlots();
        LOGGER.info("Aux slot {} created", auxFXSlot2);
        EXTEfx.alAuxiliaryEffectSloti(auxFXSlot2, EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, AL10.AL_TRUE);

        auxFXSlot3 = EXTEfx.alGenAuxiliaryEffectSlots();
        LOGGER.info("Aux slot {} created", auxFXSlot3);
        EXTEfx.alAuxiliaryEffectSloti(auxFXSlot3, EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, AL10.AL_TRUE);
        logALError("Failed creating auxiliary effect slots");

        reverb0 = EXTEfx.alGenEffects();
        EXTEfx.alEffecti(reverb0, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_EAXREVERB);
        logALError("Failed creating reverb effect slot 0");
        reverb1 = EXTEfx.alGenEffects();
        EXTEfx.alEffecti(reverb1, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_EAXREVERB);
        logALError("Failed creating reverb effect slot 1");
        reverb2 = EXTEfx.alGenEffects();
        EXTEfx.alEffecti(reverb2, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_EAXREVERB);
        logALError("Failed creating reverb effect slot 2");
        reverb3 = EXTEfx.alGenEffects();
        EXTEfx.alEffecti(reverb3, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_EAXREVERB);
        logALError("Failed creating reverb effect slot 3");

        directFilter0 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri(directFilter0, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);
        logDebug("directFilter0: {}", directFilter0);

        sendFilter0 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri(sendFilter0, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);
        logDebug("filter0: {}", sendFilter0);

        sendFilter1 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri(sendFilter1, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);
        logDebug("filter1: {}", sendFilter1);

        sendFilter2 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri(sendFilter2, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);
        logDebug("filter2: {}", sendFilter2);

        sendFilter3 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri(sendFilter3, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);
        logDebug("filter3: {}", sendFilter3);
        logALError("Error creating lowpass filters");

        syncReverbParams();
    }

    public static void setLastSoundCategoryAndName(SoundSource sc, String name) {
        lastSoundCategory = sc;
        lastSoundName = name;
    }

    public static void onPlaySound(double posX, double posY, double posZ, int sourceID) {
        logDebug("On play sound - Source ID: {} {}, {}, {} \tSound category: {} \tSound name: {}", sourceID, posX, posY, posZ, lastSoundCategory.toString(), lastSoundName);

        if (ConfigManager.getConfig().reloadReverb) {
            ConfigManager.reload(false);
            ConfigManager.getConfig().reloadReverb = false;
            ConfigManager.save();
        }

        long startTime = 0;
        long endTime;

        if (ConfigManager.getConfig().Misc.performanceLogging) {
            startTime = System.nanoTime();
        }

        evaluateEnvironment(sourceID, posX, posY, posZ);

        if (ConfigManager.getConfig().Misc.performanceLogging) {
            endTime = System.nanoTime();
            LOGGER.info("Total calculation time for sound {}: {} milliseconds", lastSoundName, (double) (endTime - startTime) / (double) 1000000);
        }
    }

    public static double calculateEntitySoundOffset(float standingEyeHeight, SoundEvent sound) {
        if (stepPattern.matcher(sound.getLocation().getPath()).matches()) {
            return 0.0;
        }
        return standingEyeHeight;
    }

    private static float getBlockReflectivity(BlockPos blockPos) {
        assert mc.level != null;
        BlockState blockState = mc.level.getBlockState(blockPos);
        SoundType soundType = blockState.getSoundType();

        double reflectivity = ConfigManager.getConfig().Material_Properties.reflectivityMap.get(".DEFAULT");

        String key = SoundPhysicsMod.blockSoundGroups.get(soundType);
        reflectivity = ConfigManager.getConfig().Material_Properties.reflectivityMap.getOrDefault(key, reflectivity);

        reflectivity *= ConfigManager.getConfig().General.globalBlockReflectance;

        return (float) reflectivity;
    }

    private static Vec3 reflect(Vec3 dir, Vec3 normal) {
        //dir - 2.0 * dot(normal, dir) * normal
        double dot = dir.dot(normal) * 2D;

        double x = dir.x - dot * normal.x;
        double y = dir.y - dot * normal.y;
        double z = dir.z - dot * normal.z;

        return new Vec3(x, y, z);
    }

    private static void evaluateEnvironment(int sourceID, double posX, double posY, double posZ) {
        if (!ConfigManager.getConfig().enabled) return;
        if (mc.player == null || mc.level == null || posY <= mc.level.getMinBuildHeight() || lastSoundCategory == SoundSource.RECORDS) {
            setDefaultEnvironment(sourceID);
            return;
        }

        boolean isRain = rainPattern.matcher(lastSoundName).matches();

        if (ConfigManager.getConfig().Performance.skipRainOcclusionTracing && isRain) {
            setDefaultEnvironment(sourceID);
            return;
        }
        long timeT = mc.level.getGameTime();
        if (RaycastFix.lastUpd != timeT) {
            if (timeT % 1024 == 0) {
                // just in case something gets corrupted
                RaycastFix.shapeCache = new Long2ObjectOpenHashMap<>(65536, 0.75F);
            } else {
                RaycastFix.shapeCache.clear();
            }
            RaycastFix.lastUpd = timeT;
        }

        float directCutoff;
        float absorptionCoeff = (float) (ConfigManager.getConfig().General.globalBlockAbsorption * 3D);

        //Direct sound occlusion
        Vec3 playerPos = mc.player.position();
        playerPos = new Vec3(playerPos.x, playerPos.y + mc.player.getEyeHeight(mc.player.getPose()), playerPos.z);
        Vec3 soundPos = new Vec3(posX, posY, posZ);
        Vec3 normalToPlayer = playerPos.subtract(soundPos).normalize();

        BlockPos soundBlockPos = new BlockPos(soundPos.x, soundPos.y, soundPos.z);

        logDebug("Player pos: {}, {}, {} \tSound Pos: {}, {}, {} \tTo player vector: {}, {}, {}", playerPos.x, playerPos.y, playerPos.z, soundPos.x, soundPos.y, soundPos.z, normalToPlayer.x, normalToPlayer.y, normalToPlayer.z);
        double occlusionAccumulation = 0F;
        //Cast a ray from the source towards the player
        Vec3 rayOrigin = soundPos;
        BlockPos lastBlockPos = soundBlockPos;
        boolean nineRay = ConfigManager.getConfig().Vlads_Tweaks._9RayDirectOcclusion && (lastSoundCategory == SoundSource.BLOCKS || blockPattern.matcher(lastSoundName).matches());
        int nOccRays = nineRay ? 9 : 1;
        double occlusionAccMin = Double.MAX_VALUE;
        for (int j = 0; j < nOccRays; j++) {
            if (j > 0) {
                int jj = j - 1;
                rayOrigin = new Vec3(soundBlockPos.getX() + 0.001D + 0.998D * (jj % 2), soundBlockPos.getY() + 0.001D + 0.998D * ((jj >> 1) % 2), soundBlockPos.getZ() + 0.001D + 0.998D * ((jj >> 2) % 2));
                lastBlockPos = soundBlockPos;
                occlusionAccumulation = 0F;
            }
            for (int i = 0; i < 10; i++) {
                BlockHitResult rayHit = fixedRaycast(new ClipContext(rayOrigin, playerPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, mc.player), mc.level, lastBlockPos);

                lastBlockPos = rayHit.getBlockPos();
                //If we hit a block

                if (rayHit.getType() == HitResult.Type.MISS) {
                    break;
                }

                BlockPos blockHitPos = rayHit.getBlockPos();
                Vec3 rayHitPos = rayHit.getLocation();
                BlockState blockHit = mc.level.getBlockState(blockHitPos);
                float blockOcclusion = 1F;

                // Regardless to whether we hit from inside or outside
                Vec3 dirVec = rayHitPos.subtract(blockHitPos.getX() + 0.5D, blockHitPos.getY() + 0.5D, blockHitPos.getZ() + 0.5D);
                Direction sideHit = Direction.getNearest(dirVec.x, dirVec.y, dirVec.z);

                if (!blockHit.isFaceSturdy(mc.level, rayHit.getBlockPos(), sideHit)) {
                    blockOcclusion *= ConfigManager.getConfig().Vlads_Tweaks.leakyBlocksOcclusionMultiplier;
                }

                logOcclusion("{} \t{},{},{}", blockHit.getBlock().getDescriptionId(), rayHitPos.x, rayHitPos.y, rayHitPos.z);

                //Accumulate density
                occlusionAccumulation += blockOcclusion;

                rayOrigin = rayHitPos;

                logOcclusion("New trace position: {}, {}, {}", rayOrigin.x, rayOrigin.y, rayOrigin.z);
            }
            occlusionAccMin = Math.min(occlusionAccMin, occlusionAccumulation);
        }
        occlusionAccumulation = Math.min(occlusionAccMin, ConfigManager.getConfig().Vlads_Tweaks.maxDirectOcclusionFromBlocks);
        directCutoff = (float) Math.exp(-occlusionAccumulation * absorptionCoeff);
        float directGain = (float) Math.pow(directCutoff, 0.1D);

        logOcclusion("Direct cutoff: {}, direct gain: {}", directCutoff, directGain);

        // Calculate reverb parameters for this sound
        float sendGain0 = 0F;
        float sendGain1 = 0F;
        float sendGain2 = 0F;
        float sendGain3 = 0F;

        float sendCutoff0 = 1F;
        float sendCutoff1 = 1F;
        float sendCutoff2 = 1F;
        float sendCutoff3 = 1F;

        if (mc.player.isUnderWater()) {
            directCutoff *= 1F - ConfigManager.getConfig().General.underwaterFilter;
        }

        if (isRain) {
            setEnvironment(sourceID, sendGain0, sendGain1, sendGain2, sendGain3, sendCutoff0, sendCutoff1, sendCutoff2, sendCutoff3, directCutoff, directGain);
            return;
        }

        // Shoot rays around sound

        float maxDistance = 256F;

        int numRays = ConfigManager.getConfig().Performance.environmentEvaluationRays;
        int rayBounces = ConfigManager.getConfig().Performance.environmentEvaluationRays;

        List<Map.Entry<Vec3, Double>> directions = new Vector<>(10, 10);
        boolean doDirEval = ConfigManager.getConfig().Vlads_Tweaks.soundDirectionEvaluation &&
                (occlusionAccumulation > 0 || !ConfigManager.getConfig().Vlads_Tweaks.notOccludedNoRedirect);

        float[] bounceReflectivityRatio = new float[rayBounces];

        float sharedAirspace = 0F;

        float rcpTotalRays = 1.F / (numRays * rayBounces);
        float rcpPrimaryRays = 1F / (numRays);

        float phi = 1.618033988F;
        float gAngle = phi * (float) Math.PI * 2F;

        for (int i = 0; i < numRays; i++) {
            float fiN = (float) i / numRays;
            float longitude = gAngle * (float) i * 1.0F;
            float latitude = (float) Math.asin(fiN * 2.0f - 1.0f);

            Vec3 rayDir = new Vec3(Math.cos(latitude) * Math.cos(longitude),
                    Math.cos(latitude) * Math.sin(longitude), Math.sin(latitude));

            Vec3 rayEnd = new Vec3(soundPos.x + rayDir.x * maxDistance, soundPos.y + rayDir.y * maxDistance,
                    soundPos.z + rayDir.z * maxDistance);

            BlockHitResult rayHit = fixedRaycast(new ClipContext(soundPos, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, mc.player), mc.level, soundBlockPos);

            if (rayHit.getType() == HitResult.Type.BLOCK) {
                double rayLength = soundPos.distanceTo(rayHit.getLocation());

                // Additional bounces
                BlockPos lastHitBlock = rayHit.getBlockPos();
                Vec3 lastHitPos = rayHit.getLocation();
                Vec3 lastHitNormal = new Vec3(rayHit.getDirection().step());
                Vec3 lastRayDir = rayDir;

                float totalRayDistance = (float) rayLength;

                // Secondary ray bounces
                for (int j = 0; j < rayBounces; j++) {
                    Vec3 newRayDir = reflect(lastRayDir, lastHitNormal);
                    Vec3 newRayStart = lastHitPos;
                    Vec3 newRayEnd = new Vec3(newRayStart.x + newRayDir.x * maxDistance, newRayStart.y + newRayDir.y * maxDistance, newRayStart.z + newRayDir.z * maxDistance);

                    BlockHitResult newRayHit = fixedRaycast(new ClipContext(newRayStart, newRayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, mc.player), mc.level, lastHitBlock);

                    float blockReflectivity = getBlockReflectivity(lastHitBlock);
                    float energyTowardsPlayer = 0.25F * (blockReflectivity * 0.75F + 0.25F);

                    if (newRayHit.getType() == HitResult.Type.MISS) {
                        totalRayDistance += lastHitPos.distanceTo(playerPos);
                    } else {
                        Vec3 newRayHitPos = rayHit.getLocation();
                        double newRayLength = lastHitPos.distanceTo(newRayHitPos);

                        bounceReflectivityRatio[j] += blockReflectivity;

                        totalRayDistance += newRayLength;

                        lastHitPos = newRayHitPos;
                        lastHitNormal = new Vec3(newRayHit.getDirection().step());
                        lastRayDir = newRayDir;
                        lastHitBlock = newRayHit.getBlockPos();

                        // Cast one final ray towards the player. If it's unobstructed, then the sound source and the player share airspace.
                        if (ConfigManager.getConfig().Performance.simplerSharedAirspaceSimulation && j == rayBounces - 1 || !ConfigManager.getConfig().Performance.simplerSharedAirspaceSimulation) {
                            Vec3 finalRayStart = new Vec3(lastHitPos.x + lastHitNormal.x * 0.001D, lastHitPos.y + lastHitNormal.y * 0.001D, lastHitPos.z + lastHitNormal.z * 0.001D);

                            BlockHitResult finalRayHit = fixedRaycast(new ClipContext(finalRayStart, playerPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, mc.player), mc.level, null);

                            if (finalRayHit.getType() == HitResult.Type.MISS) {
                                if (doDirEval) {
                                    directions.add(Map.entry(finalRayStart.subtract(playerPos), totalRayDistance + finalRayStart.distanceTo(playerPos)));
                                }
                                sharedAirspace += 1F;
                            }
                        }
                    }

                    float reflectionDelay = (float) Math.max(totalRayDistance, 0D) * 0.12F * blockReflectivity;

                    float cross0 = 1F - Mth.clamp(Math.abs(reflectionDelay - 0F), 0F, 1F);
                    float cross1 = 1F - Mth.clamp(Math.abs(reflectionDelay - 1F), 0F, 1F);
                    float cross2 = 1F - Mth.clamp(Math.abs(reflectionDelay - 2F), 0F, 1F);
                    float cross3 = Mth.clamp(reflectionDelay - 2.0f, 0.0f, 1.0f);

                    sendGain0 += cross0 * energyTowardsPlayer * 6.4F * rcpTotalRays;
                    sendGain1 += cross1 * energyTowardsPlayer * 12.8F * rcpTotalRays;
                    sendGain2 += cross2 * energyTowardsPlayer * 12.8F * rcpTotalRays;
                    sendGain3 += cross3 * energyTowardsPlayer * 12.8F * rcpTotalRays;

                    // Nowhere to bounce off of, stop bouncing!
                    if (newRayHit.getType() == HitResult.Type.MISS) {
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < bounceReflectivityRatio.length; i++) {
            bounceReflectivityRatio[i] = bounceReflectivityRatio[i] / numRays;
        }

        // Take weighted (on squared distance) average of the directions sound reflection came from
        dirEval:
        {
            if (directions.isEmpty()) {
                break dirEval;
            }
            if (ConfigManager.getConfig().Misc.performanceLogging) {
                LOGGER.info("Evaluating direction from {} entries", sharedAirspace);
            }
            Vec3 sum = new Vec3(0D, 0D, 0D);
            double weight = 0D;

            for (Map.Entry<Vec3, Double> direction : directions) {
                double val = direction.getValue();
                if (val <= 0D) {
                    break dirEval;
                }
                double w = 1 / (val * val);
                weight += w;
                sum = sum.add(direction.getKey().normalize().scale(w));
            }
            sum = sum.scale(1 / weight);
            if (sum.lengthSqr() >= ConfigManager.getConfig().Vlads_Tweaks.maxDirVariance) {
                setSoundPos(sourceID, sum.normalize().scale(soundPos.distanceTo(playerPos)).add(playerPos));
            }
        }

        sharedAirspace *= 64F;

        if (ConfigManager.getConfig().Performance.simplerSharedAirspaceSimulation) {
            sharedAirspace *= rcpPrimaryRays;
        } else {
            sharedAirspace *= rcpTotalRays;
        }

        float sharedAirspaceWeight0 = Mth.clamp(sharedAirspace / 20F, 0F, 1F);
        float sharedAirspaceWeight1 = Mth.clamp(sharedAirspace / 15F, 0F, 1F);
        float sharedAirspaceWeight2 = Mth.clamp(sharedAirspace / 10F, 0F, 1F);
        float sharedAirspaceWeight3 = Mth.clamp(sharedAirspace / 10F, 0F, 1F);

        sendCutoff0 = (float) Math.exp(-occlusionAccumulation * absorptionCoeff * 1F) * (1F - sharedAirspaceWeight0) + sharedAirspaceWeight0;
        sendCutoff1 = (float) Math.exp(-occlusionAccumulation * absorptionCoeff * 1F) * (1F - sharedAirspaceWeight1) + sharedAirspaceWeight1;
        sendCutoff2 = (float) Math.exp(-occlusionAccumulation * absorptionCoeff * 1F) * (1F - sharedAirspaceWeight2) + sharedAirspaceWeight2;
        sendCutoff3 = (float) Math.exp(-occlusionAccumulation * absorptionCoeff * 1F) * (1F - sharedAirspaceWeight3) + sharedAirspaceWeight3;

        // Attempt to preserve directionality when airspace is shared by allowing some of the dry signal through but filtered
        float averageSharedAirspace = (sharedAirspaceWeight0 + sharedAirspaceWeight1 + sharedAirspaceWeight2 + sharedAirspaceWeight3) * 0.25F;
        directCutoff = Math.max((float) Math.pow(averageSharedAirspace, 0.5D) * 0.2F, directCutoff);

        directGain = (float) Math.pow(directCutoff, 0.1D);

        logEnvironment("Bounce reflectivity 0: {}, bounce reflectivity 1: {}, bounce reflectivity 2: {}, bounce reflectivity 3: {}", bounceReflectivityRatio[0], bounceReflectivityRatio[1], bounceReflectivityRatio[2], bounceReflectivityRatio[3]);

        sendGain1 *= bounceReflectivityRatio[1];
        sendGain2 *= (float) Math.pow(bounceReflectivityRatio[2], 3D);
        sendGain3 *= (float) Math.pow(bounceReflectivityRatio[3], 4D);

        sendGain0 = Mth.clamp(sendGain0, 0F, 1F);
        sendGain1 = Mth.clamp(sendGain1, 0F, 1F);
        sendGain2 = Mth.clamp(sendGain2 * 1.05F - 0.05F, 0F, 1F);
        sendGain3 = Mth.clamp(sendGain3 * 1.05F - 0.05F, 0F, 1F);

        sendGain0 *= (float) Math.pow(sendCutoff0, 0.1D);
        sendGain1 *= (float) Math.pow(sendCutoff1, 0.1D);
        sendGain2 *= (float) Math.pow(sendCutoff2, 0.1D);
        sendGain3 *= (float) Math.pow(sendCutoff3, 0.1D);

        logEnvironment("Final environment settings: {}, {}, {}, {}", sendGain0, sendGain1, sendGain2, sendGain3);

        assert mc.player != null;
        if (mc.player.isUnderWater()) {
            sendCutoff0 *= 0.4f;
            sendCutoff1 *= 0.4f;
            sendCutoff2 *= 0.4f;
            sendCutoff3 *= 0.4f;
        }
        setEnvironment(sourceID, sendGain0, sendGain1, sendGain2, sendGain3, sendCutoff0, sendCutoff1, sendCutoff2, sendCutoff3, directCutoff, directGain);
    }

    private static void setDefaultEnvironment(int sourceID) {
        setEnvironment(sourceID, 0F, 0F, 0F, 0F, 1F, 1F, 1F, 1F, 1F, 1F);
    }

    private static void setEnvironment(int sourceID, float sendGain0, float sendGain1, float sendGain2, float sendGain3, float sendCutoff0, float sendCutoff1, float sendCutoff2, float sendCutoff3, float directCutoff, float directGain) {
        if (!ConfigManager.getConfig().enabled) {
            return;
        }
        // Set reverb send filter values and set source to send to all reverb fx slots
        EXTEfx.alFilterf(sendFilter0, EXTEfx.AL_LOWPASS_GAIN, sendGain0);
        EXTEfx.alFilterf(sendFilter0, EXTEfx.AL_LOWPASS_GAINHF, sendCutoff0);
        AL11.alSource3i(sourceID, EXTEfx.AL_AUXILIARY_SEND_FILTER, auxFXSlot0, 1, sendFilter0);
        logALError("Set Environment filter0:");

        EXTEfx.alFilterf(sendFilter1, EXTEfx.AL_LOWPASS_GAIN, sendGain1);
        EXTEfx.alFilterf(sendFilter1, EXTEfx.AL_LOWPASS_GAINHF, sendCutoff1);
        AL11.alSource3i(sourceID, EXTEfx.AL_AUXILIARY_SEND_FILTER, auxFXSlot1, 1, sendFilter1);
        logALError("Set Environment filter1:");

        EXTEfx.alFilterf(sendFilter2, EXTEfx.AL_LOWPASS_GAIN, sendGain2);
        EXTEfx.alFilterf(sendFilter2, EXTEfx.AL_LOWPASS_GAINHF, sendCutoff2);
        AL11.alSource3i(sourceID, EXTEfx.AL_AUXILIARY_SEND_FILTER, auxFXSlot2, 1, sendFilter2);
        logALError("Set Environment filter2:");

        EXTEfx.alFilterf(sendFilter3, EXTEfx.AL_LOWPASS_GAIN, sendGain3);
        EXTEfx.alFilterf(sendFilter3, EXTEfx.AL_LOWPASS_GAINHF, sendCutoff3);
        AL11.alSource3i(sourceID, EXTEfx.AL_AUXILIARY_SEND_FILTER, auxFXSlot3, 1, sendFilter3);
        logALError("Set Environment filter3:");

        EXTEfx.alFilterf(directFilter0, EXTEfx.AL_LOWPASS_GAIN, directGain);
        EXTEfx.alFilterf(directFilter0, EXTEfx.AL_LOWPASS_GAINHF, directCutoff);
        AL10.alSourcei(sourceID, EXTEfx.AL_DIRECT_FILTER, directFilter0);
        logALError("Set Environment directFilter0:");

        AL10.alSourcef(sourceID, EXTEfx.AL_AIR_ABSORPTION_FACTOR, (float) ConfigManager.getConfig().General.airAbsorption);
        logALError("Set Environment airAbsorption:");
    }

    private static void setSoundPos(int sourceID, Vec3 pos) {
        if (!ConfigManager.getConfig().enabled) return;
        //System.out.println(pos);//TO DO
        AL10.alSourcefv(sourceID, 4100, new float[]{(float) pos.x, (float) pos.y, (float) pos.z});
    }

    /*
     * Applies the parameters in the enum ReverbParams to the main reverb effect.
     */
    protected static void setReverbParams(ReverbParams r, int auxFXSlot, int reverbSlot) {
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DENSITY, r.density);
        logALError("Error while assigning reverb density: " + r.density);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DIFFUSION, r.diffusion);
        logALError("Error while assigning reverb diffusion: " + r.diffusion);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_GAIN, r.gain);
        logALError("Error while assigning reverb gain: " + r.gain);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_GAINHF, r.gainHF);
        logALError("Error while assigning reverb gainHF: " + r.gainHF);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DECAY_TIME, r.decayTime);
        logALError("Error while assigning reverb decayTime: " + r.decayTime);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DECAY_HFRATIO, r.decayHFRatio);
        logALError("Error while assigning reverb decayHFRatio: " + r.decayHFRatio);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_REFLECTIONS_GAIN, r.reflectionsGain);
        logALError("Error while assigning reverb reflectionsGain: " + r.reflectionsGain);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_LATE_REVERB_GAIN, r.lateReverbGain);
        logALError("Error while assigning reverb lateReverbGain: " + r.lateReverbGain);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_LATE_REVERB_DELAY, r.lateReverbDelay);
        logALError("Error while assigning reverb lateReverbDelay: " + r.lateReverbDelay);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_AIR_ABSORPTION_GAINHF, r.airAbsorptionGainHF);
        logALError("Error while assigning reverb airAbsorptionGainHF: " + r.airAbsorptionGainHF);
        EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_ROOM_ROLLOFF_FACTOR, r.roomRolloffFactor);
        logALError("Error while assigning reverb roomRolloffFactor: " + r.roomRolloffFactor);

        // Attach updated effect object
        EXTEfx.alAuxiliaryEffectSloti(auxFXSlot, EXTEfx.AL_EFFECTSLOT_EFFECT, reverbSlot);
    }

    protected static void logOcclusion(String message, Object... args) {
        if (!ConfigManager.getConfig().Misc.occlusionLogging) {
            return;
        }
        OCCLUSION_LOGGER.info(message, args);
    }

    protected static void logEnvironment(String message, Object... args) {
        if (!ConfigManager.getConfig().Misc.environmentLogging) {
            return;
        }
        ENVIRONMENT_LOGGER.info(message, args);
    }

    protected static void logDebug(String message, Object... args) {
        if (!ConfigManager.getConfig().Misc.debugLogging) {
            return;
        }
        DEBUG_LOGGER.info(message, args);
    }

    public static void logALError(String errorMessage) {
        int error = AL10.alGetError();
        if (error == AL10.AL_NO_ERROR) {
            return;
        }

        String errorName = switch (error) {
            case AL10.AL_INVALID_NAME -> "AL_INVALID_NAME";
            case AL10.AL_INVALID_ENUM -> "AL_INVALID_ENUM";
            case AL10.AL_INVALID_VALUE -> "AL_INVALID_VALUE";
            case AL10.AL_INVALID_OPERATION -> "AL_INVALID_OPERATION";
            case AL10.AL_OUT_OF_MEMORY -> "AL_OUT_OF_MEMORY";
            default -> Integer.toString(error);
        };

        LOGGER.error("{}: OpenAL error {}", errorMessage, errorName);
    }

}
