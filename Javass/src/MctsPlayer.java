import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TurnState;

public class MctsPlayer implements Player {

	PlayerId ownId;
	long rngSeed;
	int iterations;
	
	public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
		if(iterations < 9) {
			throw new IllegalArgumentException();
		}
		this.ownId = ownId;
		this.rngSeed = rngSeed;
		this.iterations = iterations;
		
	}
	
	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {
		// TODO Auto-generated method stub
		return null;
	}

}
