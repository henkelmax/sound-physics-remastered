package com.sonicether.soundphysics.world;

import javax.annotation.Nonnull;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class UnsafeClientLevel implements ClientLevelProxy {

    private final ClientLevel clientLevel;

    public UnsafeClientLevel(ClientLevel level) {
        this.clientLevel = level;
    }

    @Override
    public BlockEntity getBlockEntity(@Nonnull BlockPos position) {
        return clientLevel.getBlockEntity(position);
    }

    @Override
    public BlockState getBlockState(@Nonnull BlockPos position) {
        return clientLevel.getBlockState(position);
    }

    @Override
    public FluidState getFluidState(@Nonnull BlockPos position) {
        return clientLevel.getFluidState(position);
    }

    @Override
    public int getHeight() {
        return clientLevel.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return clientLevel.getMinBuildHeight();
    }

}
