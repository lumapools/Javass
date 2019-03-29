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

public final class SilentPlayer implements Player{
    private final Player player;
    
    public SilentPlayer(Player underlyingPlayer) {
        this.player = underlyingPlayer;
    }
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Card c = player.cardToPlay(state, hand);
        return c;
    }
    
    
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("The winning team is " + winningTeam.name());
    }
    

}