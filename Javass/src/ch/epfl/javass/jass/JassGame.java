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
    private boolean alreadySaid = false; //pour faire le setWinningTeam qu'une seule fois
    
    /**
     * Constructeur qui construit une partie de Jass
     * @param rngSeed (long)
     *          la graine aléatoire
     * @param players (Map<PlayerId, Player>)
     *          table associative l'identité-joueur
     * @param playerNames (Map<PlayerId, String>)
     *          table associative l'identité-nomDuJoueur
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections.unmodifiableMap(new EnumMap<>(playerNames));
        this.playerCardSet = new HashMap<>();
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        turnState = TurnState.ofPackedComponents(0L, 0L, 0);
    }
    
    /**
     * Cette méthode retourne vrai ssi la partie est terminée
     * @return (boolean)  vrai ssi la partie est terminée
     */
    public boolean isGameOver() {
        for (TeamId t: TeamId.ALL) {
            if(turnState.score().totalPoints(t) >= Jass.WINNING_POINTS) {
                if(!alreadySaid) {
                    setWinningTeam(t);
                    alreadySaid = true;
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Cette méthode fait avancer l'état du jeu jusqu'à la fin du prochain pli, ou ne fait rien si la partie est terminée
     */
    public void advanceToEndOfNextTrick() {
        if(isGameOver()) {
            return;
        }
        if(turnState.packedScore() == 0L && turnState.packedUnplayedCards() == 0L && turnState.packedTrick() == 0) {
            shuffleDeckAndDistribute();
            startNewGame();
        }else {
            turnState = turnState.withTrickCollected();
            updateScore();
            
        }
        if(isGameOver()) {
        } else {
            if(turnState.isTerminal()) {
                shuffleDeckAndDistribute();
                startNewTurn();
            }
            updateTrick();
            playerPlays();
        }
    }
    
    /**
     * Mélange les cartes et les distribue
     */
    private void shuffleDeckAndDistribute(){
        int deckSize = CardSet.ALL_CARDS.size();
        List<Card> deck = new ArrayList<>();
        for(int i=0; i< deckSize;i++)
            deck.add(CardSet.ALL_CARDS.get(i));
        Collections.shuffle(deck, shuffleRng);
        for(int i=0; i<4; i++ )
            playerCardSet.put(PlayerId.values()[i], CardSet.of(deck.subList(i*deckSize/4, (i+1)*deckSize/4)));
    }
    
    /**
     * Commence un nouveau tour
     */
    private void startNewTurn() {
        chooseAndSetTrump();
        for(PlayerId pId: PlayerId.ALL) {
            updateHand(pId);
        }
        firstPlayer = PlayerId.ALL.get((firstPlayer.ordinal()+1)%4);
        turnState = TurnState.initial(trump, turnState.score().nextTurn(), firstPlayer);
    }
    
    /**
     * Commence une nouvelle partie
     */
    private void startNewGame() {
        chooseAndSetTrump();
        for (Map.Entry<PlayerId, CardSet> e: playerCardSet.entrySet()) {
            players.get(e.getKey()).setPlayers(e.getKey(), playerNames);
            if(e.getValue().contains(Jass.STARTING_CARD))
                 firstPlayer = e.getKey();
        }
        for(PlayerId pId: PlayerId.ALL) {
            updateHand(pId);
            players.get(pId).setTrump(trump);
        }
        updateScore();
        turnState = TurnState.initial(trump, Score.INITIAL, firstPlayer);
    }
    
    /**
     * Fait jouer un joueur
     */
    private void playerPlays() {
        for (int i=0; i<4; ++i) {
                 Card c = players.get(turnState.nextPlayer()).cardToPlay(turnState, playerCardSet.get(turnState.nextPlayer()));
                 removeCard(turnState.nextPlayer(), c);
                 updateHand(turnState.nextPlayer());
                 turnState = turnState.withNewCardPlayed(c);
                 updateTrick();
                 
        }
    }
    
    /**
     * Met à jour la main du joueur voulu
     * @param playerId (PlayerId)
     * 			le joueur dont on veut mettre la main à jour
     */
    private void updateHand(PlayerId playerId) {
        players.get(playerId).updateHand(playerCardSet.get(playerId));
    }
    
    /**
     * Enlève une carte de la main d'un joueur
     * @param playerId (PlayerId)
     * 			l'identifiant du joueur 
     * @param card (Card)
     * 			la carte à enlever
     */
    private void removeCard(PlayerId playerId, Card card) {
        playerCardSet.put(playerId, playerCardSet.get(playerId).remove(card));
    }
    
    /**
     * Met à jour le pli
     */
    private void updateTrick() {
        for(PlayerId pId: PlayerId.ALL) {
            players.get(pId).updateTrick(turnState.trick());
        }
    }
    
    /**
     * Met à jour le score 
     */
    private void updateScore() {
        for(PlayerId pId: PlayerId.ALL) {
            players.get(pId).updateScore(turnState.score());
        }
    }
    
    /**
     * Donne l'atout aléatoirement
     */
    private void chooseAndSetTrump() {
        trump = Color.ALL.get(trumpRng.nextInt(4));
    }
    
    /**
     * Donne l'équipe gagnante selon chaque joueur
     * @param t (TeamId)
     * 			l'identifiant de l'équipe
     */
    private void setWinningTeam(TeamId t) {
        for(PlayerId pId: PlayerId.ALL) {
          players.get(pId).setWinningTeam(t);
      }
    }
}