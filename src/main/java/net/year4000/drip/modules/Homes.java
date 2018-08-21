/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.modules;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.SleepingEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Last;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.RespawnLocation;

import java.util.Map;
import java.util.UUID;

@Plugin(id = "homes", description = "Use EnderPearls to teleport to your home location and spawns")
public class Homes {

    //@InjectSettings private Settings<HomeSettings> settings;
    private HomeSettings settings = new HomeSettings();

    @Listener
    public void onEnderTeleport(MoveEntityEvent.Teleport event, @Root Player player, @Last Entity entity) {
        if (entity.getType().equals(EntityTypes.ENDER_PEARL)) {
            Vector3d from = event.getFromTransform().getPosition().mul(1, 0, 1).abs();
            Vector3d to = event.getToTransform().getPosition().mul(1, 0, 1).abs();

            if (to.distanceSquared(from) < settings.get().radius) {
                event.setCancelled(true);

                if (player.get(Keys.IS_SNEAKING).get()) { // Send to the world's spawn
                    player.sendMessage(Text.of("sending to spawn"));
                    player.getCooldownTracker().setCooldown(ItemTypes.ENDER_PEARL, settings.get().spawnDelay);
                    player.setTransform(new Transform<>(event.getFromTransform().getExtent().getSpawnLocation()));
                } else { // Send to the players spawn / bed location
                    player.sendMessage(Text.of("sending to bed"));
                    player.getCooldownTracker().setCooldown(ItemTypes.ENDER_PEARL, settings.get().bedSpawnDelay);
                    System.out.println(player.get(Keys.RESPAWN_LOCATIONS).get().get(player.getWorld().getUniqueId()));
                    player.setTransformSafely(new Transform<>(event.getFromTransform().getExtent().getSpawnLocation()));
                }
            }
        }
    }

    @Listener
    public void onSleeping(SleepingEvent.Pre event, @First Player player) {
        Map<UUID, RespawnLocation> bedLocations = player.get(Keys.RESPAWN_LOCATIONS).get();
        RespawnLocation bedlocation = RespawnLocation.builder()
                .forceSpawn(true)
                .world(player.getWorld())
                .location(event.getBed().getLocation().get())
                .build();
        bedLocations.put(player.getWorld().getUniqueId(), bedlocation);
        System.out.println(player.offer(Keys.RESPAWN_LOCATIONS, bedLocations));
        player.sendMessage(Text.of("Setting your bed spawn"));
    }

    public static class HomeSettings {
        public HomeSettings get() { return this; }

        public int radius = 1;

        public int spawnDelay = 20 * 5;

        public int bedSpawnDelay = 20 * 5;
    }
}
