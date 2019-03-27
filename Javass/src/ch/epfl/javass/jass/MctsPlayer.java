package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.MctsPlayer.Node;

/**
 * Cette classe représente un joueur simulé au moyen de l'algorithme MCTS
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class MctsPlayer implements Player{
    private PlayerId ownId;
    private long rngSeed;
    public int iterations;
    SplittableRandom rng;
    
    
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
    	this.rng = new SplittableRandom(rngSeed);
        this.ownId = ownId;
        this.rngSeed = rngSeed;
        this.iterations = iterations;
        
    }
    
    public static class Node{
    	
        private TurnState turnState;
        private Node[] children;
        private CardSet embryos;
        private int totalScorePerNode;
        private int numSimulations;
        private Card lastPlayedCard;

        
        public Node(TurnState turnState, Card lastPlayedCard) {
        	MctsPlayer dummy = new MctsPlayer(PlayerId.PLAYER_1, 0, 10);
        	this.turnState = turnState;
        	this.embryos = embryos();
        	children = new Node[embryos.size()];
        	this.lastPlayedCard = lastPlayedCard;
        	
        }
        
        public CardSet embryos() {
        	return turnState.unplayedCards();
        }
        
        
        public Card cardPlayed() {
        	return turnState.trick().card(0);
        }
        
        
        /**
         * Retourne le meilleur enfant, pour continuer à construire l'arbre
         * @param c
         * @return
         */
        private int bestBranchFollowChild(int c) {
        	int max = 0;
        	for(int i = 0; i < children.length; i++) {
        		if(children[i].computeV(c, this) > children[max].computeV(c, this)) {
        			max = i;
        		}
        	}
        	return max;
        }
        
        //TODO: Write this method
        private double computeV(int c, Node parent) {
        	if(numSimulations <= 0) {
        		return Double.POSITIVE_INFINITY;
        	}
        	else {
        		return totalScorePerNode/numSimulations + c * Math.sqrt(2*Math.log(parent.numSimulations)/(numSimulations));
        	}
        } 
        
        @Override
        public String toString() {
        	if(!turnState.isTerminal())
        		return String.format("[%.2f, %d] %s ========= %s", 
        				(double)totalScorePerNode/(double)numSimulations, 
        				numSimulations, turnState.trick().toString(), embryos);
        	return "";
        }
        
        public void print(String prefix) {
        	System.out.print(prefix);
        	System.out.println(toString());
	        for(Node child: children) {
	        	if(child != null) {
	        		child.print("   " + prefix);
	        	}
	        	
        	}

        }
        
        public void print(int maxDepth) {
        	print(0, maxDepth);
        }
        
        
        public void print(int depth, int maxDepth) {
        	if(depth > maxDepth) {
        		return;
        	}
        	for(int i = 0; i < depth; i++) {
        		System.out.print("   ");
        	}
        	System.out.println(toString());
	        for(Node child: children) {
	        	if(child != null) {
	        		child.print(depth + 1, maxDepth);
	        	}
	        	
        	}

        }
        
        
        public Node addChild(Card card) {
        	for(int i = 0; i < children.length; i++) {
        		if(children[i] == null) {
        			children[i] = new Node(turnState.withNewCardPlayedAndTrickCollected(card), card);
        			embryos = embryos.remove(card);
        			return children[i];
        			
        		}
        	}
        	throw new IllegalStateException("Children Full");
        }
        
        
        
        
        public boolean hasEmbryos() {
        	return !embryos.equals(CardSet.EMPTY);
        }
        
        
        public TurnState getTurnState() {
        	return turnState;
        }
        
    }
    
    
    /**
     * Simule une partie entière à partir d'unr turnState
     * @param turnState l'état du tout encours
     * @return (Score) le score final obtenu de turnState
     */
    public Score randomSimulate(TurnState turnState, CardSet myHand) {
	    	while(!turnState.isTerminal()) {
	    		//System.out.println(turnState);
	    		//System.out.println("My hand: " + myHand);
	    		if(!turnState.nextPlayer().equals(this.ownId)) {
	    			CardSet playable = turnState.unplayedCards().difference(myHand);
	    			Card card = playable.get(rng.nextInt(playable.size()));
	    			
	    			turnState = turnState.withNewCardPlayedAndTrickCollected(card);
	    		}
				else {
					
					CardSet playable = turnState.trick().playableCards(myHand);
					int randomCardIndex = rng.nextInt(playable.size());
					Card randomCardFromHand = myHand.get(randomCardIndex);
					turnState = turnState.withNewCardPlayedAndTrickCollected(randomCardFromHand);
					myHand = myHand.remove(randomCardFromHand);
				}
	    	}
	    	return turnState.score();
    	
    }
    
    public Score randomSimulatePrimitiveMcts(TurnState turnState) {
    	//while(!turnState.unplayedCards().isEmpty()) {
    	while(!turnState.isTerminal()) {
			CardSet playable = turnState.unplayedCards();
			Card card = playable.get(rng.nextInt(playable.size()));
			turnState = turnState.withNewCardPlayedAndTrickCollected(card);
		}
    	long packedScore = turnState.packedScore();
    	Score score = Score.ofPacked(packedScore);
		return score;
    }
    
    /**
     * 
     * @param root
     * @return
     */
    public List<Node> growTreeByOneNode(Node root) {
    	List<Node> path = new ArrayList<Node>();
    	while (true) {
    		path.add(root);
        	if(root.hasEmbryos()) {
        		path.add(root.addChild(root.embryos.get(0)));
        		return path;
        	}
        	if(root.children.length == 0) {
        		return Collections.EMPTY_LIST;
        		
    		}
        	else {
        		root = root.children[root.bestBranchFollowChild(40)];
        	}		
    	}    	
    }
    
    
    public void computeAndUpdateScores(List<Node> path) {
    
		if(!path.isEmpty()) {
			Node lastNode = path.get(path.size()-1);
	    	Score lastScore = this.randomSimulatePrimitiveMcts(lastNode.turnState);
	    	for(Node n: path) { 
	    		n.totalScorePerNode += lastScore.totalPoints(TeamId.TEAM_1);
	    		n.numSimulations += 1;
	    	}
		}
    	
	}
    	
    
    

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
    	System.out.println(state);
    	Node root = new Node(state, null);
		List<Node> path;
		for(int i = 0; i < iterations; i++) {
			if(!root.turnState.isTerminal()) {
				path = growTreeByOneNode(root);
				computeAndUpdateScores(path);
			}
			
		}
		return root.children[root.bestBranchFollowChild(0)].lastPlayedCard;
    }
    
   
   
    

}
