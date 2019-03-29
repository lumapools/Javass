package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Permet d'identifier chacun des quatre joueurs
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;
    
    /**
     * une liste immuable contenant toutes les valeurs du type 
     * énuméré, dans l'ordre de déclaration (rang)
     */
    public static final List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    
    /**
     * Le nombre de valeurs du type énuméré
     */
    public static final int COUNT = values().length;
    
    /**
     * Retourne (l'identité de) l'équipe à laquelle appartient le joueur auquel on l'applique
     * @return (TeamId) l'identité de l'équipe
     */
    public TeamId team() {
        if(this.equals(PLAYER_1) || this.equals(PLAYER_3)) {
            return TeamId.TEAM_1;
        }
        return TeamId.TEAM_2;
    }
}
