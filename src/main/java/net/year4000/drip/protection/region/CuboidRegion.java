package net.year4000.drip.protection.region;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import net.year4000.utilities.ObjectHelper;

import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.Set;

/** Represents a cuboid region with two positions */
public final class CuboidRegion implements Region {
    private Vector3i pointOne;
    private Vector3i pointTwo;
    // Use a soft reference here to save memory when it is needed
    private transient SoftReference<Set<Vector3i>> points;

    public CuboidRegion(Vector3i pointOne, Vector3i pointTwo) {
        this.pointOne = ObjectHelper.nonNull(pointOne, "pointOne");
        this.pointTwo = ObjectHelper.nonNull(pointTwo, "pointTwo");
    }

    /** Generate the points of the cuboid region */
    private Set<Vector3i> generatePoint() {
        ImmutableSet.Builder<Vector3i> points = ImmutableSet.builder();
        Vector3i min = pointOne.min(pointTwo);
        Vector3i max = pointOne.max(pointTwo);
        for (int y = min.getY(); y < max.getY(); y++) {
            for (int x = min.getX(); x < max.getX(); x++) {
                for (int z = min.getZ(); z < max.getZ(); z++) {
                    points.add(new Vector3i(x, y, z));
                }
            }
        }
        return points.build();
    }

    @Override
    public Optional<Set<Vector3i>> getPoints() {
        if (points == null || points.get() == null) {
            points = new SoftReference<>(generatePoint());
        }
        return Optional.of(points.get());
    }

    @Override
    public boolean contains(Vector3i vector3i) {
        Vector3i min = pointOne.min(pointTwo);
        Vector3i max = pointOne.max(pointTwo);
        boolean x = vector3i.getX() >= min.getX() && vector3i.getX() <= max.getX();
        boolean y = vector3i.getY() >= min.getY() && vector3i.getY() <= max.getY();
        boolean z = vector3i.getZ() >= min.getZ() && vector3i.getZ() <= max.getZ();
        return x && y && z;
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
