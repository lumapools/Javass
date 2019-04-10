package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Permet d'identifier chacune des deux équipes
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 */
public enum TeamId {
	TEAM_1, TEAM_2;

	/**
	 * une liste immuable contenant toutes les valeurs du type énuméré, dans l'ordre
	 * de déclaration (rang)
	 */
	public static final List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values()));

	/**
	 * Le nombre de valeurs du type énuméré
	 */
	public static final int COUNT = values().length;

	/**
	 * Retourne l'autre équipe que celle à laquelle on l'applique
	 * 
	 * @return (TeamId) l'autre équipe
	 */
	public TeamId other() {
		if (this.equals(TEAM_1)) {
			return TEAM_2;
		} else {
			return TEAM_1;
		}
	}
}