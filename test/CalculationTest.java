import java.util.ArrayList;

import models.Game;
import models.Player;
import models.Score;
import models.Team;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class CalculationTest extends UnitTest {
  Player bob;
  Player alice;
  private ArrayList<Game> games;

  @Before
  public void setup() {
    Fixtures.deleteDatabase();
    Fixtures.loadModels("data.yml");
    bob = Player.find("byName", "Bob").first();
    alice = Player.find("byName", "Alice").first();
    games = new ArrayList<Game>(Game.<Game> findAll());
  }

  @Test
  public void playerCount() {
    assertEquals(2, games.get(0).playerCount());
  }

  @Test
  public void shotsFired() {
    assertEquals(5, bob.shotCount());
    assertEquals(0, alice.shotCount());
  }

  @Test
  public void imbaness() {
    float total = 0;
    for (Team team : games.get(0).teams) {
      for (Score score : team.scores) {
        total += score.imbaness();
      }
    }
    assertEquals(games.get(0).playerCount(), total, 0.1);

    assertTrue(bob.imbaness() > 0);
    assertTrue(bob.imbaness() < 10);
  }
}
