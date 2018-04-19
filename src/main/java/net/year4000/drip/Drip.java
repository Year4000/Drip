/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip;

import com.google.inject.Inject;
import net.year4000.utilities.sponge.AbstractSpongePlugin;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ServiceManager;

@Plugin(
    id = "net_year4000_drip",
    name = "Drip",
    version = Constants.FULL_VERSION,
    dependencies = {@Dependency(id = "net_year4000_utilities")},
    description = "A core system for drip modules.",
    url = "https://www.year4000.net",
    authors = {"ewized"}
)
public class Drip extends AbstractSpongePlugin {
    @Inject
    protected Game game;

    /** Get the instance of this plugin */
    public static Drip get() {
        return instance(Drip.class);
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
