package controllers;

import java.util.Collections;
import java.util.List;

import models.Player;
import models.Player.PlayerComparator;
import models.Player.PlayerComparator.Property;
import play.mvc.Controller;

public class PlayerController extends Controller {
  public static void newPlayer(String name, int imbaness, boolean imba) {
    if (name != null && name.length() > 0) {
      new Player(name, imbaness, imba).save();
    }
    list();
  }

  public static void list() {
    List<Player> players = Player.find("byImba", false).<Player> fetch();
    Collections.sort(players, new PlayerComparator(Property.IMBANESS));
    render(players);
  }
}
