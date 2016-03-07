/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip;

import com.google.inject.Inject;
import net.year4000.utilities.sponge.AbstractSpongePlugin;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ServiceManager;

@Plugin(id = "drip", name = "Drip", version = Constants.FULL_VERSION, dependencies = "after:utilities")
public class Drip extends AbstractSpongePlugin {
    @Inject
    protected Game game;

    /** Get the instance of this plugin */
    public static Drip get() {
        return instance();
    }

    /** Register the service with sponge, then return the provider */
    protected <P> P registerService(Class<? super P> clazz, P object) {
        services().setProvider(this, clazz, object);
        return object;
    }

    /** Gets the ServiceManager */
    protected ServiceManager services() {
        return game.getServiceManager();
    }
}
