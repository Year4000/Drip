/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.protection;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.util.Identifiable;

import java.util.Set;

/** The default ProtectionManager that handles all the protection methods */
public final class ProtectionManager implements ProtectionService {
    @Override
    public Set<ProtectedRegion> findRegions(Vector3i vector3i) {
        return ImmutableSet.of();
    }

    @Override
    public Set<ProtectedRegion> findRegions(Identifiable owner) {
        return ImmutableSet.of();
    }
}
