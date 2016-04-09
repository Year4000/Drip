/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.protection.region;

import com.flowpowered.math.vector.Vector3i;
import net.year4000.utilities.ObjectHelper;

import java.util.Optional;
import java.util.Set;

/** Represents a cube region that wraps a cuboid region */
public final class CubeRegion implements Region {
    private CuboidRegion region;

    /** Create a cube region with a center value and height radius and width radius */
    public CubeRegion(Vector3i center, int height, int width) {
        ObjectHelper.nonNull(center, "center");
        ObjectHelper.isLarger(height, 0);
        ObjectHelper.isLarger(width, 0);
        Vector3i posOne = new Vector3i(center.getX() - width, center.getY() - height, center.getZ() - width);
        Vector3i posTwo = new Vector3i(center.getX() + width, center.getY() + height, center.getZ() + width);
        this.region = new CuboidRegion(posOne, posTwo);
    }

    /** Create a cube region with a center value and radius */
    public CubeRegion(Vector3i center, int radius) {
        this(center, radius, radius);
    }

    @Override
    public Optional<Set<Vector3i>> getPoints() {
        return region.getPoints();
    }

    @Override
    public boolean contains(Vector3i vector3i) {
        return region.contains(vector3i);
    }

    @Override
    public boolean equals(Object other) {
        return ObjectHelper.equals(this, other);
    }

    @Override
    public int hashCode() {
        return ObjectHelper.hashCode(this);
    }

    @Override
    public String toString() {
        return ObjectHelper.toString(this);
    }
}
