/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.drip.protection;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFutureTask;
import net.year4000.drip.Constants;
import net.year4000.drip.Drip;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.Utils;
import net.year4000.utilities.sponge.Utilities;
import net.year4000.utilities.sponge.protocol.PacketListener;
import net.year4000.utilities.sponge.protocol.PacketType;
import net.year4000.utilities.sponge.protocol.PacketTypes;
import net.year4000.utilities.sponge.protocol.Packets;
import net.year4000.utilities.tuple.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.data.Has;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Plugin(
    id = "protection",
    name = "Protection",
    version = Constants.GIT_HASH,
    dependencies = {@Dependency(id = "drip")},
    description = "Protect all the things without a database.",
    url = "https://www.year4000.net",
    authors = {"ewized"}
)
public final class Protection extends Drip {
    /** The internal protection manager */
    private ProtectionManager manager;
    private static final int CHUNK_WIDTH = 16;
    private static final int CHUNK_HEIGHT = 256;
    private static final Text SIGN_HEADER = Text.of("[Protect]");
    private static final Set<Direction> BLOCK_DIRECTIONS = ImmutableSet.of(
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST
    );
    /** Black list blocks that we know are not fences that have large quantise in the world */
    private static final Set<BlockType> BLOCK_TYPES_BLACKLIST = ImmutableSet.of(
            BlockTypes.AIR,
            BlockTypes.STONE,
            BlockTypes.DIRT,
            BlockTypes.GRASS,
            BlockTypes.SAND,
            BlockTypes.NETHERRACK,
            BlockTypes.WATER,
            BlockTypes.LAVA,
            BlockTypes.END_STONE,
            BlockTypes.BEDROCK
    );
    private static final Set<String> BLOCK_TYPES = ImmutableSet.of(
            "fence",
            "wall"
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

    final Map<UUID, Vector3i> placedSigns = Maps.newHashMap();

    @Listener
    public void on(ChangeSignEvent event, @First Player player) {
        Vector3i position = placedSigns.remove(player.getUniqueId());
        if (position != null) {
            Conditions.condition(position.equals(event.getTargetTile().getLocation().getBlockPosition()), "This should never happen");
            // todo use the text from the event to do something with it
            event.setCancelled(true);
        }
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
            if (entity.offer(Keys.SIGN_LINES, lines).isSuccessful()) {
                placedSigns.put(player.getUniqueId(), block.getPosition());
            }
        });
    }

    @Listener
    public void onInteract(InteractBlockEvent.Primary event, @First Player player) {
        // debug for users to check if the block is protected
        if (player.get(Keys.IS_SNEAKING).isPresent() && player.get(Keys.IS_SNEAKING).get()) {
            if (player.getItemInHand(HandTypes.MAIN_HAND).get().getType() == ItemTypes.SIGN) {
                player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.GRAY, "This block is protected by: ", TextStyles.ITALIC, player.getName()));
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void on(ChangeBlockEvent.Break event, @First Player player) {
        // other stuff
        BlockSnapshot block = event.getTransactions().get(0).getFinal();
        //Pair<Vector2i, BlockState[][]> flat = flatten(block.getLocation().get());
        Pair<Vector2i, BlockSnapshot[][]> flat = flattenLocation(block.getLocation().get());
        player.sendMessage(Text.of("-----"));
        // todo check data of the block we are breaking as it may be part of the fence
        for (int i = 0; i < 16; i++) {
            String line = Stream.of(flat.b.get()[i])
                    .map(snapshot -> {
                        if (snapshot != null && isFenceBlock(snapshot.getState())) {
                            if (isConnected(snapshot.getLocation())) {
                                return "C";
                            }
                            return "X";
                        }

                        return "-";
                    })
                    .collect(Collectors.joining());
            player.sendMessage(Text.of(i % 2 == 0 ? TextColors.GREEN : TextColors.DARK_GREEN, line));
        }
    }

  private Map<Vector3i, ChunkGraph> regions = Maps.newHashMap();


  @Listener
    public void on(LoadChunkEvent event) {
      Chunk chunk = event.getTargetChunk();
      consumerOnChunk(chunk, blockState -> {
        if (isFenceBlock(blockState)) {
            // todo generate chunk graph
          //System.out.println(chunk.getPosition());
        }
      });
    }

    public void consumerOnChunk(Chunk chunk, Consumer<BlockState> consumer) {
      Vector3i pos = chunk.getPosition();

      for (int y = 0; y < CHUNK_HEIGHT; y++) {
        for (int[] x = {pos.getX() << 4, 0}; x[1] < CHUNK_WIDTH; x[0]++, x[1]++ ) {
          for (int[] z = {pos.getZ() << 4, 0}; z[1] < CHUNK_WIDTH; z[0]++, z[1]++ ) {
            consumer.accept(chunk.getBlock(x[0], y, z[0]));
          }
        }
      }
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

    /** Make sure the fence block is connected to multiple ajecent fence block */
    public boolean isConnected(Location<World> location) {
        int count = 2;
        int adjacent = 0;
        for (Direction direction : BLOCK_DIRECTIONS) {
            if (isFenceBlock(location.getBlockRelative(direction).getBlock())) {
                if (++adjacent >= count) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Make sure the fence block is connected to multiple ajecent fence block */
    public boolean isConnected(Optional<Location<World>> optionalLocation) {
        return optionalLocation.isPresent() && isConnected(optionalLocation.get());
    }

    /** Check if the block state is one that should be a boundary block for the region */
    public boolean isFenceBlock(BlockState blockState) {
        return isFenceBlock(blockState.getType());
    }

    /** Check if the block state is one that should be a boundary block for the region */
    public boolean isFenceBlock(BlockType blockType) {
        String blockTypeName = blockType.getName().toLowerCase();
        if (BLOCK_TYPES_BLACKLIST.contains(blockType)) { // if we know its blacklisted don't search
            return false;
        }
        for (String type : BLOCK_TYPES) {
            if (blockTypeName.contains(type)) {
                return true;
            }
        }
        return false;
    }

    /** Flatten a chunk into a BlockState array */
    public Pair<Vector2i, BlockState[][]> flatten(Location<World> world) {
        BlockState[][] flat = new BlockState[16][16];
        Vector3i pos = world.getChunkPosition();

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int[] x = {pos.getX() << 4, 0}; x[1] < CHUNK_WIDTH; x[0]++, x[1]++ ) {
                for (int[] z = {pos.getZ() << 4, 0}; z[1] < CHUNK_WIDTH; z[0]++, z[1]++ ) {
                    BlockState state = world.getExtent().getBlock(x[0], y, z[0]);

                    if (isFenceBlock(state)) {
                        flat[x[1]][z[1]] = state;
                    }
                }
            }
        }

        return new Pair<>(new Vector2i(pos.getX(), pos.getZ()), flat);
    }

    /** Flatten a chunk into a BlockState array */
    public Pair<Vector2i, BlockSnapshot[][]> flattenLocation(Location<World> world) {
        BlockSnapshot[][] flat = new BlockSnapshot[16][16];
        Vector3i pos = world.getChunkPosition();

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int[] x = {pos.getX() << 4, 0}; x[1] < CHUNK_WIDTH; x[0]++, x[1]++ ) {
                for (int[] z = {pos.getZ() << 4, 0}; z[1] < CHUNK_WIDTH; z[0]++, z[1]++ ) {
                    Vector3i position = new Vector3i(x[0], y, z[0]);
                    BlockSnapshot snapshot = world.copy().setBlockPosition(position).createSnapshot();
                    if (isFenceBlock(snapshot.getState())) {
                        flat[x[1]][z[1]] = snapshot;
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
