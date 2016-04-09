/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import net.year4000.drip.protection.ProtectedRegion;
import net.year4000.drip.protection.region.CubeRegion;
import net.year4000.drip.protection.region.GlobalRegion;
import net.year4000.drip.protection.region.SphereRegion;
import org.junit.Assert;
import org.junit.Test;
import org.spongepowered.api.util.Identifiable;

import java.util.UUID;

@SuppressWarnings("ALL")
public class ProtectedRegionTest {
    /** A dummy class to represent an identifiable */
    public static class Self implements Identifiable {
        private final UUID uuid = UUID.randomUUID();

        @Override
        public UUID getUniqueId() {
            return uuid;
        }
    }

    private static final Self OWNER = new Self();
    private static final Self USER  = new Self();


    @Test
    public void ownerRegionTest() {
        final SphereRegion sphere = new SphereRegion(Vector3i.ZERO, 10);
        final CubeRegion cube = new CubeRegion(new Vector3i(0, -10, 0), 10);
        ProtectedRegion region = new ProtectedRegion(OWNER, sphere, cube);
        System.out.println(region);
        Assert.assertTrue(region.isOwner(OWNER));
        Assert.assertFalse(region.isOwner(USER));
    }

    @Test
    public void globalRegionTest() {
        ProtectedRegion region = new ProtectedRegion(OWNER, new GlobalRegion());
        System.out.println(region);
        // The region can not contain dupe points
        Assert.assertTrue(region.contains(Vector3i.ZERO));
        Assert.assertFalse(region.getPoints().isPresent());
    }

    @Test
    public void definedRegionTest() {
        final SphereRegion sphere = new SphereRegion(Vector3i.ZERO, 10);
        final CubeRegion cube = new CubeRegion(new Vector3i(0, -10, 0), 10);
        ProtectedRegion region = new ProtectedRegion(OWNER, sphere, cube);
        System.out.println(region);
        // The region can not contain dupe points
        Assert.assertTrue(region.contains(Vector3i.ZERO));
        Assert.assertTrue(region.getPoints().isPresent());
        final int size = ImmutableSet.builder()
            .addAll(sphere.getPoints().get())
            .addAll(cube.getPoints().get())
            .build().size();
        Assert.assertEquals(size, region.getPoints().get().size());
    }
}
