package ch.epfl.javass.jass;

import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Représente un ensemble de cartes
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class CardSet {
	/**
	 * Constante qui représente un ensemble vide
	 */
	public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
	/**
	 * Constante qui représente un ensemble de cartes dans lequel tout les cartes y
	 * sont
	 */
	public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);

	// l'ensemble de cartes empaqueté
	private final long packed;

	private CardSet(long packed) {
		this.packed = packed;
	}

	/**
	 * Crée une instance du set de cartes à partir d'une liste de cartes donnée
	 * 
	 * @param cards (List contenant des Card) La liste de cartes dont on veut créer
	 *              un CardSet
	 * @return (CardSet) obtenu à partir de la liste des cartes
	 */
	public static CardSet of(List<Card> cards) {
		long cardSet = PackedCardSet.EMPTY;
		for (Card c : cards) {
			cardSet = PackedCardSet.add(cardSet, c.packed());
		}
		return new CardSet(cardSet);
	}

	/**
	 * Créer une instance de set de cartes à partir d'une séquence de 64 bits donnée
	 * 
	 * @param packed (long) le cardSet sous forme de séquence de 64 bits
	 * @return (CardSet) le set de cartes obtenu à partir de la chaîne de 64 bits
	 */
	public static CardSet ofPacked(long packed) {
		Preconditions.checkArgument(PackedCardSet.isValid(packed));
		return new CardSet(packed);
	}

	/**
	 * Retourne la séquence de 64 bits qui représente ce cardSet
	 * 
	 * @return (long) la séquence de 64 bits qui représente ce cardSet
	 */
	public long packed() {
		return packed;
	}

	/**
	 * Check si une liste contient des cartes ou pas
	 * 
	 * @return (boolean) vrai si c'est un set vide sinon faux
	 */
	public boolean isEmpty() {
		return PackedCardSet.isEmpty(packed());
	}

	/**
	 * Retourne le nombre de cartes dans le set de cartes
	 * 
	 * @return (int) le nombre de cartes dans le set de cartes
	 */
	public int size() {
		return PackedCardSet.size(packed());
	}

	/**
	 * Retourne la version empaquetée de la carte d'index donné de l'ensemble de
	 * cartes empaqueté donné
	 * 
	 * @param index (int) l'index auquel on veut la carte
	 * @return (Card) la carte qui se trouve à l'index index
	 */
	public Card get(int index) {
		return Card.ofPacked(PackedCardSet.get(packed(), index));
	}

	/**
	 * Retourne l'ensemble de cartes auquel la carte donnée a été ajoutée
	 * 
	 * @param card (Card) la carte qu'on veut ajouter à l'ensemble
	 * @return (CardSet) l'ensemble de carte auquel on a ajouté la carte card
	 */
	public CardSet add(Card card) {
		return new CardSet(PackedCardSet.add(packed(), card.packed()));
	}

	/**
	 * Retourne l'ensemble de cartes duquel la carte donnée à été supprimée
	 * 
	 * @param card (Card) la carte à supprimer de l'ensemble
	 * @return (CardSet) l'ensemble de cartes duquel on a supprimé la carte card
	 */
	public CardSet remove(Card card) {
		return new CardSet(PackedCardSet.remove(packed(), card.packed()));
	}

	/**
	 * Retourne vrai ssi l'ensemble de cartes empaqueté donné contient la carte
	 * empaquetée donnée
	 * 
	 * @param card (Card) la carte qu'on veut vérifier
	 * @return (boolean) vrai si la carte donnée en argument est contenue dans le
	 *         cardSet, sinon faux
	 */
	public boolean contains(Card card) {
		return PackedCardSet.contains(packed(), card.packed());
	}

	/**
	 * Retourne le complément d'un ensemble de carte, c'est-à-dire l'ensemble de
	 * cartes pas contenues dans l'ensemble
	 * 
	 * @return (CardSet) le complément de l'ensemble de cartes
	 */
	public CardSet complement() {
		return new CardSet(PackedCardSet.complement(packed()));
	}

	/**
	 * Retourne l'union de deux ensembles de cartes
	 * 
	 * @param that (CardSet) l'ensemble qu'on veut unir avec celui-ci
	 * @return (CardSet) l'union de deux ensembles de cartes
	 */
	public CardSet union(CardSet that) {
		return new CardSet(PackedCardSet.union(packed(), that.packed()));
	}

	/**
	 * Retourne l'intersection de deux ensembles de cartes
	 * 
	 * @param that (CardSet) l'ensemble avec lequel on veut prendre l'intersection
	 * @return (CardSet) l'intersection de deux ensembles de cartes
	 */
	public CardSet intersection(CardSet that) {
		return new CardSet(PackedCardSet.intersection(packed(), that.packed()));
	}

	/**
	 * Calcule la différence avec un autre ensemble de cartes et retourne l'ensemble
	 * des cartes qui se trouvent dans cet ensemble mais pas dans celui donné en
	 * argument
	 * 
	 * @param that (CardSet) ensemble qu'on veut comparer avec celui-ci
	 * @return (CardSet) l'ensemble des cartes se trouvant dans cet ensemble mais
	 *         pas celui donné en argument
	 */
	public CardSet difference(CardSet that) {
		return new CardSet(PackedCardSet.difference(packed(), that.packed()));
	}

	/**
	 * Retourne le sous-ensemble de cet ensemble de cartes constitué uniquement des
	 * cartes de la couleur choisie
	 * 
	 * @param color (Card.Color) la couleur voulue
	 * @return (CardSet) l'ensemble de cartes qui contient seulement les cartes de
	 *         la couleur choisie
	 */
	public CardSet subsetOfColor(Card.Color color) {
		return new CardSet(PackedCardSet.subsetOfColor(packed(), color));

	}

	/**
	 * @return (boolean) vrai si ça représente le même ensemble, faux sinon
	 */
	@Override
	public boolean equals(Object that) {
		return that instanceof CardSet && ((CardSet) that).packed() == this.packed();
	}

	/**
	 * @return (int) le résultat de la méthode hashCode de la classe Long appliquée
	 *         à l'ensemble empaqueté
	 */
	@Override
	public int hashCode() {
		return Long.hashCode(packed());
	}

	/**
	 * @return (String) l'ensemble avec rang et couleur
	 */
	@Override
	public String toString() {
		return PackedCardSet.toString(packed());
	}
}