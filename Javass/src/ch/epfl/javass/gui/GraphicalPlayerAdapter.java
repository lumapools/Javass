package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;

/**
 * Un adaptateur permettant d'adapter l'interface graphique (c-à-d la classe
 * GraphicalPlayer) pour en faire un joueur, c-à-d une valeur de type Player
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public class GraphicalPlayerAdapter implements Player {
	//constante
	private static final int QUEUE_SIZE = 1;
	
	// attributs
    private HandBean hB;
    private ScoreBean sB;
    private TrickBean tB;
    private GraphicalPlayer graphicalPlayer;
    private ArrayBlockingQueue<Card> blockQueue;

    public GraphicalPlayerAdapter() {
        hB = new HandBean();
        sB = new ScoreBean();
        tB = new TrickBean();
        blockQueue = new ArrayBlockingQueue<Card>(QUEUE_SIZE);
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, sB, tB, hB,
                blockQueue);
        Platform.runLater(() -> {
            graphicalPlayer.createStage().show();
        });
    }

    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> {
            hB.setHand(newHand);
        });
    }

    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> {
            tB.setTrump(trump);
        });
    }

    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> {
            tB.setTrick(newTrick);
        });
    }

    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {
            for (TeamId team : TeamId.ALL) {
                sB.setGamePoints(team, score.gamePoints(team));
                sB.setTotalPoints(team, score.totalPoints(team));
                sB.setTurnPoints(team, score.turnPoints(team));
            }
        });
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> {
            sB.setWinningTeam(winningTeam);
        });

    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Platform.runLater(() -> {
            hB.setPlayableCards(state.trick().playableCards(hand));
        });
        try {
            Card c = blockQueue.take();
            Platform.runLater(() -> {
                hB.setPlayableCards(CardSet.EMPTY);
            });
            return c;
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

}
