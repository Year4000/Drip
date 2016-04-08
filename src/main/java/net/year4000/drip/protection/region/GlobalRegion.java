package net.year4000.drip.protection.region;

import com.flowpowered.math.vector.Vector3i;
import net.year4000.utilities.ObjectHelper;

import java.util.Optional;
import java.util.Set;

/** A region that contains everything */
public final class GlobalRegion implements Region {
    @Override
    public Optional<Set<Vector3i>> getPoints() {
        return Optional.empty();
    }

    @Override
    public boolean contains(Vector3i vector3i) {
        return true;
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
