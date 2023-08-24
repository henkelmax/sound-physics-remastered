package com.sonicether.soundphysics.config.blocksound;

import com.sonicether.soundphysics.config.SoundTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SoundType;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockSoundTypeDefinition extends BlockDefinition {

    private final SoundType soundType;

    public BlockSoundTypeDefinition(SoundType soundType) {
        this.soundType = soundType;
    }

    @Override
    public String getConfigString() {
        return SoundTypes.getName(soundType);
    }

    @Override
    @Nullable
    public String getConfigComment() {
        return getName().getString();
    }

    @Override
    public Component getName() {
        return SoundTypes.getNameComponent(soundType).append(Component.literal(" (Sound Type)"));
    }

    public SoundType getSoundType() {
        return soundType;
    }

    @Nullable
    public static BlockSoundTypeDefinition fromConfigString(String configString) {
        SoundType soundType = SoundTypes.getSoundType(configString);
        if (soundType == null) {
            return null;
        }
        return new BlockSoundTypeDefinition(soundType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockSoundTypeDefinition that = (BlockSoundTypeDefinition) o;
        return Objects.equals(soundType, that.soundType);
    }

    @Override
    public int hashCode() {
        return soundType != null ? soundType.hashCode() : 0;
    }
}
