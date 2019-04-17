package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

/**
 * Représente le serveur d'un joueur, qui attend une connexion sur le port 5108
 * et pilote un joueur local en fonction des messages reçus
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class RemotePlayerServer {
    private Player player;

    /**
     * Constructeur public auquel on passe le joueur local, de type Player, dont
     * les différentes méthodes doivent être appelées en fonction des messages
     * reçus via le réseau
     * 
     * @param player (Player)
     *            joueur local
     */
    public RemotePlayerServer(Player player) {
        this.player = player;
    }

    /**
     * dans une boucle infinie :
     * 
     * 1. attend un message du client, 
     * 2. appelle la méthode correspondante du joueur local, et 
     * 3. dans le cas de cardToPlay, renvoie la valeur de
     * retour au client.
     * 
     */
    public void run() {
        try (ServerSocket s0 = new ServerSocket(5108);
                Socket s = s0.accept();
                BufferedReader r = new BufferedReader(new InputStreamReader(
                        s.getInputStream(), StandardCharsets.US_ASCII));
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                        s.getOutputStream(), StandardCharsets.US_ASCII))) {
            while (true) {
                readAndCall(r.readLine(), w);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // appelle la méthode correspondante du joueur local
    private void readAndCall(String s, BufferedWriter w) throws IOException {
        String[] ls = StringSerializer.split(' ', s);
        JassCommand msg = JassCommand.valueOf(ls[0]);
        switch (msg) {
        case PLRS:
            int ownId = StringSerializer.deserializeInt(ls[1]);
            String[] names = StringSerializer.split(',', ls[2]);
            Map<PlayerId, String> playerNames = new HashMap<>();
            for (int i = 0; i < 3; i++) {
                playerNames.put(PlayerId.ALL.get((ownId + i) % 4),
                        StringSerializer.deserializeString(names[i]));
            }
            player.setPlayers(PlayerId.ALL.get(ownId), playerNames);
            break;
        case TRMP:
            player.setTrump(
                    Color.ALL.get(StringSerializer.deserializeInt(ls[1])));
            break;
        case HAND:
            player.updateHand(
                    CardSet.ofPacked(StringSerializer.deserializeLong(ls[1])));
            break;
        case TRCK:
            player.updateTrick(
                    Trick.ofPacked(StringSerializer.deserializeInt(ls[1])));
            break;
        case CARD:
            String[] turnState = StringSerializer.split(',', ls[1]);
            Card c = player.cardToPlay(
                    TurnState.ofPackedComponents(
                            StringSerializer.deserializeLong(turnState[0]),
                            StringSerializer.deserializeLong(turnState[1]),
                            StringSerializer.deserializeInt(turnState[2])),
                    CardSet.ofPacked(StringSerializer.deserializeLong(ls[2])));
            w.write(StringSerializer.serializeInt(c.packed()));
            w.write('\n');
            w.flush();
            break;
        case SCOR:
            player.updateScore(
                    Score.ofPacked(StringSerializer.deserializeLong(ls[1])));
            break;
        case WINR:
            if (StringSerializer.deserializeInt(ls[1]) == 0) {
                player.setWinningTeam(TeamId.TEAM_1);
            }
            player.setWinningTeam(TeamId.TEAM_2);
            break;
        }
    }

}