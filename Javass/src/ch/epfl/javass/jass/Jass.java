package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Contient un certain nombre de constantes entières de type int, 
 * liées au jeu de Jass
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public interface Jass {

    Card STARTING_CARD = Card.of(Color.DIAMOND, Rank.SEVEN);
    
    int HAND_SIZE = 9;
    int TRICKS_PER_TURN = 9;
    int WINNING_POINTS = 1000;
    int MATCH_ADDITIONAL_POINTS = 100;
    int LAST_TRICK_ADDITIONAL_POINTS = 5;
}
