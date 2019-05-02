package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * un bean JavaFX contenant (principalement) les scores et doté des propriétés
 * suivantes :
 * 
 * turnPoints, qui contient les points du tour, gamePoints, qui contient les
 * points de la partie, totalPoints, qui contient le total des points,
 * winningTeam, qui contient l'identité de l'équipe ayant gagné la partie
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class ScoreBean {
    private IntegerProperty turnPoints_team1 = new SimpleIntegerProperty();
    private IntegerProperty turnPoints_team2 = new SimpleIntegerProperty();

    private IntegerProperty gamePoints_team1 = new SimpleIntegerProperty();
    private IntegerProperty gamePoints_team2 = new SimpleIntegerProperty();

    private IntegerProperty totalPoints_team1 = new SimpleIntegerProperty();
    private IntegerProperty totalPoints_team2 = new SimpleIntegerProperty();

    private ObjectProperty<TeamId> winningTeam = new SimpleObjectProperty<>(null);

    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        if (team.equals(TeamId.TEAM_1)) {
            return turnPoints_team1;
        }
        return turnPoints_team2;
    }

    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team.equals(TeamId.TEAM_1)) {
            turnPoints_team1.set(newTurnPoints);
        } else {
            turnPoints_team2.set(newTurnPoints);
        }
    }

    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        if (team.equals(TeamId.TEAM_1)) {
            return gamePoints_team1;
        }
        return gamePoints_team2;
    }

    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team.equals(TeamId.TEAM_1)) {
            gamePoints_team1.set(newGamePoints);
        } else {
            gamePoints_team2.set(newGamePoints);
        }
    }

    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        if (team.equals(TeamId.TEAM_1)) {
            return totalPoints_team1;
        }
        return totalPoints_team2;
    }

    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team.equals(TeamId.TEAM_1)) {
            totalPoints_team1.set(newTotalPoints);
        } else {
            totalPoints_team2.set(newTotalPoints);
        }
    }

    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }
}
