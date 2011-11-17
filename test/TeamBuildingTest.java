import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import play.test.UnitTest;
import util.Partitioner;
import util.Partitioner.Partition;
import benchmark.TeamBuildingBenchmark.Item;

public class TeamBuildingTest extends UnitTest {
  private List<Item> values = new LinkedList<Item>();

  @Test
  public void trivialTests() {
    Partition part = Partitioner.part(values);
    assertEquals(0, part.m);
    assertEquals(0, part.left.size());
    assertEquals(0, part.right.size());
  }

  @Test
  public void simpleTest() {
    values.add(new Item(1));
    Partition part = Partitioner.part(values);
    assertEquals(1, part.m);
    assertEquals(1, Math.abs(part.right.size() - part.left.size()));

    values.add(new Item(2));
    part = Partitioner.part(values);
    assertEquals(0, part.m);
    assertEquals(1, part.right.size());
    assertEquals(1, part.left.size());

    values.add(new Item(3));
    values.add(new Item(10));
    part = Partitioner.part(values);
    assertEquals(0, part.m);
    assertEquals(2, part.right.size());
    assertEquals(2, part.left.size());
    assertEquals(6, part.difference, 0);
  }

  @Test
  public void complexTest() {
    for (int i = 0; i < 16; i++) {
      values.add(new Item(i));
    }
    Partition part = Partitioner.part(values);
    assertEquals(0, part.m);
    assertEquals(0, part.difference, 0);

    for (int i = 0; i < 16; i++) {
      values.add(new Item(i * 11 + 7));
    }
    part = Partitioner.part(values);
    System.out.println(part.m);
    System.out.println(part.difference);
  }
}
