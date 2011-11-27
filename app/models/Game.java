package models;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Game extends Model {
  @OneToMany(mappedBy = "game", cascade = { CascadeType.MERGE,
      CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.ALL })
  public Set<Team> teams;
  public Date date;

  public int playerCount() {
    int count = 0;
    for (Team team : teams) {
      count += team.playerCount();
    }
    return count;
  }

  public int shots() {
    int count = 0;
    for (Team team : teams) {
      count += team.shotCount();
    }
    return count;
  }

  public float avgScore() {
    return totalScore() / (float) playerCount();
  }

  private int totalScore() {
    int score = 0;
    for (Team team : teams) {
      score += team.totalScore();
    }
    return score;
  }

  public float correctedAvgScore() {
    int score = 0;
    int count = 0;
    for (Team team : teams) {
      score += team.correctedTotalScore();
      count += team.correctedPlayerCount();
    }
    return score / (float) count;
  }

}
