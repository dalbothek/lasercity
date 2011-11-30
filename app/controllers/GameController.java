package controllers;

import java.util.Date;
import java.util.HashMap;

import models.Game;
import models.Player;
import models.Score;
import models.Team;
import play.db.jpa.JPA;
import play.mvc.Controller;

public class GameController extends Controller {
  public static void newGame(HashMap<Long, Integer> score, HashMap<Long, Integer> team) {
    Game game = new Game().save();
    game.date = new Date();

    Team leftTeam = new Team(game, "Red").save();
    Team rightTeam = new Team(game, "Blue").save();

    for (long id : score.keySet()) {
      Player player = Player.findById(id);
      if (player == null) {
        JPA.setRollbackOnly();
        PlayerController.list();
      }
      Team t = team.get(id) == 1 ? leftTeam : rightTeam;
      new Score(t, player, score.get(player.id)).save();
    }

    game.save();
    PlayerController.list();
  }
}
