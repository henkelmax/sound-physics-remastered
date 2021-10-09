package com.sonicether.soundphysics;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
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
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RaycastFix {

    private static long lastUpdate = 0L;
    private static final Map<Long, VoxelShape> shapeCache = new Long2ObjectOpenHashMap<>(65536, 0.75F);
    private static final Minecraft mc = Minecraft.getInstance();


    public static void updateCache() {
        long gameTime = Minecraft.getInstance().level.getGameTime();
        // Reset shape cache every tick, usually up to 22000
        if (lastUpdate != gameTime) {
            if (!shapeCache.isEmpty() && SoundPhysicsMod.CONFIG.performanceLogging.get()) {
                SoundPhysics.LOGGER.info("Clearing {} raycasting block cache entries", shapeCache.size());
            }
            shapeCache.clear();
            lastUpdate = gameTime;
        }
    }

    public static BlockHitResult fixedRaycast(Vec3 start, Vec3 end, @Nullable BlockPos ignore) {
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, mc.player);
        return traverseBlocks(context.getFrom(), context.getTo(), context, (c, pos) -> {
            if (new BlockPos(pos).equals(ignore)) {
                return null;
            }

            BlockState blockState = mc.level.getBlockState(pos);
            FluidState fluidState = mc.level.getFluidState(pos);

            VoxelShape blockShape = shapeCache.computeIfAbsent(pos.asLong(), (key) -> blockState.getCollisionShape(mc.level, pos));
            BlockHitResult blockHit = mc.level.clipWithInteractionOverride(start, end, pos, blockShape, blockState);
            VoxelShape fluidShape = shapeCache.computeIfAbsent(pos.asLong(), (key) -> context.getFluidShape(fluidState, mc.level, pos));
            BlockHitResult fluidHit = fluidShape.clip(start, end, pos);

            if (fluidHit == null) {
                return blockHit;
            }
            if (blockHit == null) {
                return fluidHit;
            }
            double blockLocation = start.distanceToSqr(blockHit.getLocation());
            double fluidLocation = start.distanceToSqr(fluidHit.getLocation());
            return blockLocation <= fluidLocation ? blockHit : fluidHit;
        }, c -> BlockHitResult.miss(context.getTo(), null, new BlockPos(context.getTo())));
    }

    private static <T, C> T traverseBlocks(Vec3 start, Vec3 end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory) {
        if (start.equals(end)) {
            return missFactory.apply(context);
        } else {
            double d0 = Mth.lerp(-1.0E-7D, end.x, start.x);
            double d1 = Mth.lerp(-1.0E-7D, end.y, start.y);
            double d2 = Mth.lerp(-1.0E-7D, end.z, start.z);
            double d3 = Mth.lerp(-1.0E-7D, start.x, end.x);
            double d4 = Mth.lerp(-1.0E-7D, start.y, end.y);
            double d5 = Mth.lerp(-1.0E-7D, start.z, end.z);
            int i = Mth.floor(d3);
            int j = Mth.floor(d4);
            int k = Mth.floor(d5);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(i, j, k);
            T t = blockHitFactory.apply(context, pos);
            if (t != null) {
                return t;
            } else {
                double d6 = d0 - d3;
                double d7 = d1 - d4;
                double d8 = d2 - d5;
                int l = Mth.sign(d6);
                int i1 = Mth.sign(d7);
                int j1 = Mth.sign(d8);
                double d9 = l == 0 ? Double.MAX_VALUE : (double) l / d6;
                double d10 = i1 == 0 ? Double.MAX_VALUE : (double) i1 / d7;
                double d11 = j1 == 0 ? Double.MAX_VALUE : (double) j1 / d8;
                double d12 = d9 * (l > 0 ? 1.0D - Mth.frac(d3) : Mth.frac(d3));
                double d13 = d10 * (i1 > 0 ? 1.0D - Mth.frac(d4) : Mth.frac(d4));
                double d14 = d11 * (j1 > 0 ? 1.0D - Mth.frac(d5) : Mth.frac(d5));

                while (d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D) {
                    if (d12 < d13) {
                        if (d12 < d14) {
                            i += l;
                            d12 += d9;
                        } else {
                            k += j1;
                            d14 += d11;
                        }
                    } else if (d13 < d14) {
                        j += i1;
                        d13 += d10;
                    } else {
                        k += j1;
                        d14 += d11;
                    }

                    T t1 = blockHitFactory.apply(context, pos.set(i, j, k));
                    if (t1 != null) {
                        return t1;
                    }
                }

                return missFactory.apply(context);
            }
        }
    }

}
