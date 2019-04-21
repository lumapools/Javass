package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Map;
import static java.nio.charset.StandardCharsets.US_ASCII;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

public class RemotePlayerClient implements Player, AutoCloseable {
	BufferedReader r;
	BufferedWriter w;
	Socket s;
	public static final int PORT_NUMBER = 5108;
	
	
	public RemotePlayerClient(String socketName, int port) throws Exception {
		s = new Socket(socketName, port);
		r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
		w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII));
	}

	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {
		String serializedScore = StringSerializer.serializeLong(state.packedScore());
		String serializedUnplayedCards = StringSerializer.serializeLong(state.packedUnplayedCards());
		String serializedTrick = StringSerializer.serializeInt(state.packedTrick());
		String serializedHand = StringSerializer.serializeLong(hand.packed());
		String combinedState = StringSerializer.combine(',', serializedScore, serializedUnplayedCards, serializedTrick);
		String toSend = StringSerializer.combine(' ', JassCommand.CARD.name(), combinedState, serializedHand);
		send(toSend);
		
		String msgFromServer = null;
		try {
			msgFromServer = r.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Card.ofPacked(StringSerializer.deserializeInt(msgFromServer));
		
		

	}

	@Override
	public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
		String serPlayer1 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_1));
		String serPlayer2 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_2));
		String serPlayer3 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_3));
		String serPlayer4 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_4));
		String serIntId = StringSerializer.serializeInt(ownId.ordinal());
		
		String serAllPlayers = StringSerializer.combine(',', serPlayer1, serPlayer2, serPlayer3, serPlayer4);
		String toSend = StringSerializer.combine(' ', JassCommand.PLRS.name(), serIntId, serAllPlayers);
		send(toSend);
	}

	@Override
	public void updateHand(CardSet newHand) {
		String serHandLong = StringSerializer.serializeLong(newHand.packed());
		String toSend = StringSerializer.combine(' ', JassCommand.HAND.name(), serHandLong);
		send(toSend);
	}

	@Override
	public void setTrump(Color trump) {
		String serTrmpInt = StringSerializer.serializeInt(trump.ordinal());
		String toSend = StringSerializer.combine(' ', JassCommand.TRMP.name(), serTrmpInt);
		send(toSend);
	}

	@Override
	public void updateTrick(Trick newTrick) {
		String serTrickInt = StringSerializer.serializeInt(newTrick.packed());
		String toSend = StringSerializer.combine(' ', JassCommand.TRCK.name(), serTrickInt);
		send(toSend);
	}

	@Override
	public void updateScore(Score score) {
		String serScoreLong = StringSerializer.serializeLong(score.packed());
		String toSend = StringSerializer.combine(' ', JassCommand.SCOR.name(), serScoreLong);
		send(toSend);
	}

	@Override
	public void setWinningTeam(TeamId winningTeam) {
		String serWinTeamId = StringSerializer.serializeInt(winningTeam.ordinal());
		String toSend = StringSerializer.combine(' ', JassCommand.WINR.name(), serWinTeamId);
		send(toSend);
		
		
	}
	
	private void send(String message) {
		try {
			w.write(message);
			w.write("\n");
			w.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		w.close();
		r.close();
		s.close();
	}

}
