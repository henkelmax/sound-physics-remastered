package com.sonicether.soundphysics.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.utils.RenderTypeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RaycastRenderer {

    private static final List<Ray> rays = Collections.synchronizedList(new ArrayList<>());
    private static final Minecraft mc = Minecraft.getInstance();

    public static void renderRays(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double x, double y, double z) {
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
                renderRay(ray, poseStack, bufferSource, x, y, z);
            }
        }
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

    public static void addRay(Vec3 start, Vec3 end, int color, boolean throughWalls) {
        if (mc.player.position().distanceTo(start) > 32D && mc.player.position().distanceTo(end) > 32D) {
            return;
        }

        synchronized (rays) {
            rays.add(new Ray(start, end, color, throughWalls));
        }
    }

    public static void renderRay(Ray ray, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double x, double y, double z) {
        poseStack.pushPose();
        int red = getRed(ray.color);
        int green = getGreen(ray.color);
        int blue = getBlue(ray.color);

        VertexConsumer consumer;
        if (ray.throughWalls) {
            consumer = bufferSource.getBuffer(RenderTypeUtils.DEBUG_LINE_STRIP_SEETHROUGH);
        } else {
            consumer = bufferSource.getBuffer(RenderTypeUtils.DEBUG_LINE_STRIP);
        }

        Matrix4f matrix4f = poseStack.last().pose();

        consumer.vertex(matrix4f, (float) (ray.start.x - x), (float) (ray.start.y - y), (float) (ray.start.z - z)).color(red, green, blue, 255).endVertex();
        consumer.vertex(matrix4f, (float) (ray.end.x - x), (float) (ray.end.y - y), (float) (ray.end.z - z)).color(red, green, blue, 255).endVertex();

        poseStack.popPose();
    }

    private static int getRed(int argb) {
        return (argb >> 16) & 0xFF;
    }

    private static int getGreen(int argb) {
        return (argb >> 8) & 0xFF;
    }

    private static int getBlue(int argb) {
        return argb & 0xFF;
    }

    private static class Ray {
        private final Vec3 start;
        private final Vec3 end;
        private final int color;
        private final long tickCreated;
        private final long lifespan;
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
