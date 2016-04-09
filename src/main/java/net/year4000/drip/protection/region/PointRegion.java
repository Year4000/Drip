/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.protection.region;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import net.year4000.utilities.ObjectHelper;

import java.util.Optional;
import java.util.Set;

/** A region that contains a single point */
public final class PointRegion extends AbstractRegion {
    private Vector3i point;

    /** Allow creation of a PointRegion via x,y,z cords */
    public PointRegion(int x, int y, int z) {
        this(new Vector3i(x, y, z));
    }

    /** Create the region with vector3i point */
    public PointRegion(Vector3i point) {
        this.point = ObjectHelper.nonNull(point, "point");
    }

    @Override
    public boolean contains(Vector3i vector3i) {
        return point.equals(vector3i);
    }

    @Override
    public Optional<Set<Vector3i>> getPoints() {
        return Optional.of(ImmutableSet.of(point));
    }
}
