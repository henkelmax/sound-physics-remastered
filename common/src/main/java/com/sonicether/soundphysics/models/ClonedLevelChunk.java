package com.sonicether.soundphysics.models;

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

// Need: LevelChunk -> BlockEntity

final class ClonedLevelChunk extends ChunkAccess {

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
        return this.blockEntities.get(blockPos);
    }

    @Override
    public BlockState getBlockState(@Nonnull BlockPos blockPos) {
        return this.withLevelChunkSectionAtPosition(blockPos, section -> {
            if (section == null || section.hasOnlyAir()) {
                return Blocks.AIR.defaultBlockState();
            }

            return section.getBlockState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
        });
    }

    @Override
    public FluidState getFluidState(@Nonnull BlockPos blockPos) {
        return this.withLevelChunkSectionAtPosition(blockPos, section -> {
            if (section == null || section.hasOnlyAir()) {
                return Fluids.EMPTY.defaultFluidState();
            }

            return section.getFluidState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
        });
    }

    private <ReturnType> ReturnType withLevelChunkSectionAtPosition(BlockPos blockPos, Function<LevelChunkSection, ReturnType> block) {
        try {
            int sectionIndex = this.getSectionIndex(blockPos.getY());

            if (sectionIndex >= 0 && sectionIndex < this.sections.length) {
                LevelChunkSection section = this.sections[sectionIndex];
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
    public void addEntity(@Nonnull Entity entity) {
        // Method is deprecated
        throw new UnsupportedOperationException("Unimplemented method 'addEntity'");
    }

    @Override
    public CompoundTag getBlockEntityNbtForSaving(@Nonnull BlockPos blockPos) {
        throw new UnsupportedOperationException("Unimplemented method 'getBlockEntityNbtForSaving'");
    }

    @Override
    public TickContainerAccess<Block> getBlockTicks() {
        return this.blockTicks;
    }

    @Override
    public TickContainerAccess<Fluid> getFluidTicks() {
        return this.fluidTicks;
    }

    @Override
    public ChunkStatus getStatus() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStatus'");
    }

    @Override
    public TicksToSave getTicksForSerialization() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTicksForSerialization'");
    }

    @Override
    public void removeBlockEntity(@Nonnull BlockPos blockPos) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeBlockEntity'");
    }

    @Override
    public void setBlockEntity(@Nonnull BlockEntity blockEntity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBlockEntity'");
    }

    @Override
    public BlockState setBlockState(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState, boolean unknownFlag) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBlockState'");
    }

}
