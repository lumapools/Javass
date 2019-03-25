package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.MctsPlayer.Node;

class MctsPlayerTest {
	
	@Test
	@Disabled
	void randomSimulateWorks(){
		
		TurnState turnState = TurnState.initial(Color.SPADE, Score.INITIAL, PlayerId.PLAYER_1);
		CardSet myHand = CardSet.ofPacked(0b111111_0000_0000_0001_1100L);
		turnState = turnState.withNewCardPlayed(Card.of(Color.SPADE, Rank.JACK));
		turnState = turnState.withNewCardPlayed(Card.of(Color.SPADE, Rank.EIGHT));
		myHand = myHand.remove(Card.of(Color.SPADE, Rank.EIGHT));
		
		
		MctsPlayer mctsPlayer = new MctsPlayer(PlayerId.PLAYER_2, 0, 10);
	   
	    assertEquals("(3,52,0,52)/(6,105,0,105)", mctsPlayer.randomSimulate(turnState, myHand).toString());
	}
	
	@Test
	@Disabled
	void nodeSysoutWorks() {
		TurnState turnState = TurnState.initial(Color.SPADE, Score.INITIAL, PlayerId.PLAYER_1);
		MctsPlayer mctsPlayer = new MctsPlayer(PlayerId.PLAYER_2, 0, 10);

//		Node nC001 = new Node(turnState, s_n, n_n); 
//		Node nC00 = new Node(turnState, s_n, n_n);
//		Node nC01 = new Node(turnState, s_n, n_n);
//		Node nC02 = new Node(turnState, s_n, n_n);
		Node root = new Node(turnState.withNewCardPlayed(Card.of(Color.SPADE, Rank.JACK)), null);
		List<Node> path = mctsPlayer.growTreeByOneNode(root);
		System.out.println("Root: " + mctsPlayer.randomSimulatePrimitive(root.getTurnState()));
		Node node2 = root.addChild(Card.of(Color.SPADE, Rank.NINE));
		Node node3 = root.addChild(Card.of(Color.SPADE, Rank.TEN));
		//Node node11 = node1.addChild(Card.of(Color.SPADE, Rank.SIX));
		Node node31 = node3.addChild(Card.of(Color.SPADE, Rank.SIX));
		root.print("");
	}
	
	@Test
	void addBestChildDemoWorks() {
		MctsPlayer mctsPlayer = new MctsPlayer(PlayerId.PLAYER_2, 0, 10);
		TurnState turnState = TurnState.initial(Color.SPADE, Score.INITIAL, PlayerId.PLAYER_1);
		for(int i = 1; i < 32; i++) {
			turnState = turnState.withNewCardPlayedAndTrickCollected(turnState.unplayedCards().get(0));
		}
		Node root = new Node(turnState, null);
		List<Node> path;
		for(int i = 0; i < 15; i++) {
			path = mctsPlayer.growTreeByOneNode(root);
			if(path != null) {
				mctsPlayer.computeAndUpdateScores(path);
			}
		}
		root.print("");
	}
	
	
	
	

	

}
