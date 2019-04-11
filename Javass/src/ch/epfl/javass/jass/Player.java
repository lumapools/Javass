package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * Représente un joueur
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public interface Player {
	/**
	 * Retourne la carte que le joueur désire jouer, sachant que l'état actuel du
	 * tour est celui décrit par state et que le joueur a les cartes hand en main
	 * 
	 * @param state (TurnState) l'état actuel du tour
	 * @param hand  (CardSet) l'ensemble de cartes en main empqueté
	 * @return (Card) la carte que le joueur désire jouer
	 */
	abstract Card cardToPlay(TurnState state, CardSet hand);

	/**
	 * Cette méthode est appelée une seule fois en début de partie pour informer le
	 * joueur qu'il a l'identité ownId et que les différents joueurs (lui inclus)
	 * sont nommés selon le contenu de la table associative playerNames,
	 * 
	 * @param ownId       (PlayerId) l'identité du joueur
	 * @param playerNames (Map<PlayerId, String>) table associative Identité-nom
	 *                    d'un joueur
	 */
	default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
	}

	/**
	 * Cette méthode est appelée chaque fois que la main du joueur change — soit en
	 * début de tour lorsque les cartes sont distribuées, soit après qu'il ait joué
	 * une carte — pour l'informer de sa nouvelle main
	 * 
	 * @param newHand (CardSet) l'ensemble de nouvelles cartes en main (empaqueté)
	 */
	default void updateHand(CardSet newHand) {
	}

	/**
	 * Cette méthode est appelée chaque fois que l'atout change — c-à-d au début de
	 * chaque tour — pour informer le joueur de l'atout
	 * 
	 * @param trump (Color) l'atout du nouveau tour
	 */
	default void setTrump(Color trump) {
	}

	/**
	 * Cette méthode est appelée chaque fois que le pli change, c-à-d chaque fois
	 * qu'une carte est posée ou lorsqu'un pli terminé est ramassé et que le
	 * prochain pli (vide) le remplace
	 * 
	 * @param newTrick (Trick) le nouveau pli
	 */
	default void updateTrick(Trick newTrick) {
	}

	/**
	 * Cette méthode est appelée chaque fois que le score change, c-à-d chaque fois
	 * qu'un pli est ramassé
	 * 
	 * @param score (Score) le score
	 */
	default void updateScore(Score score) {
	}

	/**
	 * Cette méthode est appelée une seule fois dès qu'une équipe a gagné en
	 * obtenant 1000 points ou plus
	 * 
	 * @param winningTeam (TeamId) l'équipe gagnante
	 */
	default void setWinningTeam(TeamId winningTeam) {
	}
}
