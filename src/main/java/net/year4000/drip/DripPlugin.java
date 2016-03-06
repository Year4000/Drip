/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip;

import net.year4000.utilities.sponge.AbstractSpongePlugin;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "drip", name = "Drip", version = Constants.FULL_VERSION)
public class DripPlugin extends AbstractSpongePlugin {

    /** Get the instance of this plugin */
    public static DripPlugin get() {
        return instance();
    }
}
