/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.protection;

import net.year4000.drip.Constants;
import net.year4000.drip.Drip;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.util.Objects;

@Plugin(id = "protection", name = "Protect.Drip", version = Constants.GIT_HASH, dependencies = "after:drip")
public final class Protection extends Drip {
    /** The internal protection manager */
    private ProtectionManager manager;

    @Listener
    public void on(GamePostInitializationEvent event) {
        // Register the protection manager / service
        manager = registerService(ProtectionService.class, new ProtectionManager());
    }

    @Listener
    public void on(GameAboutToStartServerEvent event) {
        registerListeners();
    }

    /** Grab {@link Protection} instance*/
    public static Protection get() {
        return instance();
    }

    /** Get the protection service or return the default one, if both fail throw an exception */
    public ProtectionService getProtectionService() {
        Objects.requireNonNull(manager, "Protection manager has not been loaded yet.");
        return services().provide(ProtectionService.class).orElse(manager);
    }

    /** Register listeners based on ProtectionService implementation */
    private void registerListeners() {
        if (getProtectionService() instanceof ProtectionManager) {
            // register custom listener for plugin
        }
        // register region listeners
    }
}
