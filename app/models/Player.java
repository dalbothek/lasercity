package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;
import util.Partitioner.Partitionable;

@Entity
public class Player extends Model implements Partitionable {
  public String name;
  public boolean imba;
  @OneToMany(mappedBy = "player", cascade = { CascadeType.MERGE,
      CascadeType.REMOVE, CascadeType.REFRESH })
  public Set<Score> scores;

  @OneToMany(mappedBy = "target", cascade = { CascadeType.MERGE,
      CascadeType.REMOVE, CascadeType.REFRESH })
  public Set<Hits> hits;
  private int guesstimate;

  public Player(String name) {
    this(name, -10);
  }

  public Player(String name, int imbaness) {
    this(name, imbaness, false);
  }

  public Player(String name, int imbaness, boolean imba) {
    this.name = name;
    guesstimate = imbaness;
    this.imba = imba;
  }

  public int shotCount() {
    int shots = 0;
    for (Score score : scores) {
      shots += score.shots;
    }
    return shots;
  }

  public int totalScore() {
    int total = 0;
    for (Score score : scores) {
      total += score.score;
    }
    return total;
  }

  public float avgScore() {
    return totalScore() / (float) gameCount();
  }

  public int gameCount() {
    return scores.size();
  }

  public float imbaness() {
    float count = 0;
    float imbaness = 0;
    float factor = 1;
    for (Score score : Score.find("player = ? order by team.game.date desc", this).<Score> fetch()) {
      count += factor;
      imbaness += score.imbaness() * factor;
      factor -= 0.05;
      if (factor == 0) {
        break;
      }
    }
    if (count == 0) {
      return guesstimate;
    }
    return imbaness / count;
  }

  public static Collection<Long> serializePlayerList(Collection<Player> players) {
    Collection<Long> ids = new ArrayList<Long>(players.size());
    for (Player player : players) {
      ids.add(player.id);
    }
    return ids;
  }

  public static Collection<Player> loadSerializedPlayerList(Collection<Long> ids) {
    Collection<Player> players = new ArrayList<Player>(ids.size());
    for (Long id : ids) {
      Player player = Player.<Player> findById(id);
      if (player != null) {
        players.add(player);
      }
    }
    return players;
  }

  public static class PlayerComparator implements Comparator<Player> {
    private HashMap<Player, Float> values;
    private Property property;

    public PlayerComparator(Property property) {
      values = new HashMap<Player, Float>();
      this.property = property;
    }

    private Float value(Player player) {
      if (!values.containsKey(player)) {
        values.put(player, calculateValue(player));
      }
      return values.get(player);
    }

    private Float calculateValue(Player player) {
      switch (property) {
        case SCORE:
          return (float) player.totalScore();
        case SHOTS:
          return (float) player.shotCount();
        case IMBANESS:
          return player.imbaness();
        default:
          return 0F;
      }
    }

    @Override
    public int compare(Player p1, Player p2) {
      return value(p2).compareTo(value(p1));
    }

    public enum Property {
      SCORE,
      SHOTS,
      IMBANESS
    }

  }

  @Override
  public float partitionWorth() {
    return imbaness();
  }
}
