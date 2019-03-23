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
        SplittableRandom rng = new SplittableRandom(rngSeed);
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
        
        
        private int bestSon() {
        	int max = 0;
     	   for(int i = 0; i < nodes.length; i++) {
     		   if(nodes[i].calculV(nodes[i].s_n, nodes[i].n_n, p.calculV(, i, n_p, c), 40) > nodes[max].calculV()) {
     			   max = i;
     		   }
     	   } 
     	   return max;
        }
        
        
        private double calculV() {
            if(n_n > 0) {
                return (s_n/n_n) + 40 * Math.sqrt((2*Math.log(n_p))/n_n);
            }
            else {
                return Double.POSITIVE_INFINITY;
            }
        }
        
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        // TODO Auto-generated method stub
        return hand.get(0);
    }
    
   
   
    

}
