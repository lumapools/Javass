package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Représentation d'une carte d'un jeu de 36 cartes
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */

// Une classe immuable
public final class Card {
	/*
	 * Représentation de la version empaquetée de la carte
	 */
	private final int packed;

	private Card(int packed) {
		this.packed = packed;
	}

	/**
	 * Appelle le constructeur et crée une carte
	 * 
	 * @param c (Color) la couleur de la carte
	 * @param r (Rank) le rang de la carte
	 * @return (Card) une nouvelle carte qui a la couleur et le rang choisis
	 */
	public static Card of(Color c, Rank r) {
		return new Card(PackedCard.pack(c, r));
	}

	/**
	 * Crée une carte à partir de sa forme empaquetée en 32 bits
	 * 
	 * @param packed (int) carte sous forme int (séquence de 32 bits)
	 * 
	 * @return (Card) la carte crée à partir de sa forme int
	 */
	public static Card ofPacked(int packed) {
		Preconditions.checkArgument(PackedCard.isValid(packed));
		return new Card(packed);
	}

	/**
	 * Retourne la version empaquetée de la carte
	 * 
	 * @return (int) la version empaquetée de la carte
	 */
	public int packed() {
		return packed;
	}

	/**
	 * Retourne la couleur de la carte
	 * 
	 * @return (Color) la couleur de la carte
	 */
	public Color color() {
		return PackedCard.color(packed);
	}

	/**
	 * Retourne le rang de la carte
	 * 
	 * @return (Rang) le rang de la carte
	 */
	public Rank rank() {
		return PackedCard.rank(packed);
	}

	/**
	 * Retourne vraie ssi le récepteur (c-à-d la carte à laquelle on applique la
	 * méthode) est supérieur à la carte passée en argument, sachant que l'atout est
	 * trump
	 * 
	 * @param trump (Color) l'atout du jeu
	 * @param that  (Card) carte précédente
	 * @return (boolean) vrai si la carte est supérieure, faux sinon (pas comparable
	 *         y compris)
	 */
	public boolean isBetter(Color trump, Card that) {
		return PackedCard.isBetter(trump, packed, that.packed());
	}

	/**
	 * Retourne la valeur de la carte, sachant que l'atout est trump
	 * 
	 * @param trump (Color) l'atout du jeu
	 * @return (int) la valeur de la carte
	 */
	public int points(Color trump) {
		return PackedCard.points(trump, packed);
	}

	/**
	 * @return (boolean) vrai si ça représente la même carte, faux sinon
	 */
	@Override
	public boolean equals(Object that) {
		return that instanceof Card && ((Card) that).packed() == packed;
	}

	/**
	 * @return (int) la version empaquetée de la carte
	 */
	@Override
	public int hashCode() {
		return packed;
	}

	/**
	 * @return (String) symbole de la couleur et le nom abrégé du rang
	 */
	@Override
	public String toString() {
		return PackedCard.toString(packed);
	}

	/**
	 * 
	 * Représente la couleur d'une carte type énuméré, publique et imbriqué dans la
	 * classe Card
	 *
	 */
	public enum Color {
		SPADE("\u2660"), HEART("\u2661"), DIAMOND("\u2662"), CLUB("\u2663");

		// symbole de la couleur
		private final String image;

		Color(String image) {
			this.image = image;
		}

		/**
		 * une liste immuable contenant toutes les valeurs du type énuméré, dans l'ordre
		 * de déclaration (couleur)
		 */
		public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));

		/**
		 * Le nombre de valeurs du type énuméré
		 */
		public static final int COUNT = values().length;

		/**
		 * @return (String) le symbole correspondant à la couleur
		 */
		@Override
		public String toString() {
			return image;
		}

	}

	/**
	 * 
	 * Représente le rang d'une carte type énuméré, publique et imbriqué dans la
	 * classe Card
	 *
	 */
	public enum Rank {
		SIX("6", 0), SEVEN("7", 1), EIGHT("8", 2), NINE("9", 7), TEN("10", 3), JACK("J", 8), QUEEN("Q", 4),
		KING("K", 5), ACE("A", 6);

		// représentation compacte du rang
		private final String representation;
		// la position lorsque c'est l'atout
		private final int orderTrump;

		private Rank(String representation, int ordreTrump) {
			this.representation = representation;
			this.orderTrump = ordreTrump;
		}

		/**
		 * une liste immuable contenant toutes les valeurs du type énuméré, dans l'ordre
		 * de déclaration (rang)
		 */
		public static final List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));

		/**
		 * Le nombre de valeurs du type énuméré
		 */
		public static final int COUNT = values().length;

		/**
		 * Retourne la position lorsque c'est l'atout
		 * 
		 * @return(int) la position comprise entre 0 et 8, de la carte d'atout ayant ce
		 *              rang, à savoir : 0 pour SIX, 1 pour SEVEN, …, 7 pour NINE et 8
		 *              pour JACK
		 */
		public int trumpOrdinal() {
			return orderTrump;
		}

		/**
		 * @return (String) représentation compacte du rang
		 */
		@Override
		public String toString() {
			return representation;
		}
	}

}