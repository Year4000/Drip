/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.protection.region;

import net.year4000.utilities.ObjectHelper;

/** The abstract region that handles the creation of points */
public abstract class AbstractRegion implements Region {
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
