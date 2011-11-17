package benchmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import util.Partitioner;
import util.Partitioner.Partitionable;

public class TeamBuildingBenchmark {
  public static void main(String[] args) {
    initialize(100);
    smallSet(10000);
    growingSet(500, 16);
    randomSet(2000, 16);
    randomSet(2, 40);
    randomSet(1, 50);
  }

  private static void initialize(int n) {
    for (int i = 0; i < n; i++) {
      Partitioner.part(Collections.EMPTY_LIST);
    }
  }

  private static void smallSet(int n) {
    Timer timer = new Timer();
    for (int i = 0; i < n; i++) {
      Partitioner.part(collection(new int[] { 1, 6, 4, 8, 6, 3 }));
    }
    System.out.println("small set: " + timer.stop() + " ms");
  }

  private static void growingSet(int n, int m) {
    Timer timer = new Timer();
    for (int j = 0; j < n; j++) {
      List<Item> items = new ArrayList<Item>(m);
      for (int i = 0; i < m * j / n; i++) {
        items.add(new Item(i));
        Partitioner.part(items);
      }
    }
    System.out.println("growing set: " + timer.stop() + " ms");
  }

  private static void randomSet(int n, int m) {
    Random rand = new Random("SadiBelai".hashCode());
    Timer timer = new Timer();
    for (int i = 0; i < n; i++) {
      List<Item> items = new ArrayList<Item>(m);
      for (int j = 0; j < m; j++) {
        items.add(new Item(rand.nextInt()));
      }
      Partitioner.part(items);
    }
    System.out.println("random set: " + timer.stop() + " ms");
  }

  public static Collection<Partitionable> collection(int[] is) {
    Collection items = new ArrayList<Item>(is.length);
    for (int i : is) {
      items.add(new Item(i));
    }
    return items;
  }

  private static class Timer {
    long start;

    public Timer() {
      start();
    }

    public void start() {
      start = System.currentTimeMillis();
    }

    public long stop() {
      return System.currentTimeMillis() - start;
    }
  }

  public static class Item implements Partitionable {
    private int value;

    public Item(int value) {
      this.value = value;
    }

    @Override
    public float partitionWorth() {
      return value;
    }
  }
}
