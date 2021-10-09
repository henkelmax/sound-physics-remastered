package com.sonicether.soundphysics.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RaycastRenderer {

    private static final List<Ray> rays = Collections.synchronizedList(new ArrayList<>());
    private static final Minecraft mc = Minecraft.getInstance();

    public static void renderRays(double x, double y, double z) {
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
                renderRay(ray, x, y, z);
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

    public static void renderRay(Ray ray, double x, double y, double z) {
        int red = getRed(ray.color);
        int green = getGreen(ray.color);
        int blue = getBlue(ray.color);

        if (!ray.throughWalls) {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1F);

        bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        bufferBuilder.vertex(ray.start.x - x, ray.start.y - y, ray.start.z - z).color(red, green, blue, 255).endVertex();
        bufferBuilder.vertex(ray.end.x - x, ray.end.y - y, ray.end.z - z).color(red, green, blue, 255).endVertex();

        tesselator.end();
        RenderSystem.lineWidth(1F);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
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
