package ch.epfl.javass.net;

import java.io.IOException;

import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.RandomPlayer;

/**
 * test thus non Jdoc 
 * 
 * IMPORTANT : LANCER D'ABORD servernetgame PUIS netjassgame
 *
 *

 */
public class ServerNetGame {
    public static void main(String[] args) throws IOException {
        Player player = new RandomPlayer(2019);
        RemotePlayerServer gali =  new RemotePlayerServer(player);
        gali.run();
        System.out.println("on devrait pas arriver la");
    }
}