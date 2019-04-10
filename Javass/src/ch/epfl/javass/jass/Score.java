package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * Représente les scores d'une partie de Jass
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 */

public final class Score {
	// Représentation de la version empaquetée du score
	private final long packed;

	/**
	 * Score initial
	 */
	public static final Score INITIAL = new Score(PackedScore.INITIAL);

	private Score(long packed) {
		this.packed = packed;
	}

	/**
	 * Retourne le score dont packed est la version empaquetée, ou lève l'exception
	 * IllegalArgumentException si cet argument ne représente pas un score empaqueté
	 * valide
	 * 
	 * @param packed (long) version empaquetée de la carte
	 * @return (Score) le score
	 */
	public static Score ofPacked(long packed) {
		Preconditions.checkArgument(PackedScore.isValid(packed));
		return new Score(packed);
	}

	/**
	 * Retourne la version empaquetée des scores
	 * 
	 * @return (long) version empaquetée des scores
	 */
	public long packed() {
		return packed;
	}

	/**
	 * Retourne le nombre de plis remportés par l'équipe donnée dans le tour courant
	 * du récepteur
	 * 
	 * @param t (TeamId) l'équipe
	 * @return (int) le nombre de plis
	 */
	public int turnTricks(TeamId t) {
		return PackedScore.turnTricks(packed(), t);
	}

	/**
	 * Retourne le nombre de points remportés par l'équipe donnée dans le tour
	 * courant du récepteur
	 * 
	 * @param t (TeamId) l'équipe
	 * @return (int) le nombre de points
	 */
	public int turnPoints(TeamId t) {
		return PackedScore.turnPoints(packed(), t);
	}

	/**
	 * Retourne le nombre de points reportés par l'équipe donnée dans les tours
	 * précédents (sans inclure le tour courant) du récepteur,
	 * 
	 * @param t (TeamId) l'équipe
	 * @return (int) le nombre de points total sans le tour courant
	 */
	public int gamePoints(TeamId t) {
		return PackedScore.gamePoints(packed(), t);
	}

	/**
	 * Retourne le nombre total de points remportés par l'équipe donnée dans la
	 * partie courante du récepteur
	 * 
	 * @param t (TeamId) l'équipe
	 * @return (int) total des points
	 */
	public int totalPoints(TeamId t) {
		return PackedScore.totalPoints(packed(), t);
	}

	/**
	 * Retourne le score dont le score empaqueté mis à jour pour tenir compte du
	 * fait que l'équipe winningTeam a remporté un pli valant trickPoints points ou
	 * lève l'exception IllegalArgumentException si le nombre de points donné est
	 * inférieur à 0
	 * 
	 * @param winningTeam (TeamId) l'équipe qui a gagné la pli
	 * @param trickPoints (int) le nombre de points de la pli                                 0
	 * @return (Score) score mis à jour
	 */
	public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
		Preconditions.checkArgument(trickPoints >= 0);
		return new Score(PackedScore.withAdditionalTrick(packed(), winningTeam, trickPoints));
	}

	/**
	 * Retourne le score mis à jour pour le tour prochain
	 * 
	 * @return (Score) score pour le prochain tour
	 */
	public Score nextTurn() {
		return new Score(PackedScore.nextTurn(packed()));
	}

	/**
	 * @return (boolean) vrai si le score est égal
	 */
	@Override
	public boolean equals(Object that) {
		return that instanceof Score && ((Score) that).packed() == this.packed();
	}

	/**
	 * @return (int) le résultat de la méthode hashCode de la classe Long
	 */
	@Override
	public int hashCode() {
		return Long.hashCode(packed());
	}

	/**
	 * @return (String) (plis, partie courante, gamePoints, totalPoints)
	 */
	@Override
	public String toString() {
		return PackedScore.toString(packed());
	}

}