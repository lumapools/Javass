package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
public final class RandomJassGameSeb {

    public static void main(String[] args) {
        int victoryTeam1=0;
        int victoryTeam2 = 0;
        
        ArrayList<Integer> gamesLost = new ArrayList<>();
        for(int i = 0; i<2000; ++i) {
            Map<PlayerId, Player> players = new HashMap<>();
            Map<PlayerId, String> playerNames = new HashMap<>();
            for(PlayerId pId: PlayerId.ALL) {
                Player player = new RandomPlayer(2019);
                playerNames.put(pId, pId.name());
                if(pId == PlayerId.PLAYER_1) {
                    player = new MctsPlayer(pId, 300, 100);
                    player = new SilentPlayer(player);
                    playerNames.put(pId, pId.name() + " (me)");
                } else if(pId == PlayerId.PLAYER_3) {
                    player = new MctsPlayer(pId, 300, 100);
                }
                players.put(pId, player);
            }
            
            JassGame g = new JassGame(i, players, playerNames);
            while(!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
            }

            if(SilentPlayer.victoryTeam1) {
                ++victoryTeam1; 
            } else if(SilentPlayer.victoryTeam2) {
                gamesLost.add(i);
                ++victoryTeam2;
            }
            System.out.println(victoryTeam1 + "/" + victoryTeam2);
        }
        for(int i = 0; i<gamesLost.size(); ++i) {
            System.out.println(gamesLost.get(i) + ",");
        }
    }

}