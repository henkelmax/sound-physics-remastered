package com.sonicether.soundphysics.utils;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class RenderTypeUtils {

    public static final RenderPipeline DEBUG_LINE_STRIP_PIPELINE_SEETHROUGH;
    public static final RenderType.CompositeRenderType DEBUG_LINE_STRIP_SEETHROUGH;
    public static final RenderType DEBUG_LINE_STRIP = RenderType.debugLineStrip(1D);

    static {
        DEBUG_LINE_STRIP_PIPELINE_SEETHROUGH = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
                .withLocation("pipeline/debug_line_strip_seethrough")
                .withVertexShader("core/position_color")
                .withFragmentShader("core/position_color")
                .withCull(false)
                .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINE_STRIP)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .build();

        DEBUG_LINE_STRIP_SEETHROUGH = RenderType.create(
                "debug_line_strip_seethrough",
                RenderType.TRANSIENT_BUFFER_SIZE,
                DEBUG_LINE_STRIP_PIPELINE_SEETHROUGH,
                RenderType.CompositeState.builder()
                        .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(1D)))
                        .createCompositeState(false)
        );
    }

}
