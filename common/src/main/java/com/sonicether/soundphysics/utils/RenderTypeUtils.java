package com.sonicether.soundphysics.utils;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class RenderTypeUtils {

    public static final RenderType.CompositeRenderType DEBUG_LINE_STRIP_SEETHROUGH;
    public static final RenderType DEBUG_LINE_STRIP = RenderType.debugLineStrip(1D);

    static {
        DEBUG_LINE_STRIP_SEETHROUGH = RenderType.create(
                "debug_line_strip_seethrough",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.DEBUG_LINE_STRIP,
                RenderType.TRANSIENT_BUFFER_SIZE,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                        .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(1D)))
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .createCompositeState(false)
        );
    }

}
