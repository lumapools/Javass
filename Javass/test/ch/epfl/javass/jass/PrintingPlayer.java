package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

public final class PrintingPlayer implements Player{
    private final Player player;
    
    public PrintingPlayer(Player underlyingPlayer) {
        this.player = underlyingPlayer;
    }
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        System.out.print("It's my turn to play, I play : ");
        System.out.println("Hand: " + hand);
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
    }
    
    @Override
    public void updateHand(CardSet newHand) {
        System.out.println("My new hand : " + newHand.toString());
    }
    
    @Override
    public void setTrump(Color trump) {
        System.out.println("Trump color : " + trump.toString());
    }
    
    @Override
    public void updateTrick(Trick newTrick) {
        System.out.println("Trick: " + newTrick);
    }
    
    @Override
    public void updateScore(Score score) {
        System.out.println("Scores: " + score.toString());
    }
    
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("The winning team is " + winningTeam.name());
    }

}