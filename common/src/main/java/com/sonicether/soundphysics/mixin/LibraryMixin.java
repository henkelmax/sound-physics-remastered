package com.sonicether.soundphysics.mixin;

import com.mojang.blaze3d.audio.Library;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.nio.IntBuffer;
import java.util.Arrays;

@Mixin(value = Library.class)
public class LibraryMixin {

    @ModifyArgs(method = "init", at = @At(value = "INVOKE", target = "Lorg/lwjgl/openal/ALC10;alcCreateContext(JLjava/nio/IntBuffer;)J"))
    private void modifyContext(Args args) {
        IntBuffer oldBuffer = args.get(1);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer;
            if (oldBuffer != null) {
                int[] original = toArray(oldBuffer.duplicate());
                buffer = stack.mallocInt(original.length + 3);
                buffer.put(original, 0, original.length - 1);
            } else {
                buffer = stack.mallocInt(4);
            }
            buffer.put(EXTEfx.ALC_MAX_AUXILIARY_SENDS).put(4).put(0);
            args.set(1, buffer.put(0).flip());
        }
    }

    @Unique
    private int[] toArray(IntBuffer buffer) {
        if (buffer.hasArray()) {
            if (buffer.arrayOffset() == 0)
                return buffer.array();

            return Arrays.copyOfRange(buffer.array(), buffer.arrayOffset(), buffer.array().length);
        }

        buffer.rewind();
        int[] arr = new int[buffer.remaining()];
        buffer.get(arr);

        return arr;
    }

}
