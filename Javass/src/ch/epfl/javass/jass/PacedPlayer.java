package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * Cette classe permet de s'assurer qu'un joueur met un temps minimum pour jouer
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class PacedPlayer implements Player {
	private final Player underlyingPlayer;
	private final double minTimeInMilliseconds;
	private final int MILLIS_PER_SEC = 1000;

	/**
	 * Constructeur public. Retourne un joueur qui se comporte exactement comme le
	 * joueur sous-jacent donné (underlyingPlayer), si ce n'est que la méthode
	 * cardToPlay ne retourne jamais son résultat en un temps inférieur à minTime
	 * secondes
	 * 
	 * @param underlyingPlayer (Player) le joueur sous-jacent
	 * @param minTime          (double) le temps minimum
	 */
	public PacedPlayer(Player underlyingPlayer, double minTime) {
		assert !(minTime < 0);
		this.underlyingPlayer = underlyingPlayer;
		this.minTimeInMilliseconds = minTime * MILLIS_PER_SEC;
	}

	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {
		long currentTime = System.currentTimeMillis();
		Card carte = underlyingPlayer.cardToPlay(state, hand);
		long timePast = System.currentTimeMillis() - currentTime;
		if (timePast < minTimeInMilliseconds) {
			try {
				Thread.sleep((long) (minTimeInMilliseconds - timePast));
			} catch (InterruptedException e) {
				/* ignore */ }
		}
		return carte;
	}

	@Override
	public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
		underlyingPlayer.setPlayers(ownId, playerNames);
	}

	@Override
	public void updateHand(CardSet newHand) {
		underlyingPlayer.updateHand(newHand);
	}

	@Override
	public void setTrump(Color trump) {
		underlyingPlayer.setTrump(trump);
	}

	@Override
	public void updateTrick(Trick newTrick) {
		underlyingPlayer.updateTrick(newTrick);
	}

	@Override
	public void updateScore(Score score) {
		underlyingPlayer.updateScore(score);
	}

	@Override
	public void setWinningTeam(TeamId winningTeam) {
		underlyingPlayer.setWinningTeam(winningTeam);
	}
}