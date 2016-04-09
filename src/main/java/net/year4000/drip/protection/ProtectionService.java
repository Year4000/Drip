/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.protection;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.util.Identifiable;

import java.util.Set;

/** The Protection service that will handle the protection of the server */
public interface ProtectionService {
    /** Query all the regions found at the specific point */
    Set<ProtectedRegion> findRegions(Vector3i vector3i);

    /** Get all the regions owned by the identifiable */
    Set<ProtectedRegion> findRegions(Identifiable owner);
}
