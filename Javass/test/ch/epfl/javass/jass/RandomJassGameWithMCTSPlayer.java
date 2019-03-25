package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

public class RandomJassGameWithMCTSPlayer {
	
	public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        
        for(PlayerId pId: PlayerId.ALL) {
            Player player = new RandomPlayer(2019);
            playerNames.put(pId, pId.name());
            if(pId == PlayerId.PLAYER_1) {
                player = new PrintingPlayer(new MctsPlayer(PlayerId.PLAYER_1, 2019, 1000));
                playerNames.put(pId, pId.name() + " (me)");
            } 
            //player = new PacedPlayer(player, 1000);
            
            players.put(pId, player);
        }
        
        JassGame g = new JassGame(2019, players, playerNames);
        while(!g.isGameOver()) {
            g.advanceToEndOfNextTrick();
            System.out.println("----");
        }
        
    }
}
