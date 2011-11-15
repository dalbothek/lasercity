package controllers;

import java.util.Collections;
import java.util.List;

import models.Player;
import models.Player.PlayerComparator;
import models.Player.PlayerComparator.Property;
import play.mvc.Before;
import play.mvc.Controller;
import play.test.Fixtures;

public class Application extends Controller {

  @Before
  public static void loadData() {
    Fixtures.deleteDatabase();
    Fixtures.loadModels("actual_data.yml");
  }

  public static void index() {
    List<Player> players = Player.<Player> findAll();
    Collections.sort(players, new PlayerComparator(Property.IMBANESS));
    render(players);
  }

}
