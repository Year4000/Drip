package net.year4000.drip;

import com.flowpowered.math.vector.Vector3i;
import net.year4000.drip.protection.region.CuboidRegion;
import net.year4000.drip.protection.region.GlobalRegion;
import net.year4000.drip.protection.region.PointRegion;
import org.junit.Assert;
import org.junit.Test;

public class RegionTest {
    @Test
    public void pointRegionTest() {
        PointRegion region = new PointRegion(0, 0, 0);
        System.out.println(region);
        Assert.assertTrue(region.getPoints().isPresent());
        Assert.assertTrue(region.getPoints().get().size() == 1);
        Assert.assertTrue(region.contains(new Vector3i(0, 0, 0)));
        Assert.assertFalse(region.contains(new Vector3i(1, 1, 1)));
    }

    @Test
    public void globalRegionTest() {
        GlobalRegion region = new GlobalRegion();
        System.out.println(region);
        Assert.assertFalse(region.getPoints().isPresent());
        Assert.assertTrue(region.contains(new Vector3i(0,0,0)));
        Assert.assertTrue(region.contains(new Vector3i(1,1,1)));
    }

    @Test
    public void cuboidRegion() {
        CuboidRegion region = new CuboidRegion(new Vector3i(2,2,2), new Vector3i(10,10,10));
        System.out.println(region);
        Assert.assertTrue(region.contains(new Vector3i(5,5,5)));
        Assert.assertFalse(region.contains(new Vector3i(0,0,0)));
        int area = 8 * 8 * 8; // area of the cuboid we are testing
        Assert.assertTrue(region.getPoints().isPresent());
        Assert.assertEquals(area, region.getPoints().get().size());
    }
}
