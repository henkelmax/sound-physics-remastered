package com.sonicether.soundphysics.models;

import java.util.HashMap;

import javax.annotation.Nonnull;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ClientLevelProxy implements BlockGetter {

    private static final int cacheDistance = 8;
    
    private final ClonedLevelHeightAccessor heightAccessor;
    private final HashMap<ChunkPos, ClonedLevelChunk> clonedLevelChunks;

    public ClientLevelProxy(ClientLevel level, BlockPos origin) {
        ClientChunkCache cache;
        ClonedLevelHeightAccessor heightAccessor;

        synchronized(level) {
            cache = level.getChunkSource();
            heightAccessor = new ClonedLevelHeightAccessor(level);
        }

        var cachedLevelChunks = new HashMap<ChunkPos, ClonedLevelChunk>();
        var originChunkPos = new ChunkPos(origin.getX() >> 4, origin.getZ() >> 4);

        synchronized(cache) {
            for (int x = -cacheDistance; x < cacheDistance; x++) {
                for (int z = -cacheDistance; z < cacheDistance; z++) {
                    var chunkPos = new ChunkPos(originChunkPos.x + x, originChunkPos.z + z);
                    var chunk = cache.getChunk(chunkPos.x, chunkPos.z, false);

                    if (chunk == null) {
                        continue;
                    }

                    var clonedChunk = new ClonedLevelChunk(level, chunkPos, chunk.getSections());
                    cachedLevelChunks.put(chunkPos, clonedChunk);
                }
            }
        }

        // (1) Somehow get x, z range of all chunks in client level cache.
        // (2) Iterate through all chunks in client level cache via `cache.getChunk`.
        // (3) For each chunk, create clone, from `LevelChunk` to `ClonedLevelChunk`.

        // Alternative, manually compute chunk indices as radius around player position
        // and just try to request all chunks in range from level chunk source.

        this.heightAccessor = heightAccessor;
        this.clonedLevelChunks = cachedLevelChunks;
    }

    // Properties

    public ClonedLevelChunk getChunk(int x, int z) {
        var chunkPos = new ChunkPos(x, z);
        return this.clonedLevelChunks.get(chunkPos);
    }

    public BlockState getBlockState(@Nonnull BlockPos blockPos) {
        if (this.isOutsideBuildHeight(blockPos)) {
            return Blocks.VOID_AIR.defaultBlockState();
        } else {
            var chunkPos = new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
            var levelChunk = this.clonedLevelChunks.get(chunkPos);

            if (levelChunk == null) {
                return Blocks.VOID_AIR.defaultBlockState();
            }

            return levelChunk.getBlockState(blockPos);
        }
   }

    public FluidState getFluidState(@Nonnull BlockPos blockPos) {
        if (this.isOutsideBuildHeight(blockPos)) {
            return Fluids.EMPTY.defaultFluidState();
        } else {
            var chunkPos = new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
            var levelChunk = this.clonedLevelChunks.get(chunkPos);

            if (levelChunk == null) {
                return Fluids.EMPTY.defaultFluidState();
            }

            return levelChunk.getFluidState(blockPos);
        }
    }

    public int getHeight() {
        return this.heightAccessor.getHeight();
    }

    public int getMinBuildHeight() {
        return this.heightAccessor.getMinBuildHeight();
    }

    public BlockEntity getBlockEntity(@Nonnull BlockPos blockPos) {
        var chunkPos = new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        var levelChunk = this.clonedLevelChunks.get(chunkPos);

        if (levelChunk == null) {
            return null;
        }

        return levelChunk.getBlockEntity(blockPos);
    }
}

