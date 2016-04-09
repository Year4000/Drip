/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.protection;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import net.year4000.drip.protection.region.AbstractComplexRegion;
import net.year4000.drip.protection.region.Region;
import net.year4000.utilities.ObjectHelper;
import org.spongepowered.api.util.Identifiable;

import java.util.Optional;
import java.util.Set;

/** A region that is owned by a owner */
public class ProtectedRegion extends AbstractComplexRegion {
    private Identifiable owner;
    private Set<Region> regions;

    public ProtectedRegion(Identifiable owner, Region... regions) {
        this.owner = ObjectHelper.nonNull(owner, "owner");
        this.regions = ImmutableSet.copyOf(regions);
    }

    @Override
    protected ImmutableSet<Vector3i> generatePoints() {
        ImmutableSet.Builder<Vector3i> points = ImmutableSet.builder();
        regions.stream()
            .map(Region::getPoints)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(points::addAll);
        return points.build();
    }

    @Override
    public Optional<Set<Vector3i>> getPoints() {
        for (Region region : regions) {
            if (!region.getPoints().isPresent()) return Optional.empty();
        }
        return Optional.of(points());
    }

    @Override
    public boolean contains(Vector3i vector3i) {
        for (Region region : regions) {
            if (!region.contains(vector3i)) return false;
        }
        return regions.size() > 0;
    }

    /** Is the selected user the owner of this region */
    public boolean isOwner(Identifiable user) {
        return owner.getUniqueId().equals(user.getUniqueId());
    }
}
