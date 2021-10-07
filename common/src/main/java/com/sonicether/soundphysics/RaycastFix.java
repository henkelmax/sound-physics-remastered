package com.sonicether.soundphysics;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class RaycastFix {

    public static long lastUpd = 0;
    public static Map<Long, VoxelShape> shapeCache = new Long2ObjectOpenHashMap<>(65536, 0.75F); // reset every tick, usually up to 22000

    public static BlockHitResult fixedRaycast(ClipContext context, BlockGetter world, @Nullable BlockPos ignore) {
        final Vec3 start = context.getFrom();
        final Vec3 end = context.getTo();
        return raycast(context.getFrom(), context.getTo(), context, (pos) -> {
            if (new BlockPos(pos).equals(ignore)) {
                return null;
            }

            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);

            VoxelShape voxelShape = shapeCache.computeIfAbsent(pos.asLong(), (key) -> blockState.getCollisionShape(world, pos));
            BlockHitResult blockHitResult = world.clipWithInteractionOverride(start, end, pos, voxelShape, blockState);
            VoxelShape voxelShape2 = shapeCache.computeIfAbsent(pos.asLong(), (key) -> context.getFluidShape(fluidState, world, pos));
            BlockHitResult blockHitResult2 = voxelShape2.clip(start, end, pos);

            if (blockHitResult2 == null) return blockHitResult;
            if (blockHitResult == null) return blockHitResult2;
            double d = start.distanceToSqr(blockHitResult.getLocation());
            double e = start.distanceToSqr(blockHitResult2.getLocation());
            return d <= e ? blockHitResult : blockHitResult2;
        }, () -> BlockHitResult.miss(context.getTo(), null, new BlockPos(context.getTo())));
    }

    static BlockHitResult raycast(Vec3 start, Vec3 end, ClipContext context, Function<BlockPos, BlockHitResult> blockHitFactory, Supplier<BlockHitResult> missFactory) {
        if (start.equals(end)) {
            return missFactory.get();
        } else {
            double d = Mth.lerp(-1.0E-7D, end.x, start.x);
            double e = Mth.lerp(-1.0E-7D, end.y, start.y);
            double f = Mth.lerp(-1.0E-7D, end.z, start.z);
            double g = Mth.lerp(-1.0E-7D, start.x, end.x);
            double h = Mth.lerp(-1.0E-7D, start.y, end.y);
            double i = Mth.lerp(-1.0E-7D, start.z, end.z);
            int j = Mth.floor(g);
            int k = Mth.floor(h);
            int l = Mth.floor(i);
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(j, k, l);
            BlockHitResult object = blockHitFactory.apply(mutable);
            if (object != null) {
                return object;
            } else {
                double m = d - g;
                double n = e - h;
                double o = f - i;
                int p = Mth.sign(m);
                int q = Mth.sign(n);
                int r = Mth.sign(o);
                double s = p == 0 ? 1.7976931348623157E308D : (double) p / m;
                double t = q == 0 ? 1.7976931348623157E308D : (double) q / n;
                double u = r == 0 ? 1.7976931348623157E308D : (double) r / o;
                double v = s * (p > 0 ? 1.0D - Mth.frac(g) : Mth.frac(g));
                double w = t * (q > 0 ? 1.0D - Mth.frac(h) : Mth.frac(h));
                double x = u * (r > 0 ? 1.0D - Mth.frac(i) : Mth.frac(i));

                BlockHitResult object2;
                do {
                    if (!(v <= 1.0D) && !(w <= 1.0D) && !(x <= 1.0D)) {
                        return missFactory.get();
                    }

                    if (v < w) {
                        if (v < x) {
                            j += p;
                            v += s;
                        } else {
                            l += r;
                            x += u;
                        }
                    } else if (w < x) {
                        k += q;
                        w += t;
                    } else {
                        l += r;
                        x += u;
                    }

                    object2 = blockHitFactory.apply(mutable.set(j, k, l));
                } while (object2 == null);

                return object2;
            }
        }
    }

}
