package com.sonicether.soundphysics.debug;

import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.gizmos.GizmoProperties;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RaycastRenderer implements DebugRenderer.SimpleDebugRenderer {

    private static final List<Ray> rays = Collections.synchronizedList(new ArrayList<>());
    private static final Minecraft mc = Minecraft.getInstance();

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        if (mc.level == null) {
            return;
        }
        if (!(SoundPhysicsMod.CONFIG.renderSoundBounces.get() || SoundPhysicsMod.CONFIG.renderOcclusion.get())) {
            synchronized (rays) {
                rays.clear();
            }
            return;
        }

        long gameTime = mc.level.getGameTime();
        synchronized (rays) {
            rays.removeIf(ray -> (gameTime - ray.tickCreated) > ray.lifespan || (gameTime - ray.tickCreated) < 0L);

            for (Ray ray : rays) {
                GizmoProperties line = Gizmos.line(ray.start, ray.end, ray.color);
                line.persistForMillis(ray.lifespan * 50);
                line.fadeOut();
                if (ray.throughWalls) {
                    line.setAlwaysOnTop();
                }
            }
        }
    }

    public static int color(ChatFormatting color) {
        Integer colorValue = color.getColor();
        if (colorValue == null) {
            return 0xFF000000;
        }
        return colorValue | 0xFF000000;
    }

    public static void addSoundBounceRay(Vec3 start, Vec3 end, int color) {
        if (!SoundPhysicsMod.CONFIG.renderSoundBounces.get()) {
            return;
        }

        addRay(start, end, color, false);
    }

    public static void addOcclusionRay(Vec3 start, Vec3 end, int color) {
        if (!SoundPhysicsMod.CONFIG.renderOcclusion.get()) {
            return;
        }

        addRay(start, end, color, true);
    }

    private static void addRay(Vec3 start, Vec3 end, int color, boolean throughWalls) {
        if (mc.player.position().distanceTo(start) > 32D && mc.player.position().distanceTo(end) > 32D) {
            return;
        }

        synchronized (rays) {
            rays.add(new Ray(start, end, color, throughWalls));
        }
    }

    private static class Ray {
        private final Vec3 start;
        private final Vec3 end;
        private final int color;
        private final long tickCreated;
        private final int lifespan;
        private final boolean throughWalls;

        public Ray(Vec3 start, Vec3 end, int color, boolean throughWalls) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.throughWalls = throughWalls;
            this.tickCreated = mc.level.getGameTime();
            this.lifespan = 20 * 2;
        }
    }

}
