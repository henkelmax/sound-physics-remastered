package com.sonicether.soundphysics;

import com.sonicether.soundphysics.integration.voicechat.AudioChannel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReflectedAudio {

    // SoundDirection / Travel length
    private final List<Map.Entry<Vec3, Double>> airspaceDirections;

    @Nullable
    private Map.Entry<Vec3, Double> directDirection;

    private final double occlusion;

    private final ResourceLocation sound;

    private int sharedAirspaces;

    public ReflectedAudio(double occlusion, ResourceLocation sound) {
        this.occlusion = occlusion;
        this.sound = sound;
        this.airspaceDirections = new LinkedList<>();
    }

    public boolean shouldEvaluateDirection() {
        return SoundPhysicsMod.CONFIG.soundDirectionEvaluation.get() && (occlusion > 0D || !SoundPhysicsMod.CONFIG.redirectNonOccludedSounds.get()) && !AudioChannel.isVoicechatSound(sound);
    }

    public int getSharedAirspaces() {
        return sharedAirspaces;
    }

    public void addDirectAirspace(Vec3 sharedAirspaceVector) {
        directDirection = Map.entry(sharedAirspaceVector, sharedAirspaceVector.length());
    }

    public void addSharedAirspace(Vec3 sharedAirspaceVector, double totalRayDistance) {
        double length = totalRayDistance + sharedAirspaceVector.length();
        if (length <= 0D || length > SoundPhysicsMod.CONFIG.maxSoundProcessingDistance.get() / SoundPhysicsMod.CONFIG.attenuationFactor.get()) {
            return;
        }
        sharedAirspaces++;
        if (!shouldEvaluateDirection()) {
            return;
        }
        airspaceDirections.add(Map.entry(sharedAirspaceVector, length));
    }

    /**
     * Evaluates where the sound is actually heard from
     * Weighted (on squared distance) average of the directions sound reflection came from
     *
     * @param soundPos    the position where the sound actually originates from
     * @param listenerPos the position of the listener
     * @return the new sound position or null if the position shouldn't be changed
     */
    @Nullable
    public Vec3 evaluateSoundPosition(Vec3 soundPos, Vec3 listenerPos) {
        if (!shouldEvaluateDirection()) {
            return null;
        }

        if (airspaceDirections.isEmpty()) {
            return null;
        }

        Vec3 sum;

        if (directDirection != null) {
            sum = directDirection.getKey().normalize();
        } else {
            sum = new Vec3(0D, 0D, 0D);
        }

        for (Map.Entry<Vec3, Double> direction : airspaceDirections) {
            double val = direction.getValue();
            if (val <= 0D) {
                return null; //TODO check
            }
            double w = 1D / (val * val);
            sum = sum.add(direction.getKey().normalize().scale(w));
        }
        Vec3 normalized = sum.normalize();
        // If the vector is too small to normalize
        if (normalized.length() < 0.5D) {
            return null;
        }
        return normalized.scale(soundPos.distanceTo(listenerPos)).add(listenerPos);
    }

}
