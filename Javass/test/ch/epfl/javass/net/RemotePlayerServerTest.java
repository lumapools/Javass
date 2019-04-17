package ch.epfl.javass.net;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.RandomPlayer;

class RemotePlayerServerTest {

	@Test
	void serverWorks() {
		RemotePlayerServer p = new RemotePlayerServer(new RandomPlayer(2000));
		p.run();
	}

}
