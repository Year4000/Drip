package net.year4000.drip.protection.region;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import net.year4000.utilities.ObjectHelper;

import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.Set;

/** A region that contains a sphere */
public final class SphereRegion implements Region {
    private Vector3i center;
    private int radius;
    private transient SoftReference<Set<Vector3i>> points;

    public SphereRegion(Vector3i center, int radius) {
        this.center = ObjectHelper.nonNull(center, "center");
        this.radius = ObjectHelper.isLarger(radius, 0);
    }

    /** Generate the points of the sphere region */
    private Set<Vector3i> generatePoint() {
        ImmutableSet.Builder<Vector3i> points = ImmutableSet.builder();
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Vector3i point = new Vector3i(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (contains(point)) {
                        points.add(point);
                    }
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
        return center.distance(vector3i) <= radius;
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
