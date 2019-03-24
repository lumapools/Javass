package ch.epfl.javass.jass;

import java.util.SplittableRandom;

/**
 * Cette classe représente un joueur simulé au moyen de l'algorithme MCTS
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class MctsPlayer implements Player{
    private PlayerId ownId;
    private long rngSeed;
    private int iterations;
    private SplittableRandom random;
    SplittableRandom rng = new SplittableRandom(0);
    
    
    /**
     * Construit un joueur simulé
     * @param ownId (PlayerId)
     *          l'identité
     * @param rngSeed (long)
     *          la graine aléatoire
     * @param iterations (int)
     *          le nombre d'itérations
     * @throw IllegalArgumentException 
     *          lorsque le nombre d'itérations est inférieur à 9
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) throws IllegalArgumentException {
        
    	if(iterations < 9) {
            throw new IllegalArgumentException();
        }
        this.ownId = ownId;
        this.rngSeed = rngSeed;
        this.iterations = iterations;
        this.random = new SplittableRandom(rng.nextLong());
        
    }
    
    private static class Node{
    	
        private TurnState turnState;
        private Node[] nodes;
        private CardSet cardSet;
        private int s_n;
        private int n_n;
        private Node p;
        
        private Node(TurnState turnState, Node[] nodes, CardSet cardSet, int s_n, int n_n, Node p) {
        	this.turnState = turnState;
        	this.nodes = nodes;
        	this.cardSet = cardSet;
        	this.s_n = s_n;
        	this.n_n = n_n;
        	this.p = p;
        }
        
        
        //TODO: Write this method
        private int bestSon() {return 0;}
        
        //TODO: Write this method
        private double computeV() {return 0;} 
        
    }
    
    
    /**
     * Simule une partie entière à partir d'unr turnState
     * @param turnState l'état du tout encours
     * @return (Score) le score final obtenu de turnState
     */
    public Score randomSimulate(TurnState turnState, CardSet myHand) {
    	
    	while(!turnState.unplayedCards().isEmpty()) {
    		System.out.println(turnState);
    		System.out.println("My hand: " + myHand);
    		if(!turnState.nextPlayer().equals(this.ownId)) {
    			CardSet playable = turnState.unplayedCards().difference(myHand);
    			Card card = playable.get(rng.nextInt(playable.size()));
    			
    			System.out.println("They are playing " + card);
    			turnState = turnState.withNewCardPlayedAndTrickCollected(card);
    		}
			else {
				CardSet playable = turnState.trick().playableCards(myHand);
				int randomCardIndex = rng.nextInt(playable.size());
				Card randomCardFromHand = myHand.get(randomCardIndex);
				System.out.println("I am playing: " + randomCardFromHand);
				turnState = turnState.withNewCardPlayedAndTrickCollected(randomCardFromHand);
				myHand = myHand.remove(randomCardFromHand);
			}
    	}
    	return turnState.score();
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        // TODO Auto-generated method stub
        return hand.get(0);
    }
    
   
   
    

}
