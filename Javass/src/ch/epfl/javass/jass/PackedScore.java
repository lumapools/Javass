package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * Contient des méthodes statiques permettant de manipuler les scores d'une partie
 * de Jass empaquetés dans un entier de type long
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 */
public final class PackedScore {
    /**
     * score début de partie
     */
    public static final long INITIAL = 0L;
    
    /*
     * constantes de score
     */
    private static final long MAX_TRICKS_PER_TURN = 9L;
    private static final long MAX_TURN_POINTS = 257L;
    private static final long MAX_GAME_POINTS = 2000L;
    private static final int INITIAL_NB_TRICKS = 0;
    private static final int INITIAL_TURN_POINTS = 0;

    /*
     * constantes de bits utilisés
     */
    private static final int SCORE_PER_TEAM_BITS = 32;
    private static final int NB_TRICK_BITS = 4;
    private static final int NB_TURN_POINTS_BITS = 9;
    private static final int NB_GAME_POINTS_BITS = 11;
    
    /*
     * constantes de bits inutilisés
     */
    private static final int FIRST_UNUSED_BITS_START = 24;
    private static final int SECOND_UNUSED_BITS_START = 56;
    private static final int NB_UNUSED_BITS = 8;
    
    private static final int TRICK_BITS_START_T1 = 0;
    private static final int TURN_POINTS_START_T1 = 4;
    private static final int GAME_POINTS_START_T1 = 13;
    private static final int TRICK_BITS_START_T2 = 32;
    private static final int TURN_POINTS_START_T2 = 36;
    private static final int GAME_POINTS_START_T2 = 45;
    
    /**
     * Constructeur privé, car non instantiable
     */
    private PackedScore() {}
    
    /**
     * Check si un score est valide ou pas (si les bits pour représenter le nb correspondent à un nb de plis valide,
     * même chose pour les points du tour et même chose pour les points totaux
     * et si les bits inutilisés sont tous des 0), et cela pour les 2 équipes
     * 
     * @param pkScore (long) 
     *          score sous forme long (séquence de 64 bits)
     * @return (boolean) la valeur de la validité d'un score
     */
    public static boolean isValid(long pkScore) {
        if (Bits64.extract(pkScore, TRICK_BITS_START_T1, NB_TRICK_BITS) > MAX_TRICKS_PER_TURN ||
                Bits64.extract(pkScore, TURN_POINTS_START_T1, NB_TURN_POINTS_BITS) > MAX_TURN_POINTS ||
                Bits64.extract(pkScore, GAME_POINTS_START_T1, NB_GAME_POINTS_BITS) > MAX_GAME_POINTS ||
                Bits64.extract(pkScore, TRICK_BITS_START_T2, NB_TRICK_BITS) > MAX_TRICKS_PER_TURN ||
                Bits64.extract(pkScore, TURN_POINTS_START_T2, NB_TURN_POINTS_BITS) > MAX_TURN_POINTS ||
                Bits64.extract(pkScore, GAME_POINTS_START_T2, NB_GAME_POINTS_BITS) > MAX_GAME_POINTS ||
                Bits64.extract(pkScore, FIRST_UNUSED_BITS_START, NB_UNUSED_BITS) != 0 || 
                Bits64.extract(pkScore, SECOND_UNUSED_BITS_START, NB_UNUSED_BITS) != 0) {
                
                return false;   
            }
            return true;
    }
    
