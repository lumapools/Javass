package ch.epfl.javass.jass;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TurnState;

class MctsPlayerTest {
    
    @Test
    void MctsPlayerWorks() {
        
        CardSet hand = CardSet.EMPTY
                .add(Card.of(Color.SPADE, Rank.EIGHT))
                .add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.SPADE, Rank.TEN))
                .add(Card.of(Color.HEART, Rank.SIX))
                .add(Card.of(Color.HEART, Rank.SEVEN))
                .add(Card.of(Color.HEART, Rank.EIGHT))
                .add(Card.of(Color.HEART, Rank.NINE))
                .add(Card.of(Color.HEART, Rank.TEN))
                .add(Card.of(Color.HEART, Rank.JACK));


        TurnState turnState = TurnState.initial(Color.SPADE, Score.INITIAL, PlayerId.PLAYER_4);
        turnState = turnState.withNewCardPlayed(Card.of(Color.SPADE, Rank.JACK));

        Player player = new MctsPlayer(PlayerId.PLAYER_1, 0, 100);
        System.out.println(player.cardToPlay(turnState, hand));
     
    
    }
}