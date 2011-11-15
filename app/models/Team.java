package models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Team extends Model {
  @ManyToOne
  public Game game;
  @OneToMany(mappedBy = "team", cascade = { CascadeType.MERGE,
      CascadeType.REMOVE, CascadeType.REFRESH })
  public Set<Score> scores;
  public String name;

  public int playerCount() {
    return scores.size();
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
    return totalScore() / (float) playerCount();
  }
}
