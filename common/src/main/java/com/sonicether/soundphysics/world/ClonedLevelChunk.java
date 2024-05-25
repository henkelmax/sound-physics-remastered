package com.sonicether.soundphysics.world;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;

/**
 * Read-only sparse clone of a client level chunk.
 *
 * Offers access to block states, fluid states, block entities, and height data.
 *
 * @author Saint (@augustsaintfreytag)
 */
public class ClonedLevelChunk extends ChunkAccess {

    private final LevelChunkTicks<Block> blockTicks;
    private final LevelChunkTicks<Fluid> fluidTicks;

    public ClonedLevelChunk(Level level, ChunkPos chunkPos, @Nullable LevelChunkSection[] levelChunkSections) {
        super(chunkPos, null, new ClonedLevelHeightAccessor(level), level.registryAccess().registryOrThrow(Registries.BIOME), 0, levelChunkSections, null);

        Heightmap.Types[] heightMapTypes = Heightmap.Types.values();
        int numberOfHeightMapTypes = heightMapTypes.length;

        for(int index = 0; index < numberOfHeightMapTypes; ++index) {
           Heightmap.Types types = heightMapTypes[index];

           if (ChunkStatus.FULL.heightmapsAfter().contains(types)) {
              this.heightmaps.put(types, new Heightmap(this, types));
           }
        }

        this.blockTicks = new LevelChunkTicks<Block>();
        this.fluidTicks = new LevelChunkTicks<Fluid>();
    }

    @Override
    public BlockEntity getBlockEntity(@Nonnull BlockPos blockPos) {
        return blockEntities.get(blockPos);
    }

    @Override
    public BlockState getBlockState(@Nonnull BlockPos blockPos) {
        return withLevelChunkSectionAtPosition(blockPos, section -> {
            if (section == null || section.hasOnlyAir()) {
                return Blocks.AIR.defaultBlockState();
            }

            return section.getBlockState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
        });
    }

    @Override
    public FluidState getFluidState(@Nonnull BlockPos blockPos) {
        return withLevelChunkSectionAtPosition(blockPos, section -> {
            if (section == null || section.hasOnlyAir()) {
                return Fluids.EMPTY.defaultFluidState();
            }

            return section.getFluidState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
        });
    }

    private <T> T withLevelChunkSectionAtPosition(BlockPos blockPos, Function<LevelChunkSection, T> block) {
        try {
            int sectionIndex = getSectionIndex(blockPos.getY());

            if (sectionIndex >= 0 && sectionIndex < sections.length) {
                LevelChunkSection section = sections[sectionIndex];
                return block.apply(section);
            }

            return block.apply(null);
        } catch (Throwable exception) {
            CrashReport crashReport = CrashReport.forThrowable(exception, "Getting section in cloned level chunk");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Chunk Section Get");

            crashReportCategory.setDetail("Location", () -> {
                return CrashReportCategory.formatLocation(this, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            });

            throw new ReportedException(crashReport);
        }
    }

    @Override
    public TickContainerAccess<Block> getBlockTicks() {
        return blockTicks;
    }

    @Override
    public TickContainerAccess<Fluid> getFluidTicks() {
        return fluidTicks;
    }

    @Override
    public TicksToSave getTicksForSerialization() {
        // Implemented but not needed for access by sound physics.
        return new ChunkAccess.TicksToSave(this.blockTicks, this.fluidTicks);
    }

    @Override
    public ChunkStatus getStatus() {
        // Implemented but not needed for access by sound physics.
        return ChunkStatus.FULL;
    }

    // Unsupported Functionality

    @Override
    public void addEntity(@Nonnull Entity entity) {
        throw new UnsupportedOperationException("Can not add entity to read-only level clone");
    }

    @Override
    public CompoundTag getBlockEntityNbtForSaving(@Nonnull BlockPos blockPos) {
        throw new UnsupportedOperationException("Can not read block entity NBT data from read-only level clone");
    }

    @Override
    public void removeBlockEntity(@Nonnull BlockPos blockPos) {
        throw new UnsupportedOperationException("Can not remove entity from read-only level clone");
    }

    @Override
    public void setBlockEntity(@Nonnull BlockEntity blockEntity) {
        throw new UnsupportedOperationException("Can not set block entity in read-only level clone");
    }

    @Override
    public BlockState setBlockState(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState, boolean unknownFlag) {
        throw new UnsupportedOperationException("Can not set block state in read-only level clone");
    }

}
