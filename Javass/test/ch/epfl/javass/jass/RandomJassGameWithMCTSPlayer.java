package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

public class RandomJassGameWithMCTSPlayer {
	
	public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        
        
        Player mctsPlayer1 = new SilentPlayer(new MctsPlayer(PlayerId.PLAYER_1, 0, 1000));
        Player randomPlayer2 = new RandomPlayer(10);
        Player mctsPlayer3 = new MctsPlayer(PlayerId.PLAYER_3, 0, 1000);
        Player randomPlayer4 = new RandomPlayer(30);
        
        players.put(PlayerId.PLAYER_1, mctsPlayer1);
        players.put(PlayerId.PLAYER_2, randomPlayer2);
        players.put(PlayerId.PLAYER_3, mctsPlayer3);
        players.put(PlayerId.PLAYER_4, randomPlayer4);
        
        playerNames.put(PlayerId.PLAYER_1, "MCTS1");
        playerNames.put(PlayerId.PLAYER_2, "RAND2");
        playerNames.put(PlayerId.PLAYER_3, "RAND3");
        playerNames.put(PlayerId.PLAYER_4, "RAND4");
        
        
        for(int i = 0; i < 100; i++) {
        	JassGame game = new JassGame(2000, players, playerNames);
        	while(!game.isGameOver()) {
        		game.advanceToEndOfNextTrick();
        	}
        }
        
        
      
        
    }
}
