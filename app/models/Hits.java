package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Hits extends Model {
  @ManyToOne
  public Player target;
  @ManyToOne
  public Score score;
  public int hits;
}
