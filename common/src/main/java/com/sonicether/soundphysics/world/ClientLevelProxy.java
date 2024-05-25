package com.sonicether.soundphysics.world;

import net.minecraft.world.level.BlockGetter;

/**
 * Convenience type to use either a full `ClientLevel` or a `ClonedClientLevel`
 * with the same sparse operational interface.
 * <p>
 * Offers access to block states, fluid states, block entities, and height data.
 * Allows performing non-mutating in-line checks, clipping, and traversal.
 *
 * @author Saint (@augustsaintfreytag)
 */
public interface ClientLevelProxy extends BlockGetter {
}
