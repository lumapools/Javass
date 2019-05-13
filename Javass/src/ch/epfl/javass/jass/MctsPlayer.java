package ch.epfl.javass.jass;

import java.util.SplittableRandom;

import ch.epfl.javass.Preconditions;

public class MctsPlayer implements Player{
    private final PlayerId playerId;
    private final SplittableRandom rng;
    private final int iterations;

    
    /**
     * Construit un joueur simulé
     * 
     * @param playerId
     *            (PlayerId) l'identité
     * 
     * @param rngSeed
     *            (long) la graine aléatoire
     * 
     * @param iterations
     *            (int) le nombre d'itérations
     * 
     * @throws IllegalArgumentException
     *             (à partir de la classe Preconditions) lorsque le nombre
     *             d'itérations est inférieur à 9
     */
    public MctsPlayer(PlayerId playerId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);
        this.playerId = playerId;
        rng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }
    @Override
    public Card cardToPlay(TurnState turnState, CardSet hand) {
        Node root = new Node(turnState, hand, null, playerId);
        CardSet playableCards = turnState.trick().playableCards(hand);
        if (playableCards.size() == 1) {
            return playableCards.get(0);
        }
        for (int i = 0; i < iterations; ++i) {
            root.addChild(root, Node.C_FOR_GROW, rng);
        }
        return playableCards.get(root.indexBestChild(Node.C_FOR_WIN));
    }

    private static final class Node{
        private CardSet unexistingChildren;
        private Node parent;
        private TurnState turnState;
        private CardSet hand;
        private PlayerId playerId;
        private Node[] children;
        private int totalScorePerNode;
        private int numSimulations;
        private final static int C_FOR_WIN = 0;
        private final static int C_FOR_GROW = 40;
        
        private Node(TurnState turnState, CardSet hand, Node parent,
                PlayerId playerId) {
            this.parent = parent;
            this.turnState = turnState;
            this.hand = hand;
            this.playerId = playerId;
            if (!turnState.isTerminal()) {
                unexistingChildren = playableCards(turnState, hand);
            } else {
                unexistingChildren = CardSet.EMPTY;
            }
            totalScorePerNode = 0;
            numSimulations = 0;
            children = new Node[unexistingChildren.size()];
        }
        
        //Ensemble des cartes pas encore jouées dans le tour étant donné le turnState et la main du joueur
        private CardSet playableCards(TurnState turnState,
                CardSet hand) {
            return (turnState.nextPlayer().equals(this.playerId))
                  ? turnState.trick().playableCards(hand)
                          : turnState.trick().playableCards(turnState.unplayedCards().difference(hand));
        }
        
         //Retourne l'index du meilleur enfant, pour continuer à construire
         //l'arbre
        private int indexBestChild(int c) {
            int max = 0;
            for (int i = 0; i < children.length; i++) {
                if (computeV(children[i], c) > computeV(children[max],
                        c)) {
                    max = i;
                }
            }
            return max;
        }
        
        
        // Calcule V(n) du noeud
        private double computeV(Node n, int c) {
            return n.numSimulations > 0 ? ((double) n.totalScorePerNode / n.numSimulations + c * Math
                    .sqrt((double) 2 * Math.log(n.parent.numSimulations) / n.numSimulations))
                    : Double.POSITIVE_INFINITY;
        }

        
        //Remplit le tableau des enfants d'un noeud
        private void addChild(Node n, int c, SplittableRandom rng) {
            if (n.children.length != 0) {
                if (n.unexistingChildren.size() != 0) {
                    int index = n.children.length
                            - n.unexistingChildren.size();
                    Card cardToPlay = n
                            .playableCards(n.turnState, n.hand)
                            .get(index);
                    CardSet hand = n.hand.contains(cardToPlay)
                            ? n.hand.remove(cardToPlay)
                            : n.hand;
                    n.children[index] = new Node(n.turnState
                            .withNewCardPlayedAndTrickCollected(cardToPlay),
                            hand, n, playerId);
                    n.unexistingChildren = n.unexistingChildren
                            .remove(cardToPlay);
                    Node child = n.children[index];
                    updateScore(child, randomScore(child.turnState, child.hand, rng));
                } else {
                    addChild(n.children[n.indexBestChild(c)], c, rng);
                }
            } else {
                updateScore(n,randomScore(n.turnState, n.hand, rng));
            }
            
        }
        
        //Reparcourt tout l'arbre et met à jour tous les scores de tous les noeuds
        private void updateScore(Node n, Score score) {
            ++n.numSimulations;
            if (n.parent != null) {
                TeamId team = n.parent.turnState.nextPlayer().team();
                n.totalScorePerNode += (team.equals(playerId.team())? score.totalPoints(playerId.team()):
                    score.totalPoints(playerId.team().other()));
                updateScore(n.parent, score);
            }
        }

        private Score randomScore(TurnState turnState, CardSet hand,
                SplittableRandom rng) {
            while (!turnState.isTerminal()) {
                CardSet playableCards = playableCards(turnState,
                        hand);
                Card rndCard = playableCards
                        .get(rng.nextInt(playableCards.size()));
                hand = hand.remove(rndCard);
                turnState = turnState.withNewCardPlayedAndTrickCollected(rndCard);
            }
            return turnState.score();
        }

    }

}
