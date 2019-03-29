package ch.epfl.javass.jass;
import ch.epfl.javass.bits.Bits32;

/**
 * Contient des méthodes statiques permettant de manipuler des cartes
 * d'un jeu de Jass empaquetées dans un entier de type int
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */

public final class PackedCard {
    
    /*
     * Nombres de bits utilisés
     */
    private static final int CARD_BITS = 6;
    private static final int CARD_COLOR_BITS = 2;
    private static final int CARD_RANK_BITS = 4;
    private static final int BIT_START = 0;
    
    /*
     * constantes liées aux valeurs
     */
    private static final int RANK_MAX_VALUE = 8;
    private static final int INVALID_POINTS = -1;
    
    /**
     * représente PAS une carte empaquetée valide
     */
    public static final int INVALID = 0b111111;
    
    /**
     * Constructeur privé pour empêcher la céation d'instances de la classe
     */
    private PackedCard() {}
    
    
    
    /**
     * Check si une carte est valide ou pas (si les bits pour représenter le rang correspondent à un rang
     * et si les bits inutilisés sont tous des 0)
     * 
     * @param pkCard (int) 
     *          carte sous forme int (séquence de 32 bits)
     * @return (boolean) la valeur de la validité d'une carte
     */
    public static boolean isValid(int pkCard) {
        if(Bits32.extract(pkCard, BIT_START, CARD_RANK_BITS) >= 0 && Bits32.extract(pkCard, BIT_START, CARD_RANK_BITS) <= RANK_MAX_VALUE &&
           Bits32.extract(pkCard, CARD_BITS, Integer.SIZE-CARD_BITS) == 0) {
           return true; 
        }
        return false;
    }
    
    /**
     * Réunit une couleur et un rang en une carte et retourne la séquence de bits qui caractérise la carte
     * 
     * @param c (Card.Color)
     *          La couleur de la carte
     * @param r (Card.Rank)
     *          Le rang de la carte
     * @return (int) la représentation de la carte sous forme empaquetée
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), CARD_RANK_BITS, c.ordinal(), CARD_COLOR_BITS);
    }
    
    /**
     * Retourne la couleur d'une carte basée sur la forme int de la carte
     * @param pkCard (int)
     *          Carte sous forme int (séquence de 32 bits)
     * @return (Card.Color) la couleur de la carte,
     *                      null si appartient à aucun cas
     */
    public static Card.Color color(int pkCard){
        assert isValid(pkCard);
        return Card.Color.ALL.get(Bits32.extract(pkCard, CARD_RANK_BITS, CARD_COLOR_BITS));
        
    }
    
    /**
     * Retourne le rang de la carte donnée sous forme int
     * 
     * @param pkCard (int)
     *          Une carte sous forme int (séquence de 32 bits)
     * @return (Card.Rank) le rang de la carte
     *                     null, si appartient à aucun cas
     */
    public static Card.Rank rank(int pkCard){
        assert isValid(pkCard);
        return Card.Rank.ALL.get(Bits32.extract(pkCard, BIT_START, CARD_RANK_BITS));
    }
    
    /**
     * Retourne vrai ssi la première carte donnée est supérieure à la seconde, sachant que l'atout est trump
     * ATTENTION: cette méthode retourne faux si les deux cartes ne sont pas comparables
     * @param trump (Card.Color)
     *          l'atout du jeu
     * @param pkCardL (int)
     *          carte sous forme de int (empaquetée), la première carte
     * @param pkCardR (int)
     *          carte sous forme de int (empaquetée), la seconde carte
     * @return (boolean) vrai si pkCardL est supérieure à pkCardR, faux sinon
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        assert isValid(pkCardL);
        assert isValid(pkCardR);
        if(color(pkCardL).equals(trump) && !color(pkCardR).equals(trump)) {
            return true;
        }
        if(color(pkCardL).equals(trump) && color(pkCardR).equals(trump) && rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal()) {
            return true;
        }
        if(!color(pkCardL).equals(trump) && !color(pkCardR).equals(trump) &&  color(pkCardL).equals(color(pkCardR)) && rank(pkCardL).ordinal() > rank(pkCardR).ordinal()) {
            return true;
        }
        return false;
    }
    
    /**
     * Retourne la valeur de la carte empaquetée donnée en argument, sachant que l'atout est trump
     * @param trump (Card.Color)
     *          l'atout du jeu
     * @param pkCard (int)
     *          carte sous forme de int (empaquetée)
     * @return (int) la valeur de la carte, INVALID si correspond à aucun cas
     */
    public static int points(Card.Color trump, int pkCard) {
        assert isValid(pkCard);
        switch(rank(pkCard)) {
            case SIX: return 0;
            case SEVEN: return 0;
            case EIGHT: return 0;
            case TEN: return 10;
            case QUEEN: return 3;
            case KING: return 4;
            case ACE: return 11;
            default: break;
        }
        if(!color(pkCard).equals(trump)) {
            switch(rank(pkCard)) {
                case NINE: return 0;
                case JACK: return 2;
                default: break;
            }
        }
        else {
            switch(rank(pkCard)) {
                case NINE: return 14;
                case JACK: return 20;
                default: break;
            }
        }
        return INVALID_POINTS;
       
    }
    
    /**
     * Retourne une représentation de la carte empaquetée donnée sous forme
     * de chaîne de caractères composée du symbole de la couleur et du nom abrégé du rang
     * @param pkCard (int)
     *          carte sous forme de int (empaquetée)
     * @return (String) symbole de la couleur+ nom abrégé du rang
     */
    public static String toString(int pkCard) {
        assert isValid(pkCard);
        return color(pkCard).toString() + rank(pkCard).toString();
    }
}