package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

import ch.epfl.javass.Preconditions;

/**
 * Cette classe représente un joueur simulé au moyen de l'algorithme MCTS
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class MctsPlayer implements Player {
	private final PlayerId ownId;
	private final int iterations;
	private final SplittableRandom rng;

	/**
	 * Construit un joueur simulé
	 * 
	 * @param ownId      (PlayerId) l'identité
	 * 
	 * @param rngSeed    (long) la graine aléatoire
	 * 
	 * @param iterations (int) le nombre d'itérations
	 * 
	 * @throws IllegalArgumentException lorsque le nombre d'itérations est inférieur
	 *                                  à 9
	 */
	public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
		Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);
		this.rng = new SplittableRandom(rngSeed);
		this.ownId = ownId;
		this.iterations = iterations;
	}

	/*
	 * Simule une partie entière à partir d'un turnState
	 * 
	 * @param turnState (TurnState) l'état du tout en cours
	 * 
	 * @param myHand (CardSet) la main du joueur
	 * 
	 * @return (Score) le score final obtenu de turnState
	 */
	private Score randomSimulate(TurnState turnState, CardSet myHand) {
		while (!turnState.isTerminal()) {
			if (!turnState.nextPlayer().equals(ownId)) {
				CardSet playable = turnState.unplayedCards().difference(myHand);
				Card card = playable.get(rng.nextInt(playable.size()));
				turnState = turnState.withNewCardPlayedAndTrickCollected(card);
			} else {
				CardSet playable = turnState.trick().playableCards(myHand);
				int randomCardIndex = rng.nextInt(playable.size());
				Card randomCardFromHand = playable.get(randomCardIndex);
				turnState = turnState.withNewCardPlayedAndTrickCollected(randomCardFromHand);
				myHand = myHand.remove(randomCardFromHand);
			}
		}
		return turnState.score();
	}

	/*
	 * Ajoute un noeud au noeud parent pour l'enfant qui a la valeur V(n) la plus
	 * élevée
	 * 
	 * @param root (Node) la racine du noeud
	 * 
	 * @return (List<Node>) Le chemin qui mène au noeud créé, ou une liste vide si
	 * on ne peut pas continuer sur l'arbre
	 */
	private List<Node> growTreeByOneNode(Node root) {
		List<Node> path = new ArrayList<Node>();
		while (true) {
			if (!root.turnState.isTerminal()) {
				path.add(root);
				if (root.hasEmbryos()) {
					path.add(root.addChild(root.embryos.get(0)));
					return path;
				}
				if (root.children.length == 0) {
					return Collections.emptyList();
				} else {
					root = root.children[root.bestBranchFollowChild(Node.C_FOR_GROW)];
				}
			} else {
				return path;
			}
		}
	}

	/*
	 * Reparcourt tout l'arbre et met à jour tous les scores de tous les noeuds
	 * 
	 * @param path (List<Node>) le chemin à parcourir
	 * 
	 * @param hand (CardSet) la main du joueur
	 */

	private void computeAndUpdateScores(List<Node> path, CardSet hand) {
		if (!path.isEmpty()) {
			Node lastNode = path.get(path.size() - 1);
			Score lastScore = this.randomSimulate(lastNode.turnState, lastNode.hand);
			TeamId teamId = ownId.team();
			path.get(0).numSimulations++;
			for (int i = 1; i < path.size(); i++) {
				if (path.get(i - 1).turnState.nextPlayer().team().equals(teamId)) {
					path.get(i).totalScorePerNode += lastScore.totalPoints(teamId);
				} else {
					path.get(i).totalScorePerNode += lastScore.totalPoints(teamId.other());
				}
				path.get(i).numSimulations += 1;
			}
		}
	}

	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {
		Node root = new Node(state, hand, null, ownId);
		List<Node> path;
		for (int i = 0; i < iterations; i++) {
			if (!root.turnState.isTerminal()) {
				path = growTreeByOneNode(root);
				computeAndUpdateScores(path, hand);
			}
		}
		return root.children[root.bestBranchFollowChild(Node.C_FOR_WIN)].lastPlayedCard;

	}

	private static class Node {

		private TurnState turnState;
		private CardSet hand;
		private PlayerId playerId;
		private Node[] children;
		private CardSet embryos;
		private int totalScorePerNode;
		private int numSimulations;
		private Card lastPlayedCard;
		private final static int C_FOR_WIN = 0;
		private final static int C_FOR_GROW = 40;

		private Node(TurnState turnState, CardSet hand, Card lastPlayedCard, PlayerId playerId) {
			this.turnState = turnState;
			this.hand = hand;
			this.playerId = playerId;
			this.lastPlayedCard = lastPlayedCard;
			if (!turnState.isTerminal()) {
				this.embryos = embryos();
				children = new Node[embryos.size()];
			} else {
				this.embryos = CardSet.EMPTY;
			}
		}

		/*
		 * Ensemble des cartes pas encore jouées dans le tour
		 * 
		 * @return (CardSet) l'ensemble des cartes pas encore jouées durant la partie
		 */
		private CardSet embryos() {
			return (turnState.nextPlayer().equals(this.playerId)) ? turnState.trick().playableCards(hand)
					: turnState.unplayedCards().difference(hand);
		}

		/*
		 * Retourne l'index du meilleur enfant, pour continuer à construire l'arbre
		 * 
		 * @param c (int) (constante pour calculer la meilleure branche avec laquelle
		 * continuer
		 * 
		 * @return (int) l'index du meilleur enfant
		 */
		private int bestBranchFollowChild(int c) {
			int max = 0;
			for (int i = 0; i < children.length; i++) {
				if (children[i].computeV(c, this) > children[max].computeV(c, this)) {
					max = i;
				}
			}
			return max;
		}

		/*
		 * Calcule V(n) du noeud
		 * 
		 * @param c (int) constante utilisée pour calculer v
		 * 
		 * @param parent (Node) le noeud parent de ce noeud
		 * 
		 * @return (double) la valeur de V(n)
		 */
		private double computeV(int c, Node parent) {
			return (numSimulations <= 0) ? Double.POSITIVE_INFINITY
					: (double) totalScorePerNode / (double) numSimulations
							+ c * Math.sqrt(2 * Math.log((double) parent.numSimulations) / (double) numSimulations);
		}

		/*
		 * Remplit le tableau des enfants d'un noeud
		 * 
		 * @param card (Card) la carte jouée
		 * 
		 * @return (Node) le nouveau noeud crée à partir de cette insertion des enfants
		 * dans le tableau
		 */
		private Node addChild(Card card) {
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null) {
					TurnState childsState = turnState.withNewCardPlayedAndTrickCollected(card);
					CardSet childsHand = hand;
					if (turnState.nextPlayer().equals(playerId)) {
						childsHand = childsHand.remove(card);
					}
					children[i] = new Node(childsState, childsHand, card, playerId);
					embryos = embryos.remove(card);
					return children[i];
				}
			}
			throw new IllegalStateException("Cannot add new child: Children Full");
		}

		/*
		 * Check si la liste des cartes injouées est vide
		 * 
		 * @return (boolean) vrai si pas toutes les cartes ont été jouées, faux sinon
		 */
		private boolean hasEmbryos() {
			return !embryos.equals(CardSet.EMPTY);
		}
	}

}