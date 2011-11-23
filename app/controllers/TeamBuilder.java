package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.Game;
import models.Player;
import models.Player.PlayerComparator;
import models.Player.PlayerComparator.Property;
import models.Score;
import models.Team;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.mvc.Controller;
import util.Partitioner;
import util.Partitioner.Partition;

public class TeamBuilder extends Controller {
  public static void playerSelection() {
    List<Player> players = Player.<Player> findAll();
    Collections.sort(players, new PlayerComparator(Property.IMBANESS));
    Collection<Long> ids1 = Cache.get(session.getId() + "-team1", Collection.class);
    Collection<Long> ids2 = Cache.get(session.getId() + "-team2", Collection.class);
    Collection<Player> left = null;
    Collection<Player> right = null;
    if (ids1 != null && ids2 != null) {
      left = Player.loadSerializedPlayerList(ids1);
      right = Player.loadSerializedPlayerList(ids2);
    }
    render(players, left, right);
  }

  public static void build(int[] ids) {
    ArrayList<Player> players = new ArrayList<Player>(ids.length);
    for (int id : ids) {
      Player player = Player.find("byId", (long) id).first();
      if (player != null) {
        players.add(player);
      }
    }
    build(players);
  }

  private static void build(Collection<Player> players) {
    Partition<Player> partition = Partitioner.part(players);

    Cache.set(session.getId() + "-team1", Player.serializePlayerList(partition.left));
    Cache.set(session.getId() + "-team2", Player.serializePlayerList(partition.right));

    playerSelection();
  }

  public static void score(HashMap<Long, Integer> score) {
    Collection<Long> ids1 = Cache.get(session.getId() + "-team1", Collection.class);
    Collection<Long> ids2 = Cache.get(session.getId() + "-team2", Collection.class);
    if (ids1 == null || ids2 == null) {
      playerSelection();
    }
    Collection<Player> left = Player.loadSerializedPlayerList(ids1);
    Collection<Player> right = Player.loadSerializedPlayerList(ids2);

    Game game = new Game().save();
    game.date = new Date();

    Team leftTeam = new Team(game, "Red").save();
    Team rightTeam = new Team(game, "Blue").save();

    for (Player player : left) {
      if (score.get(player.id) == null) {
        JPA.setRollbackOnly();
        playerSelection();
      }
      new Score(leftTeam, player, score.get(player.id)).save();
    }

    for (Player player : right) {
      if (score.get(player.id) == null) {
        JPA.setRollbackOnly();
        playerSelection();
      }
      new Score(rightTeam, player, score.get(player.id)).save();
    }

    game.save();
    game.refresh();
    leftTeam.refresh();
    rightTeam.refresh();
    left.addAll(right);
    build(left);
  }
}
