package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class MctsPlayerTest {
	
	@Test
	void randomSimulateWorks(){
		
		TurnState turnState = TurnState.initial(Color.SPADE, Score.INITIAL, PlayerId.PLAYER_1);
		CardSet myHand = CardSet.ofPacked(0b111111_0000_0000_0001_1100L);
		turnState = turnState.withNewCardPlayed(Card.of(Color.SPADE, Rank.JACK));
		turnState = turnState.withNewCardPlayed(Card.of(Color.SPADE, Rank.EIGHT));
		myHand = myHand.remove(Card.of(Color.SPADE, Rank.EIGHT));
		
		
		MctsPlayer mctsPlayer = new MctsPlayer(PlayerId.PLAYER_2, 0, 10);
	   
	    System.out.println(mctsPlayer.randomSimulate(turnState, myHand));
        
		
	}
	
	
	
	

	

}
