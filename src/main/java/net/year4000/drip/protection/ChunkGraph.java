package net.year4000.drip.protection;

import com.flowpowered.math.vector.Vector2i;
import net.year4000.utilities.Conditions;
import org.spongepowered.api.util.Identifiable;

public class ChunkGraph {
  private static final int MAX = 16;
  private short[] graph = new short[MAX];
  private Identifiable owner;

  public void owner(Identifiable owner) {
    this.owner = Conditions.nonNull(owner, "owner can not be null");
  }

  public void set(Vector2i vector2i) {
    set(vector2i.getX(), vector2i.getY());
  }

  protected void set(int x, int y) {
    graph[x] |= (0x01 << y);
  }

  public void unset(Vector2i vector2i) {
    unset(vector2i.getX(), vector2i.getY());
  }

  protected void unset(int x, int y) {
    graph[x] &= ~(0x01 << y);
  }

  /** Check if the relative position is protected par of this graph */
  public boolean isProtected(int x, int y) {
    return (graph[x] | (0x01 << y)) == graph[x];
  }

  public ChunkGraph merge(ChunkGraph merge) {
    for (int i = 0 ; i < MAX ; i++) {
      graph[i] |= merge.graph[i];
    }
    return this;
  }

  /** Check if the user is an owner of this protected graph */
  public boolean isOwner(Identifiable user) {
    return owner.getUniqueId().equals(user.getUniqueId());
  }

  @Override
  public String toString() {
    StringBuilder value = new StringBuilder("ChunkGraph[\n");
    for (int y = 0 ; y < MAX ; y++) {
      for (int x = 0 ; x < MAX ; x++) {
        value.append(isProtected(x, y) ? "1" : "0");
      }
      value.append(",\n");
    }
    return value.delete(value.length() - 2, value.length() - 1).append("]").toString();
  }

  public static void main(String[] args) {
    ChunkGraph chunkGraph = new ChunkGraph();
    ChunkGraph chunkGraphA = new ChunkGraph();

    for (int i = 0 ; i < MAX ; i++) {
      chunkGraph.set(i, i);
    }

    for (int i = 0 ; i < MAX ; i++) {
      chunkGraphA.set(MAX - 1 - i, i);
    }

    chunkGraph.unset(0,0);

    System.out.println(chunkGraph);
    System.out.println(chunkGraphA);
    System.out.println(chunkGraph.merge(chunkGraphA));
  }
}
