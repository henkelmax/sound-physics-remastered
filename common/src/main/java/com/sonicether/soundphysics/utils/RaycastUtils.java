package com.sonicether.soundphysics.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class RaycastUtils {

    public static BlockHitResult rayCast(@Nullable BlockGetter blockGetter, Vec3 from, Vec3 to, @Nullable BlockPos ignore) {
        if (blockGetter == null) {
            Vec3 vec = from.subtract(to);
            return BlockHitResult.miss(to, Direction.getNearest(vec.x, vec.y, vec.z), BlockPos.containing(to));
        }
        return BlockGetter.traverseBlocks(from, to, blockGetter, (g, pos) -> {
            if (pos.equals(ignore)) {
                return null;
            }
            BlockState blockState = blockGetter.getBlockState(pos);
            FluidState fluidState = blockGetter.getFluidState(pos);
            VoxelShape shape = ClipContext.Block.COLLIDER.get(blockState, blockGetter, pos, CollisionContext.empty());
            BlockHitResult blockHitResult = blockGetter.clipWithInteractionOverride(from, to, pos, shape, blockState);
            VoxelShape fluidShape = fluidState.getShape(blockGetter, pos);
            BlockHitResult fluidHitResult = fluidShape.clip(from, to, pos);
            if (fluidHitResult == null) {
                return blockHitResult;
            }
            if (blockHitResult == null) {
                return fluidHitResult;
            }
            double blockDistance = from.distanceToSqr(blockHitResult.getLocation());
            double fluidDistance = from.distanceToSqr(fluidHitResult.getLocation());
            return blockDistance <= fluidDistance ? blockHitResult : fluidHitResult;
        }, (g) -> {
            Vec3 vec = from.subtract(to);
            return BlockHitResult.miss(to, Direction.getNearest(vec.x, vec.y, vec.z), BlockPos.containing(to));
        });
    }

}
