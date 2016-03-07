/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip;

import net.year4000.utilities.utils.UtilityConstructError;

public final class Constants {

    private Constants() {
        UtilityConstructError.raise();
    }

    /** The version of Drip */
    public static final String VERSION = "${version}";

    /** The git hash for this version of Drip */
    public static final String GIT_HASH = "${git_hash}";

    /** The full version of Drip contains version and git hash */
    public static final String FULL_VERSION = "${version}-${git_hash}";
}
