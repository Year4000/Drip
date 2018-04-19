/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.protection;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import net.year4000.drip.Constants;
import net.year4000.drip.Drip;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.Utils;
import net.year4000.utilities.tuple.Pair;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.data.Has;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Plugin(
    id = "net_year4000_drip_protection",
    name = "Protect.Drip",
    version = Constants.GIT_HASH,
    dependencies = {@Dependency(id = "net_year4000_utilities")},
    description = "Protect all the things without a database.",
    url = "https://www.year4000.net",
    authors = {"ewized"}
)
public final class Protection extends Drip {
    /** The internal protection manager */
    private ProtectionManager manager;
    private static final Text SIGN_HEADER = Text.of("[Protect]");
    private static final Set<BlockType> BLOCK_TYPES = ImmutableSet.of(
            BlockTypes.FENCE,
            BlockTypes.FENCE_GATE,
            BlockTypes.SPRUCE_FENCE,
            BlockTypes.SPRUCE_FENCE_GATE,
            BlockTypes.BIRCH_FENCE,
            BlockTypes.BIRCH_FENCE_GATE,
            BlockTypes.DARK_OAK_FENCE,
            BlockTypes.DARK_OAK_FENCE_GATE,
            BlockTypes.ACACIA_FENCE,
            BlockTypes.ACACIA_FENCE_GATE,
            BlockTypes.JUNGLE_FENCE,
            BlockTypes.JUNGLE_FENCE_GATE,
            BlockTypes.NETHER_BRICK_FENCE
    );

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
        return instance(Protection.class);
    }

    @Listener
    public void on(ChangeBlockEvent.Place event, @First @Has(SneakingData.class) Player player) {
        BlockSnapshot block = event.getTransactions().get(0).getFinal();

        // Only if placed block is a wall sign
        if (!block.getState().getType().equals(BlockTypes.WALL_SIGN)) return;

        // Only when player is sneaking
        if (!player.getValue(Keys.IS_SNEAKING).get().get()) return;

        // Set the text of the sign
        block.getLocation().get().getTileEntity().ifPresent(entity -> {
            List<Text> lines = Arrays.asList(SIGN_HEADER, Text.of(player.getName()));
            entity.offer(Keys.SIGN_LINES, lines);
        });
    }

    @Listener
    public void on(ChangeBlockEvent.Break event, @First Player player) {
        BlockSnapshot block = event.getTransactions().get(0).getFinal();
        Pair<Vector2i, BlockState[][]> flat = flatten(block.getLocation().get());
        /*player.sendMessage(Text.of("-----"));
        for (int i = 0; i < 16; i++) {
            String line = Stream.of(flat.b.get()[i])
                    .map(state -> {
                        if (state != null && BLOCK_TYPES.contains(state.getType())) {
                            return "X";
                        }

                        return "-";
                    })
                    .collect(Collectors.joining());
            player.sendMessage(Text.of(i % 2 == 0 ? TextColors.GREEN : TextColors.DARK_GREEN, line));
        }*/
    }

    /** Convert a list of Pairs for BlockState */
    public Set<Vector3i> points(Pair<Vector3i, List<BlockState[][]>> blockStates) {
        ImmutableSet.Builder<Vector3i> points = ImmutableSet.builder();

        for (BlockState[][] chunk : blockStates.b.get()) {
            for (int x = 0; x < chunk.length; x++) {
                for (int z = 0; z < chunk[x].length; z++) {
                    if (chunk[x][z] != null) {
                        // todo
                    }
                }
            }
        }

        return points.build();
    }

    /** Flatten a chunk into a BlockState array */
    public Pair<Vector2i, BlockState[][]> flatten(Location<World> world) {
        BlockState[][] flat = new BlockState[16][16];
        Vector3i pos = world.getChunkPosition();
        final int chunkWidth = 16;
        final int chunkHeight = 256;

        for (int y = 0; y < chunkHeight; y++) {
            for (int[] x = {pos.getX() << 4, 0}; x[1] < chunkWidth; x[0]++, x[1]++ ) {
                for (int[] z = {pos.getZ() << 4, 0}; z[1] < chunkWidth; z[0]++, z[1]++ ) {
                    BlockState state = world.getExtent().getBlock(x[0], y, z[0]);

                    if (BLOCK_TYPES.contains(state.getType())) {
                        flat[x[1]][z[1]] = state;
                    }
                }
            }
        }

        return new Pair<>(new Vector2i(pos.getX(), pos.getZ()), flat);
    }

    /** Get the protection service or return the default one, if both fail throw an exception */
    public ProtectionService getProtectionService() {
        Conditions.nonNull(manager, "Protection manager has not been loaded yet.");
        return services().provide(ProtectionService.class).orElse(manager);
    }

    /** Register listeners based on ProtectionService implementation */
    private void registerListeners() {
        if (getProtectionService() instanceof ProtectionManager) {
            // register custom listener for plugin
        }
        // register region listeners
    }

    @Override
    public String toString() {
        return Utils.toString(this);
    }
}
