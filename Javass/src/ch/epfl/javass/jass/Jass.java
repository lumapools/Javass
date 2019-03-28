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

    //long ALL_CARDS = 0b111111111_0000000111111111_0000000111111111_0000000111111111L;
    long ALL_CARDS = 0b00000000000000010_0000000000000010_0000000000000010_0000000000000010L;
    Card STARTING_CARD = Card.of(Color.DIAMOND, Rank.SEVEN);
    
    int HAND_SIZE = 1;
    int TRICKS_PER_TURN = 1;
    int WINNING_POINTS = 1000;
    int MATCH_ADDITIONAL_POINTS = 100;
    int LAST_TRICK_ADDITIONAL_POINTS = 5;
}
