package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import models.Player;
import play.mvc.Controller;
import util.Partitioner;
import util.Partitioner.Partition;

public class TeamBuilder extends Controller {
  public static void build(HashMap<Integer, Boolean> player) {
    ArrayList<Player> players = new ArrayList<Player>(player.size());
    for (Integer id : player.keySet()) {
      if (!player.get(id)) {
        continue;
      }
      Player p = Player.find("byId", (long) id).first();
      if (p != null) {
        players.add(p);
      }
    }
    Partition<Player> partition = Partitioner.part(players);
    Collection<Player> left = partition.left;
    Collection<Player> right = partition.right;
    render(left, right);
  }
}
