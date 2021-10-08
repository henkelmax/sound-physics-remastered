package com.sonicether.soundphysics.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.ChatFormatting;
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
        if (!SoundPhysicsMod.CONFIG.renderSoundBounces.get()) {
            synchronized (rays) {
                rays.clear();
            }
            return;
        }
        long gameTime = mc.level.getGameTime();
        synchronized (rays) {
            rays.removeIf(ray -> (gameTime - ray.tickCreated) > ray.lifespan || (gameTime - ray.tickCreated) < 0L);
            for (Ray ray : rays) {
                renderRay(ray.start, ray.end, ray.color, x, y, z);
            }
        }
    }

    public static void addRay(Vec3 start, Vec3 end, ChatFormatting color) {
        if (!SoundPhysicsMod.CONFIG.renderSoundBounces.get()) {
            return;
        }
        synchronized (rays) {
            rays.add(new Ray(start, end, color, mc.level.getGameTime()));
        }
    }

    public static void renderRay(Vec3 start, Vec3 end, ChatFormatting color, double x, double y, double z) {
        Integer col = color.getColor();
        if (col == null) {
            return;
        }
        int red = getRed(col);
        int green = getGreen(col);
        int blue = getBlue(col);

        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(2F);
        bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        bufferBuilder.vertex(start.x - x, start.y - y, start.z - z).color(red, green, blue, 0).endVertex();
        bufferBuilder.vertex(start.x - x, start.y - y, start.z - z).color(red, green, blue, 255).endVertex();
        bufferBuilder.vertex(end.x - x, end.y - y, end.z - z).color(red, green, blue, 255).endVertex();
        bufferBuilder.vertex(end.x - x, end.y - y, end.z - z).color(red, green, blue, 0).endVertex();

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
        private final ChatFormatting color;
        private final long tickCreated;
        private final long lifespan;

        public Ray(Vec3 start, Vec3 end, ChatFormatting color, long tickCreated) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.tickCreated = tickCreated;
            this.lifespan = 20 * 2;
        }
    }

}