    /**
     * 
     * @param turnTricks1 (int)
     *          nb de plis remportés par l'équipe 1
     * @param turnPoints1 (int)
     *          nb de points remportée par l'équipe 1 dans le tour courant
     * @param gamePoints1 (int)
     *          nb de points remportés par l'équipe 1 au total
     * @param turnTricks2 (int)
     *          nb de plis remportés par l'équipe 2
     * @param turnPoints2 (int)
     *          nb de points remportée par l'équipe 2 dans le tour courant
     * @param gamePoints2 (int)
     *          nb de points remportés par l'équipe 2 au total
     * @return (long) le score des 2 équipes empaqueté dans une séquence de 64 bits
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {
        long pkScore1 = (long) Bits32.pack(turnTricks1, NB_TRICK_BITS, turnPoints1, NB_TURN_POINTS_BITS, gamePoints1, NB_GAME_POINTS_BITS);
        long pkScore2 = (long) Bits32.pack(turnTricks2, NB_TRICK_BITS, turnPoints2, NB_TURN_POINTS_BITS, gamePoints2, NB_GAME_POINTS_BITS);
        return Bits64.pack(pkScore1, SCORE_PER_TEAM_BITS, pkScore2, SCORE_PER_TEAM_BITS);
        
    }
    /**
     * Retourne le nombre de plis remportés par l'équipe donnée dans le tour courant des scores empaquetés donnés
     * @param pkScore(long)
     *          score empaqueté
     * @param t(TeamId)
     *          l'équipe de laquelle on veut le nombre de plis remportés
     * @return(int) le nombre de plis remportés
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert(isValid(pkScore));
        if(t.equals(TeamId.TEAM_1)) {
            return (int)Bits64.extract(pkScore, TRICK_BITS_START_T1, NB_TRICK_BITS);
        }
        else {
            return (int)Bits64.extract(pkScore, TRICK_BITS_START_T2, NB_TRICK_BITS);
        }
    }
    
    /**
     * Retourne le nombre de points remportés par l'équipe donnée dans le tour courant des scores empaquetés donnés
     * @param pkScore(long)
     *          score empaqueté
     * @param t(TeamId)
     *          l'équipe de laquelle on veut les points remportés
     * @return(int) nombre de points remportés dans le tour courant
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert(isValid(pkScore));
        if(t.equals(TeamId.TEAM_1)) {
            return (int)Bits64.extract(pkScore, TURN_POINTS_START_T1, NB_TURN_POINTS_BITS);
        }
        else {
            return (int)Bits64.extract(pkScore, TURN_POINTS_START_T2, NB_TURN_POINTS_BITS);
        }
    }
    
    /**
     * Retourne le nombre de points remportés par l'équipe donnée dans les tours précédents (sans inclure le tour courant) des scores empaquetés donnés,
     * @param pkScore(long)
     *          score empaqueté
     * @param t(TeamId)
     *          l'équipe de laquelle on veut les points remportés
     * @return (int) nombre de points remportés dans les tours précédents
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert(isValid(pkScore));
        if(t.equals(TeamId.TEAM_1)) {
            return (int)Bits64.extract(pkScore, GAME_POINTS_START_T1, NB_GAME_POINTS_BITS);
        }
        else {
            return (int)Bits64.extract(pkScore, GAME_POINTS_START_T2, NB_GAME_POINTS_BITS);
        }
    }
    
    /**
     * Retourne le nombre total des points remportés par l'équipe donnée dans la partie courante des scores empaquetés donnés
     * @param pkScore(long)
     *          le score empaqueté
     * @param t(TeamId)
     *          l'équipe de laquelle on veut le nombre de points total
     * @return(int) le nombre total
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert(isValid(pkScore));
        return gamePoints(pkScore, t) + turnPoints(pkScore, t);
    }
    
    /**
     * Met à jour les scores de la partie (sans mettre le score total de l'équipe é jour),
     * et si le nombre de plis gagnés est égal à 9, alors on ajoute le nb de points additionels (MATCH_ADDITIONAL_POINTS)
     * aux points du tour de l'équipe qui a fait match
     * 
     * @param pkScore (long)
     *          le score actuel empaqueté sous forme de séquence de 64 bits
     * @param winningTeam (TeamId)
     *          l'équipe qui gagne le pli
     * @param trickPoints (int)
     *          les points gagnés durant ce tour
     * @return (long) le score actuel empaqueté sous forme de séquence de 64 bits (sans avoir mis à jour le score total des 2 équipes)
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {

        assert(isValid(pkScore));
        int tricksUpdated = turnTricks(pkScore, winningTeam) + 1;
        int scoreUpdated = turnPoints(pkScore, winningTeam) + trickPoints;

        int turnTricks1;
        int turnPoints1;
        int gamePoints1;
        int turnTricks2;
        int turnPoints2;
        int gamePoints2;

        if(winningTeam.equals(TeamId.TEAM_1)){
            turnTricks1 = tricksUpdated;
            turnPoints1 = scoreUpdated;
            gamePoints1 = gamePoints(pkScore, TeamId.TEAM_1);
            turnTricks2 = turnTricks(pkScore, TeamId.TEAM_2);
            turnPoints2 = turnPoints(pkScore, TeamId.TEAM_2);
            gamePoints2 = gamePoints(pkScore, TeamId.TEAM_2);
            if(tricksUpdated == Jass.TRICKS_PER_TURN)
                turnPoints1 += Jass.MATCH_ADDITIONAL_POINTS;
	        } 
	        else {
	            turnTricks1 = turnTricks(pkScore, TeamId.TEAM_1);
	            turnPoints1 = turnPoints(pkScore, TeamId.TEAM_1);
	            gamePoints1 = gamePoints(pkScore, TeamId.TEAM_1);
	            turnTricks2 = tricksUpdated;
	            turnPoints2 = scoreUpdated;
	            gamePoints2 = gamePoints(pkScore, TeamId.TEAM_2);
	            if(tricksUpdated == Jass.TRICKS_PER_TURN)
	                turnPoints2 += Jass.MATCH_ADDITIONAL_POINTS;
	        }
        return pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2);
    }
    
    
    /**
     * Retourne les scores empaquetés donnés mis à jour pour le tour prochain
     * @param pkScore(long)
     *          le score empaqueté
     * @return(long) le score empaqueté mis à jour
     */
    public static long nextTurn(long pkScore) {
        assert(isValid(pkScore));
        return pack(INITIAL_NB_TRICKS, INITIAL_TURN_POINTS, totalPoints(pkScore, TeamId.TEAM_1), INITIAL_NB_TRICKS, INITIAL_TURN_POINTS, totalPoints(pkScore, TeamId.TEAM_2));
    }
    
    /**
     * Représentation textuelle des scores
     * @param pkScore(long)
     *          score empaquetée
     * @return(String) (plis, points partie courante, gamePoints, totalPoints)
     */
    public static String toString(long pkScore) {
    	return String.format("(%d, %3d, %4d, %4d) (%d, %3d, %4d, %4d)" , turnTricks(pkScore, TeamId.TEAM_1), turnPoints(pkScore, TeamId.TEAM_1), gamePoints(pkScore, TeamId.TEAM_1),
    			totalPoints(pkScore, TeamId.TEAM_1), 
    			turnTricks(pkScore, TeamId.TEAM_2), turnPoints(pkScore, TeamId.TEAM_2), gamePoints(pkScore, TeamId.TEAM_2), totalPoints(pkScore, TeamId.TEAM_2));
    }
}