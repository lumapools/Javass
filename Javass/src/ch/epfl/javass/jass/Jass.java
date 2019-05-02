package ch.epfl.javass.jass;

/**
 * Contient un certain nombre de constantes entières de type int, liées au jeu
 * de Jass
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public interface Jass {
	/**
	 * Le nombre de cartes dans la main au début
	 */
	public final static int HAND_SIZE = 9;
	/**
	 * Le nombre de plis par tour
	 */
	public final static int TRICKS_PER_TURN = 9;
	/**
	 * Le nombre de points nécessaire pour gagner
	 */
	public final static int WINNING_POINTS = 1000;
	/**
	 * Les points additionnels lors d'un match
	 */
	public final static int MATCH_ADDITIONAL_POINTS = 100;
	/**
	 * Les points additionnels du dernier pli
	 */
	public final static int LAST_TRICK_ADDITIONAL_POINTS = 5;
}