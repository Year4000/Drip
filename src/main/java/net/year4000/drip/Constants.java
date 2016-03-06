/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip;

public final class Constants {

    private Constants() {}

    /** The version of Drip */
    public static final String VERSION = "${version}";

    /** The git hash for this version of Drip */
    public static final String GIT_HASH = "${git_hash}";

    /** The full version of Drip contains version and git hash */
    public static final String FULL_VERSION = "${version}-${git_hash}";
}
