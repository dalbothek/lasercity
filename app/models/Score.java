package models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Score extends Model {
  @ManyToOne
  public Player player;
  @ManyToOne
  public Team team;
  public int score;
  public int shots;
  @OneToMany(mappedBy = "score", cascade = { CascadeType.MERGE,
      CascadeType.REMOVE, CascadeType.REFRESH })
  Set<Hits> hits;

  public Score(Team team, Player player, int score) {
    this.team = team;
    this.score = score;
    this.player = player;
  }

  public float imbaness() {
    return score * 100 / team.game.correctedAvgScore() - 100;
  }
}
