import models.Player;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class DataStorageTest extends UnitTest {
  Player bob;
  Player alice;

  @Before
  public void setup() {
    Fixtures.deleteDatabase();
    Fixtures.loadModels("data.yml");
    bob = Player.find("byName", "Bob").first();
    alice = Player.find("byName", "Alice").first();
  }

  @Test
  public void playersExist() {
    assertEquals(2, Player.count());
  }

  @Test
  public void playerName() {
    assertEquals("Bob", bob.name);
    assertEquals("Alice", alice.name);
  }

  @Test
  public void playerHits() {
    assertEquals(0, bob.hits.size());
    assertEquals(2, alice.scores.size());
  }

  @Test
  public void playerScores() {
    assertEquals(2, bob.scores.size());
    assertEquals(2, alice.scores.size());
  }

}
