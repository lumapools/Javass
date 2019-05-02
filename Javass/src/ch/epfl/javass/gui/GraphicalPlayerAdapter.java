package ch.epfl.javass.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

public class GraphicalPlayerAdapter implements Player {

	ScoreBean scoreBean;
	HandBean handBean;
	TrickBean trickBean;
	GraphicalPlayer gPlayer;
	ArrayBlockingQueue<Card> queue;
	
	
	public GraphicalPlayerAdapter() {
		scoreBean = new ScoreBean();
		handBean = new HandBean();
		trickBean = new TrickBean();
		queue = new ArrayBlockingQueue<Card>(1);
		
	}
	
	@Override
	public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
	  gPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean, trickBean, handBean, queue);
	  Platform.runLater(() -> { gPlayer.createStage().show(); });
	}
	
	@Override
	public void updateHand(CardSet newHand) {
		Platform.runLater(() -> {handBean.setHand(newHand);});
	}
	
	@Override
	public void setTrump(Color trump) {
		Platform.runLater(() -> {trickBean.setTrump(trump);});
	}
	
	@Override
	public void updateTrick(Trick newTrick) {
		Platform.runLater(() -> {trickBean.setTrick(newTrick);});
	}
	
	@Override
	public void updateScore(Score score) {
		Platform.runLater(() -> {
			for(TeamId teamId : TeamId.ALL) {
				scoreBean.setGamePoints(teamId, score.gamePoints(teamId));
				scoreBean.setTurnPoints(teamId, score.turnPoints(teamId));
				scoreBean.setTotalPoints(teamId, score.totalPoints(teamId));
			}
		});
	}
	
	@Override
	public void setWinningTeam(TeamId teamId) {
		Platform.runLater(() -> {scoreBean.setWinningTeam(teamId);});
	}
	
	@Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        handBean.setPlayableCards(state.trick().playableCards(hand));
        try {
            Card c = queue.take();
            handBean.setPlayableCards(state.trick().playableCards(CardSet.EMPTY));
            return c;
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
	

}
