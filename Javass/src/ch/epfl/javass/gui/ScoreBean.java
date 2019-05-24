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
 * turnPoints, qui contient les points du tour; gamePoints, qui contient les
 * points de la partie; totalPoints, qui contient le total des points;
 * winningTeam, qui contient l'identité de l'équipe ayant gagné la partie
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class ScoreBean {
    private final IntegerProperty turnPoints_team1 = new SimpleIntegerProperty();
    private final IntegerProperty turnPoints_team2 = new SimpleIntegerProperty();

    private final IntegerProperty gamePoints_team1 = new SimpleIntegerProperty();
    private final IntegerProperty gamePoints_team2 = new SimpleIntegerProperty();

    private final IntegerProperty totalPoints_team1 = new SimpleIntegerProperty();
    private final IntegerProperty totalPoints_team2 = new SimpleIntegerProperty();

    private final ObjectProperty<TeamId> winningTeam = new SimpleObjectProperty<>(
            null);

    /**
     * Permet d'obtenir la propriété turnPoints de l'équipe correspondante
     * 
     * @param team
     *            (TeamId) l'équipe correspondante
     * @return (ReadOnlyIntegerProperty) la propriété en question
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        if (team.equals(TeamId.TEAM_1)) {
            return turnPoints_team1;
        }
        return turnPoints_team2;
    }

    /**
     * Permet de modifier la valeur que la propriété turnPoints contient
     * 
     * @param team
     *            (TeamId) l'équipe correspondante
     * @param newTurnPoints
     *            (int) les nouveaux turnPoints
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team.equals(TeamId.TEAM_1)) {
            turnPoints_team1.set(newTurnPoints);
        } else {
            turnPoints_team2.set(newTurnPoints);
        }
    }

    /**
     * Permet d'obtenir la propriété gamePoints de l'équipe correspondante
     * 
     * @param team
     *            (TeamId) l'équipe correspondante
     * @return (ReadOnlyIntegerProperty) la propriété en question
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        if (team.equals(TeamId.TEAM_1)) {
            return gamePoints_team1;
        }
        return gamePoints_team2;
    }

    /**
     * Permet de modifier la valeur que la propriété gamePoints contient
     * 
     * @param team
     *            (TeamId) l'équipe correspondante
     * @param newGamePoints
     *            (int) les nouveaux gamePoints
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team.equals(TeamId.TEAM_1)) {
            gamePoints_team1.set(newGamePoints);
        } else {
            gamePoints_team2.set(newGamePoints);
        }
    }

    /**
     * Permet d'obtenir la propriété totalPoints de l'équipe correspondante
     * 
     * @param team
     *            (TeamId) l'équipe correspondante
     * @return (ReadOnlyIntegerProperty) la propriété en question
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        if (team.equals(TeamId.TEAM_1)) {
            return totalPoints_team1;
        }
        return totalPoints_team2;
    }

    /**
     * Permet de modifier la valeur que la propriété totalPoints contient
     * 
     * @param team
     *            (TeamId) l'équipe correspondante
     * @param newTotalPoints
     *            (int) les nouveaux totalPoints
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team.equals(TeamId.TEAM_1)) {
            totalPoints_team1.set(newTotalPoints);
        } else {
            totalPoints_team2.set(newTotalPoints);
        }
    }

    /**
     * Permet d'obtenir la propriété winningTeam (qui est nulle tant que la
     * partie n'est pas terminée)
     * 
     * @return (ReadOnlyIntegerProperty) la propriété en question
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

    /**
     * Permet de modifier la valeur que la propriété winningTeam contient
     * 
     * @param winningTeam
     *            (int) l'équipe gagnante
     */
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }
}