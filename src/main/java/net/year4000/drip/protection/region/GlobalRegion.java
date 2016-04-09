package net.year4000.drip.protection.region;

import com.flowpowered.math.vector.Vector3i;

import java.util.Optional;
import java.util.Set;

/** A region that contains everything */
public final class GlobalRegion extends AbstractRegion {
    @Override
    public Optional<Set<Vector3i>> getPoints() {
        return Optional.empty();
    }

    @Override
    public boolean contains(Vector3i vector3i) {
        return true;
    }
}
