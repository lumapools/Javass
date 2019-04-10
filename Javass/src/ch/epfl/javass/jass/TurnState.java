package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * Représente l'état d'un tour de jeu
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */

public final class TurnState {
	private final long currentScore;
	private final long unplayedCards;
	private final int currentTrick;

	private TurnState(long currentScore, long unplayedCards, int currentTrick) {
		this.currentScore = currentScore;
		this.unplayedCards = unplayedCards;
		this.currentTrick = currentTrick;
	}

	/**
	 * Retourne l'état initial correspondant à un tour de jeu dont l'atout, le score
	 * initial et le joueur initial sont ceux donnés
	 * 
	 * @param trump       (Color) atout du tour
	 * @param score       (Score) score initial
	 * @param firstPlayer (PlayerId) joueur initial
	 * @return (TurnState) l'état initial
	 */
	public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
		return new TurnState(score.packed(), PackedCardSet.ALL_CARDS, PackedTrick.firstEmpty(trump, firstPlayer));
	}

	/**
	 * Retourne l'état dont les composantes (empaquetées) sont celles données, ou
	 * lève IllegalArgumentException si l'une d'entre elles est invalide selon la
	 * méthode isValid correspondante
	 * 
	 * @param pkScore         (long) score empaqueté
	 * @param pkUnplayedCards (long) l'ensemble de cartes pas jouées
	 * @param pkTrick         (int) pli empaqueté
	 * @return (TurnState) l'état correspondant aux paramètres donnés
	 */
	public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
		Preconditions.checkArgument(
				PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick));
		return new TurnState(pkScore, pkUnplayedCards, pkTrick);
	}

	/**
	 * Accesseur permettant d'obtenir la version empaquetée du score courant
	 * 
	 * @return (long) le score courant empaqueté
	 */
	public long packedScore() {
		return currentScore;
	}

	/**
	 * Accesseur permettant d'obtenir la version empaquetée de l'ensemble des cartes
	 * pas encore jouées
	 * 
	 * @return (long) l'ensemble empaqueté des cartes pas jouées
	 */
	public long packedUnplayedCards() {
		return unplayedCards;
	}

	/**
	 * Accesseur permettant d'obtenir la version empaquetée du pli courant
	 * 
	 * @return (int) le pli courant empaqueté
	 */
	public int packedTrick() {
		return currentTrick;
	}

	/**
	 * Accesseur permettant d'obtenir la version objet du score courant
	 * 
	 * @return (Score) le score courant
	 */
	public Score score() {
		return Score.ofPacked(packedScore());
	}

	/**
	 * Accesseur permettant d'obtenir la version objet de l'ensemble des cartes pas
	 * encore jouées
	 * 
	 * @return (CardSet) l'ensemble empaqueté des cartes pas jouées
	 */
	public CardSet unplayedCards() {
		return CardSet.ofPacked(packedUnplayedCards());
	}

	/**
	 * Accesseur permettant d'obtenir la version objet du pli courant
	 * 
	 * @return (Trick) le pli courant
	 */
	public Trick trick() {
		return Trick.ofPacked(packedTrick());
	}

	/**
	 * Retourne vrai ssi l'état est terminal, c-à-d si le dernier pli du tour a été
	 * joué
	 * 
	 * @return (boolean) vrai ssi terminal
	 */
	public boolean isTerminal() {
		return currentTrick == PackedTrick.INVALID;
	}

	/**
	 * Retourne l'identité du joueur devant jouer la prochaine carte, ou lève
	 * l'exception IllegalStateException si le pli courant est plein
	 * 
	 * @return (PlayerId) le prochain joueur
	 * @throws IllegalStateException si le pli est plein
	 */
	public PlayerId nextPlayer() throws IllegalStateException {
		if (PackedTrick.isFull(packedTrick())) {
			throw new IllegalStateException();
		}
		return PackedTrick.player(packedTrick(), PackedTrick.size(packedTrick()));
	}

	/**
	 * Retourne l'état correspondant à celui auquel on l'applique après que le
	 * prochain joueur ait joué la carte donnée, ou lève IllegalStateException si le
	 * pli courant est plein
	 * 
	 * @param card (Card) la carte jouée
	 * @return (TurnState) l'état correspondant
	 * @throws IllegalStateException si le pli est plein
	 */
	public TurnState withNewCardPlayed(Card card) throws IllegalStateException {
		int pkCard = card.packed();
		if (PackedTrick.isFull(packedTrick())) {
			throw new IllegalStateException();
		}
		return new TurnState(packedScore(), PackedCardSet.remove(packedUnplayedCards(), pkCard),
				PackedTrick.withAddedCard(packedTrick(), pkCard));
	}

	/**
	 * Retourne l'état correspondant à celui auquel on l'applique après que le pli
	 * courant ait été ramassé, ou lève IllegalStateException si le pli courant
	 * n'est pas terminé (c-à-d plein)
	 * 
	 * @return (TurnState) l'état correspondant
	 * @throws IllegalStateException si le pli n'est pas plein
	 */
	public TurnState withTrickCollected() throws IllegalStateException {
		if (!PackedTrick.isFull(packedTrick())) {
			throw new IllegalStateException();
		}
		long newCurrentScore = PackedScore.withAdditionalTrick(packedScore(),
				PackedTrick.winningPlayer(packedTrick()).team(), PackedTrick.points(packedTrick()));
		int newCurrentTrick = PackedTrick.nextEmpty(packedTrick());
		return new TurnState(newCurrentScore, packedUnplayedCards(), newCurrentTrick);
	}

	/**
	 * Retourne l'état correspondant à celui auquel on l'applique après que le
	 * prochain joueur ait joué la carte donnée, et que le pli courant ait été
	 * ramassé s'il est alors plein; lève IllegalStateException si le pli courant
	 * est plein
	 * 
	 * @param card (Card) carte à rajouter
	 * @return (TurnState) l'état correspondant
	 * @throws IllegalStateException si le pli courant avant d'ajouter la carte est
	 *                               plein
	 */
	public TurnState withNewCardPlayedAndTrickCollected(Card card) throws IllegalStateException {
		assert PackedCard.isValid(card.packed());
		if (PackedTrick.isFull(packedTrick())) {
			throw new IllegalStateException();
		}
		TurnState updateState = withNewCardPlayed(card);
		if (PackedTrick.isFull(updateState.packedTrick())) {
			updateState = updateState.withTrickCollected();
		}
		return updateState;
	}

}