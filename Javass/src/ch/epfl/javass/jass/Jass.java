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
	int HAND_SIZE = 9;
	/**
	 * Le nombre de plis par tour
	 */
	int TRICKS_PER_TURN = 9;
	/**
	 * Le nombre de points nécessaire pour gagner
	 */
	int WINNING_POINTS = 1000;
	/**
	 * Les points additionnels lors d'un match
	 */
	int MATCH_ADDITIONAL_POINTS = 100;
	/**
	 * Les points additionnels du dernier pli
	 */
	int LAST_TRICK_ADDITIONAL_POINTS = 5;
}