package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
public final class PrintingPlayer implements Player{
    private final Player player;
    public static boolean victoryTeam1;
    public static boolean victoryTeam2;
    
    public PrintingPlayer(Player underlyingPlayer) {
        this.player = underlyingPlayer;
        victoryTeam1 = false;
        victoryTeam2 = false;
    }
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        
        System.out.print("It's my turn to play, I play : ");
        Card c = player.cardToPlay(state, hand);
        System.out.println(c);
        return c;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        System.out.println("The players are : ");
        for(PlayerId players: PlayerId.ALL) {
            System.out.println("  " +playerNames.get(players));
        }
        player.setPlayers(ownId, playerNames);
    }
    
    @Override
    public void updateHand(CardSet newHand) {
        System.out.println("My new hand : " + newHand.toString());
        player.updateHand(newHand);
    }
    
    @Override
    public void setTrump(Color trump) {
        System.out.println("Trump color : " + trump.toString());
        player.setTrump(trump);
    }
    
    @Override
    public void updateTrick(Trick newTrick) {
        System.out.println("Trick " + newTrick.index() + ", started by " + newTrick.player(0).name() + " : " + newTrick.toString().substring(1, newTrick.toString().length()-1));
        player.updateTrick(newTrick);
    }
    
    @Override
    public void updateScore(Score score) {
        System.out.println("Scores: " + score.toString());
        player.updateScore(score);
    }
    
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("The winning team is " + winningTeam.name());
        if(winningTeam.equals(TeamId.TEAM_1)) {
            victoryTeam1 = true;
        } else if(winningTeam.equals(TeamId.TEAM_2)) {
            victoryTeam2 = true;
        }
        player.setWinningTeam(winningTeam);
    }

}