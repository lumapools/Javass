package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Cette classe représente une partie de Jass
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class JassGame {
    private Map<PlayerId, Player> players;
    private Map<PlayerId, String> playerNames;
    private Map<PlayerId, CardSet> playerCardSet;
    private Random shuffleRng;
    private Random trumpRng;
    private TurnState turnState;
    private PlayerId firstPlayer;
    private Color trump;

    /**
     * Constructeur qui construit une partie de Jass
     * 
     * @param rngSeed
     *            (long) la graine aléatoire
     * @param players
     *            (Map<PlayerId, Player>) table associative l'identité-joueur
     * @param playerNames
     *            (Map<PlayerId, String>) table associative
     *            l'identité-nomDuJoueur
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {
        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections
                .unmodifiableMap(new EnumMap<>(playerNames));
        this.playerCardSet = new HashMap<>();
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        
    }

    /**
     * Cette méthode retourne vrai ssi la partie est terminée
     * 
     * @return (boolean) vrai ssi la partie est terminée
     */
    public boolean isGameOver() {
        return turnState != null && (turnState.score()
                .totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS
                || turnState.score()
                        .totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS);
    }

    /**
     * Cette méthode fait avancer l'état du jeu jusqu'à la fin du prochain pli,
     * ou ne fait rien si la partie est terminée
     */
    public void advanceToEndOfNextTrick() {
        if (isGameOver()) {
            return;
        }
        if (turnState == null) {
            startNewGame();
        } else {
            turnState = turnState.withTrickCollected();
        }
        if (isGameOver()) {
            for (TeamId t : TeamId.ALL)
                if (turnState.score().totalPoints(t) >= Jass.WINNING_POINTS) {
                    updateScore();
                    setWinningTeam(t);
                }
            return;
        }
        if (turnState.isTerminal()) {
            startNewTurn();
        }
        updateScore();
        updateTrick();
        playerPlays();

    }

    private void shuffleDeckAndDistribute() {
        List<Card> deck = new ArrayList<>();
        for (Color c : Color.ALL) {
            for (Rank r : Rank.ALL) {
                deck.add(Card.of(c, r));
            }
        }
        Collections.shuffle(deck, shuffleRng);
        playerCardSet.put(PlayerId.PLAYER_1, CardSet.of(deck.subList(0, 9)));
        playerCardSet.put(PlayerId.PLAYER_2, CardSet.of(deck.subList(9, 18)));
        playerCardSet.put(PlayerId.PLAYER_3, CardSet.of(deck.subList(18, 27)));
        playerCardSet.put(PlayerId.PLAYER_4, CardSet.of(deck.subList(27, 36)));
    }

    private void startNewTurn() {
        shuffleDeckAndDistribute();
        chooseAndSetTrump();
        for (PlayerId pId : PlayerId.ALL) {
            updateHand(pId);
        }
        setTrumpPlayer();
        firstPlayer = PlayerId.ALL.get((firstPlayer.ordinal() + 1) % 4);
        turnState = TurnState.initial(trump, turnState.score().nextTurn(),
                firstPlayer);
    }

    private void startNewGame() {
        shuffleDeckAndDistribute();
        chooseAndSetTrump();
        for (Map.Entry<PlayerId, CardSet> e : playerCardSet.entrySet()) {
            players.get(e.getKey()).setPlayers(e.getKey(), playerNames);
            if (e.getValue().contains(Card.of(Color.DIAMOND, Rank.SEVEN)))
                firstPlayer = e.getKey();
        }
        for (PlayerId pId : PlayerId.ALL) {
            updateHand(pId);
        }
        setTrumpPlayer();
        turnState = TurnState.initial(trump, Score.INITIAL, firstPlayer);
    }

    private void playerPlays() {
        for (int i = 0; i < 4; ++i) {
            Card c = players.get(turnState.nextPlayer()).cardToPlay(turnState,
                    playerCardSet.get(turnState.nextPlayer()));
            removeCard(turnState.nextPlayer(), c);
            updateHand(turnState.nextPlayer());
            turnState = turnState.withNewCardPlayed(c);
            updateTrick();
        }
    }

    private void updateHand(PlayerId playerId) {
        players.get(playerId).updateHand(playerCardSet.get(playerId));
    }

    private void setTrumpPlayer() {
        for (Player player : players.values()) {
            player.setTrump(trump);
        }
    }

    private void removeCard(PlayerId playerId, Card card) {
        playerCardSet.put(playerId, playerCardSet.get(playerId).remove(card));
    }

    private void updateTrick() {
        for (Player player : players.values()) {
            player.updateTrick(turnState.trick());
        }
    }

    private void updateScore() {
        for (Player player : players.values()) {
            player.updateScore(turnState.score());
        }
    }

    private void chooseAndSetTrump() {
        trump = Color.ALL.get(trumpRng.nextInt(4));
    }

    private void setWinningTeam(TeamId t) {
        for (Player player : players.values()) {
            player.setWinningTeam(t);
        }
    }
}