package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

class MctsPlayerTest {
	
	public static void test(String[] args) {
		
		Map<PlayerId, Player> players = new HashMap<>();
	    Map<PlayerId, String> playerNames = new HashMap<>();
	    
	    playerNames.put(PlayerId.PLAYER_1, "mcts");
	    playerNames.put(PlayerId.PLAYER_2, "p2");
	    playerNames.put(PlayerId.PLAYER_3, "p3");
	    playerNames.put(PlayerId.PLAYER_4, "p4");

	    Player mctsPlayer = new MctsPlayer(PlayerId.PLAYER_1, 2019, 9);
	    mctsPlayer = new PrintingPlayer(mctsPlayer);
	    Player p2 = new RandomPlayer(2019);
	    Player p3 = new RandomPlayer(2019);
	    Player p4 = new RandomPlayer(2019);
	    
	    players.put(PlayerId.PLAYER_1, mctsPlayer);
	    players.put(PlayerId.PLAYER_2, p2);
	    players.put(PlayerId.PLAYER_3, p3);
	    players.put(PlayerId.PLAYER_4, p4);
	   
	    JassGame g = new JassGame(2019, players, playerNames);
	    int i = 0;
        while(i != 2) {
            g.advanceToEndOfNextTrick();
            System.out.println("----");
            i++;
        }
		
	}
	
	public static void main(String[] args) {
		
		List<Object> random = new ArrayList<Object>();
		int i = 10;
		while (i > 0) {
			SplittableRandom rng = new SplittableRandom();
			List<Double> toFill = new ArrayList<Double>();
			toFill.add(rng.nextDouble()*rng.nextDouble());
			toFill.add(rng.nextDouble()*rng.nextDouble());
			toFill.add(rng.nextDouble()*rng.nextDouble());
			toFill.add(rng.nextDouble()*rng.nextDouble());
			random.add(toFill);
			i--;	
			System.out.println(random);
		}
		
	}
	
	

	

}
