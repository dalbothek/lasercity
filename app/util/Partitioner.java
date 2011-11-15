package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Partitioner<T extends Partitioner.Partitionable> {
  public interface Partitionable {
    public float partitionWorth();
  }

  public static <T extends Partitioner.Partitionable> Partition part(Collection<T> items) {
    Partitioner<T> partitioner = new Partitioner<T>(items);
    partitioner.part();
    return partitioner.partition();
  }

  private Collection<T> items;
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
  }

  private Partition partition() {
    return bestPartitions;
  }

  private void part() {
    if (items.size() == 0) {
      bestPartitions = new Partition<T>();
      bestDifference = 0;
      return;
    }
    List<Partition<T>> partitions = new ArrayList<Partition<T>>(n);
    for (T t : items) {
      partitions.add(new Partition<T>(t));
    }
    Collections.sort(partitions);
    bldm(partitions);
    System.out.println("steps(" + n + ") : " + steps);
  }

  private void bldm(Collection<Partition<T>> partitions) {
    steps++;
    int k = partitions.size();
    if (k == 1) {
      Partition<T> leaf = partitions.iterator().next();
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
      if (k <= n2) {
        ArrayList<Partition<T>> sortedPartitions;
        partitions = sortedPartitions = new ArrayList<Partition<T>>(partitions);
        Collections.sort(sortedPartitions);
      }
      Collection<Partition<T>> left = new ArrayList<Partition<T>>(k - 1);
      Collection<Partition<T>> right = new ArrayList<Partition<T>>(k - 1);
      Partition combine1 = null;
      Partition combine2 = null;
      for (Partition partition : partitions) {
        if (combine1 == null) {
          combine1 = partition;
        } else if (combine2 == null) {
          combine2 = partition;
        } else {
          left.add(partition);
          right.add(partition);
        }
      }
      left.add(combine1.combine(combine2));
      right.add(combine1.combineReverse(combine2));
      bldm(left);
      bldm(right);
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

  public static class Partition<T extends Partitioner.Partitionable> implements Comparable<Partition<T>> {
    public final Collection<T> left;
    public final Collection<T> right;
    public final int m;
    public final float difference;

    public Partition() {
      this(Collections.<T> emptySet(), Collections.<T> emptySet());
    }

    public Partition(T item) {
      this(createCollection(item), Collections.<T> emptySet());
    }

    private Partition(Collection<T> left, Collection<T> right) {
      this.left = left;
      this.right = right;

      m = Math.abs(left.size() - right.size());

      float tmpDiff = 0;
      for (T t : left) {
        tmpDiff += t.partitionWorth();
      }
      for (T t : right) {
        tmpDiff -= t.partitionWorth();
      }
      difference = tmpDiff;
    }

    private static <T extends Partitioner.Partitionable> Collection<T> createCollection(T item) {
      Collection<T> tmpLeft = new ArrayList<T>(1);
      tmpLeft.add(item);
      return tmpLeft;
    }

    public Partition<T> combine(Partition<T> partition) {
      return combine(partition.left, partition.right);
    }

    public Partition<T> combineReverse(Partition<T> partition) {
      return combine(partition.right, partition.left);
    }

    private Partition<T> combine(Collection<T> otherLeft, Collection<T> otherRight) {
      Collection<T> newLeft = new ArrayList<T>(left.size() + otherLeft.size());
      Collection<T> newRight = new ArrayList<T>(right.size() + otherRight.size());
      newLeft.addAll(left);
      newLeft.addAll(otherLeft);
      newRight.addAll(right);
      newRight.addAll(otherRight);
      return new Partition<T>(newLeft, newRight);
    }

    @Override
    public int compareTo(Partition<T> other) {
      return Float.valueOf(other.difference).compareTo(difference);
    }
  }
}
