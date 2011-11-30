package controllers;

import java.util.Collections;
import java.util.List;

import models.Player;
import models.Player.PlayerComparator;
import models.Player.PlayerComparator.Property;
import play.mvc.Controller;
import play.test.Fixtures;

public class Mobile extends Controller {

  public static void loadData() {
    Fixtures.deleteDatabase();
    Fixtures.loadModels("actual_data.yml");
    view();
  }

  public static void view() {
    List<Player> players = Player.find("byImba", false).<Player> fetch();
    Collections.sort(players, new PlayerComparator(Property.IMBANESS));
    render(players);
  }

}
