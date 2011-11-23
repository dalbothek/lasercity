package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * <code>Partitioner</code> splits a {@link Collection} of objects into two
 * groups such that the worth of both groups is as balanced as possible. The
 * worth of a group is determined by calling the <code>partitionWorth</code>
 * method on each member and adding the results.
 * 
 * @author Simon Marti
 */
public class Partitioner<T extends Partitioner.Partitionable> {
  /**
   * Classes implementing the <code>Partitionable</code> interface can be
   * partitioned by the {@link Partitioner}.
   * 
   */
  public interface Partitionable {
    /**
     * The worth of an object is used by the {@link Partitioner} to balance the
     * groups.
     * 
     * @return the worth of the object
     */
    public float partitionWorth();
  }

  /**
   * Splits a {@link Collection} of objects into two groups such that the worth
   * of both groups is as balanced as possible.
   * 
   * @param items Items to split
   * @return An instance of {@link Partition} containing the two groups
   */
  public static <T extends Partitioner.Partitionable> Partition part(Collection<T> items) {
    Partitioner<T> partitioner = new Partitioner<T>(items);
    partitioner.part();
    return partitioner.partition();
  }

  private Collection<T> items;
  private HashMap<T, Float> worths = new HashMap<T, Float>();
  private float bestDifference = Float.MAX_VALUE;
  private Partition bestPartitions = null;
  private final int n;
  private final int n2;
  private final int m;

  private int steps = 0;

  private Partitioner(Collection<T> items) {
    this.items = items;
    this.n = items.size();
    n2 = n / 2;
    m = n % 2;

    float min = Float.MAX_VALUE;
    for (T item : items) {
      float worth = item.partitionWorth();
      worths.put(item, worth);
      if (worth < min) {
        min = worth;
      }
    }
    if (min < 0) {
      for (T item : items) {
        worths.put(item, worths.get(item) - min);
      }
    }
  }

  private Partition partition() {
    return bestPartitions;
  }

  private void part() {
    if (items.size() == 0) {
      bestPartitions = new Partition<T>(worths);
      bestDifference = 0;
      return;
    }
    List<Partition<T>> partitions = new ArrayList<Partition<T>>();
    for (T t : items) {
      partitions.add(new Partition<T>(t, worths));
    }
    Collections.sort(partitions);
    bldm(partitions);
  }

  private void bldm(List<Partition<T>> partitions) {
    steps++;
    int k = partitions.size();
    if (k == 1) {
      Partition<T> leaf = partitions.get(0);
      if (leaf.m <= m && leaf.difference < bestDifference) {
        bestDifference = leaf.difference;
        bestPartitions = leaf;
      }
    } else {
      float maxDifference = maxDifference(partitions);
      float sumDifference = sumDifference(partitions);
      if (2 * maxDifference - sumDifference >= bestDifference) {
        return;
      }
      int maxm = maxm(partitions);
      int summ = summ(partitions);
      if (2 * maxm - summ > m || summ < m) {
        return;
      }
      if (k == n2) {
        Collections.sort(partitions);
      }
      Partition left = partitions.remove(0);
      Partition right = partitions.remove(0);
      Partition combined = left.combine(right);
      add(partitions, combined, k);
      bldm(partitions);
      partitions.remove(combined);
      combined = left.combineReverse(right);
      add(partitions, combined, k);
      bldm(partitions);
      partitions.remove(combined);
      partitions.add(0, right);
      partitions.add(0, left);
    }
  }

  private void add(List<Partition<T>> partitions, Partition<T> partition, int k) {
    if (k > n2) {
      partitions.add(partition);
    } else {
      for (int i = 0; i < partitions.size(); i++) {
        if (partitions.get(i).difference < partition.difference) {
          partitions.add(i, partition);
          return;
        }
      }
      partitions.add(partition);
    }
  }

  private float sumDifference(Collection<Partition<T>> partitions) {
    float sum = 0;
    for (Partition partition : partitions) {
      sum += partition.difference;
    }
    return sum;
  }

  private int summ(Collection<Partition<T>> partitions) {
    int sum = 0;
    for (Partition partition : partitions) {
      sum += partition.m;
    }
    return sum;
  }

  private int maxm(Collection<Partition<T>> partitions) {
    int best = 0;
    for (Partition partition : partitions) {
      if (best < partition.m) {
        best = partition.m;
      }
    }
    return best;
  }

  private float maxDifference(Collection<Partition<T>> partitions) {
    float best = 0;
    for (Partition partition : partitions) {
      if (best < partition.difference) {
        best = partition.difference;
      }
    }
    return best;
  }

  /**
   * Represents an optimal partition computed by {@link Partitioner}.
   */
  public static class Partition<T extends Partitioner.Partitionable> implements Comparable<Partition<T>> {
    public final Collection<T> left;
    public final Collection<T> right;
    public final int m;
    public final float difference;
    HashMap<T, Float> worths;

    private Partition(HashMap<T, Float> worths) {
      this(Collections.<T> emptySet(), Collections.<T> emptySet(), worths);
    }

    private Partition(T item, HashMap<T, Float> worths) {
      this(createCollection(item), Collections.<T> emptySet(), worths);
    }

    private Partition(Collection<T> left, Collection<T> right, HashMap<T, Float> worths) {
      this.left = left;
      this.right = right;
      this.worths = worths;

      m = Math.abs(left.size() - right.size());

      float tmpDiff = 0;
      for (T t : left) {
        tmpDiff += worths.get(t);
      }
      for (T t : right) {
        tmpDiff -= worths.get(t);
      }
      difference = tmpDiff;
    }

    Partition(Collection<T> left, Collection<T> right, int m, float difference) {
      this.left = left;
      this.right = right;
      this.m = m;
      this.difference = difference;
    }

    private static <T extends Partitioner.Partitionable> Collection<T> createCollection(T item) {
      Collection<T> tmpLeft = new ArrayList<T>(1);
      tmpLeft.add(item);
      return tmpLeft;
    }

    private Partition<T> combine(Partition<T> partition) {
      return combine(partition.left, partition.right);
    }

    private Partition<T> combineReverse(Partition<T> partition) {
      return combine(partition.right, partition.left);
    }

    private Partition<T> combine(Collection<T> otherLeft, Collection<T> otherRight) {
      Collection<T> newLeft = new ArrayList<T>(left.size() + otherLeft.size());
      Collection<T> newRight = new ArrayList<T>(right.size() + otherRight.size());
      newLeft.addAll(left);
      newLeft.addAll(otherLeft);
      newRight.addAll(right);
      newRight.addAll(otherRight);
      return new Partition<T>(newLeft, newRight, worths);
    }

    @Override
    public int compareTo(Partition<T> other) {
      return Float.compare(other.difference, difference);
    }
  }
}
