package ch.epfl.javass.jass;

import java.util.StringJoiner;  

import ch.epfl.javass.bits.Bits64;

/**
 * Ensemble de cartes représenté sous forme de 64 bits
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class PackedCardSet {
    // nombre de zéros consécutifs
    private static final int CONSECUTIVE_ZERO_BITS = 7;
    
    /**
     * Nombre de bits pour représenter une ensemble de cartes appartenant à la même couleur (y compris les 7 bits inutilisés
     */
    private static final int COLOR_SET_SIZE = 16;
    
    /**
     * Constantes qui représentent un ensemble de cartes meilleures qu'une carte donnée D'ATOUT
     */
    private static final int BETTER_THAN_SIX = 0b111_111_110;
    private static final int BETTER_THAN_SEVEN = 0b111_111_100;
    private static final int BETTER_THAN_EIGHT = 0b111_111_000;
    private static final int BETTER_THAN_TEN = 0b111_101_000;
    private static final int BETTER_THAN_QUEEN = 0b110_101_000;
    private static final int BETTER_THAN_KING = 0b100_101_000;
    private static final int BETTER_THAN_ACE = 0b000_101_000;
    private static final int BETTER_THAN_NINE = 0b000_100_000;
    private static final int BETTER_THAN_JACK = 0b000_000_000;
    //tableau qui contient ces cartes dessus, utilisé pour la méthode TrumpAbove
    private static final int tableBetterCards[] = new int[] {BETTER_THAN_SIX, BETTER_THAN_SEVEN, BETTER_THAN_EIGHT, BETTER_THAN_TEN, BETTER_THAN_QUEEN, 
            BETTER_THAN_KING, BETTER_THAN_ACE, BETTER_THAN_NINE, BETTER_THAN_JACK};
    
    /**
     * Index de début de chaque suite de zéros
     */
    private static final int FIRST_ZEROS_START = 9;
    private static final int SECOND_ZEROS_START = 25;
    private static final int THIRD_ZEROS_START = 41;
    private static final int FOURTH_ZEROS_START = 57;
    
    /**
     * Constante qui représente un ensemble vide 
     */
    public static final long EMPTY = 0L;
    /**
     * Constante qui représente un ensemble de cartes dans lequel tout les cartes y sont
     */
    public static final long ALL_CARDS = 0b111111111_0000000111111111_0000000111111111_0000000111111111L;
    //public static final long ALL_CARDS = 0b000000001_0000000000000001_0000000000000001_0000000000000001L;

    
    /**
     * Constructeur privé, car non instantiable
     */
    private PackedCardSet() {}
    
    
    /**
     * Check si un ensemble de carte est valide
     * @param pkCardSet (long)
     *          l'ensemble de cartes
     * @return (boolean) faux si l'ensemble de cartes est invalide (c'est-à-dire contient un/des 1 à un/des endroits pas voulus)
     */
    public static boolean isValid(long pkCardSet) {
        if(Bits64.extract(pkCardSet, FIRST_ZEROS_START, CONSECUTIVE_ZERO_BITS) != 0 || Bits64.extract(pkCardSet, SECOND_ZEROS_START, CONSECUTIVE_ZERO_BITS) != 0 || 
                Bits64.extract(pkCardSet, THIRD_ZEROS_START, CONSECUTIVE_ZERO_BITS) != 0 || Bits64.extract(pkCardSet, FOURTH_ZEROS_START, CONSECUTIVE_ZERO_BITS) != 0) {
            return false;
        }
        return true;
    }
    
    /**
     * Etant donné une carte d'atout représentée sous form de 32 bits, cette méthode retourne un long qui représente un ensemble de cartes
     * qui contient toutes les cartes qui sont plus fortes que cette carte
     * 
     * @param pkCard (long)
     *          carte empaquetée représentée sous une forme de 32 bits
     * @return (long) l'ensemble de cartes (d'atout nécéssairement) qui sont plus fortes que pkCard
     */
    public static long trumpAbove(int pkCard) {
        assert(PackedCard.isValid(pkCard));
        return ((long)tableBetterCards[PackedCard.rank(pkCard).trumpOrdinal()]) << COLOR_SET_SIZE*PackedCard.color(pkCard).ordinal();
    }
    

    /**
     * Retourne l'ensemble de cartes empaqueté contenant uniquement la carte empaquetée donnée
     * @param pkCard (int)
     *          la carte empaquetée
     * @return (long) l'ensemble contenant uniquement la carte
     */
    public static long singleton(int pkCard) {
        assert(PackedCard.isValid(pkCard));
        return 1L << pkCard;
    }
    
    /**
     * Retourne vrai ssi l'ensemble de cartes empaqueté donné est vide
     * @param pkCardSet (long)
     *          ensemble de carte empaqueté
     * @return (boolean) vrai: ensemble vide
     */
    public static boolean isEmpty(long pkCardSet) {
        assert(isValid(pkCardSet));
        return pkCardSet == 0L;
    }
    
    /**
     * Retourne la taille de l'ensemble de cartes empaqueté donné, c-à-d le nombre de cartes qu'il contient
     * @param pkCardSet (long)
     *          ensemble de cartes empaqueté
     * @return (int) la taille
     */
    public static int size(long pkCardSet) {
        assert(isValid(pkCardSet));
        return Long.bitCount(pkCardSet);
    }
    
    /**
     * Retourne la version empaquetée de la carte d'index donné de l'ensemble de cartes empaqueté donné
     * @param pkCardSet (long)
     *          ensemble de cartes empaqueté
     * @param index (int)
     *          l'index dans l'ensemble
     * @return (int) la version empaquetée de la carte
     */
    public static int get(long pkCardSet, int index) {
        assert(isValid(pkCardSet));
        assert(index < size(pkCardSet));
        for(int i=0; i<index; i++) {
            pkCardSet = pkCardSet^Long.lowestOneBit(pkCardSet);
        }
        return Long.numberOfTrailingZeros(pkCardSet);
    }
    
    
    /**
     * Retourne l'ensemble de cartes empaqueté donné auquel la carte empaquetée donnée a été ajoutée
     * @param pkCardSet (long)
     *          ensemble empaqueté de départ
     * @param pkCard (int)
     *          carte empaquetée
     * @return (long) ensemble empaqueté avec la carte
     */
    public static long add(long pkCardSet, int pkCard) {
        assert(isValid(pkCardSet));
        assert(PackedCard.isValid(pkCard));
        return union(pkCardSet,singleton(pkCard));
    }
    
    /**
     * Retourne l'ensemble de cartes empaqueté donné duquel la carte empaquetée donnée a été supprimée
     * @param pkCardSet (long)
     *          ensemble empaqueté de départ
     * @param pkCard (int)
     *          carte empaquetée
     * @return (long) ensemble empaqueté sans la carte
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert(isValid(pkCardSet));
        assert(PackedCard.isValid(pkCard));
        return pkCardSet & ~singleton(pkCard);
    }
    
    /**
     * Retourne vrai ssi l'ensemble de cartes empaqueté donné contient la carte empaquetée donnée
     * @param pkCardSet (long)
     *          ensemble de cartes empaqueté
     * @param pkCard (int)
     *          carte empaquetée
     * @return (boolean) vrai si la carte est contenu, sinon faux
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert(isValid(pkCardSet));
        assert(PackedCard.isValid(pkCard));
        return pkCardSet == (pkCardSet|singleton(pkCard));
    }
    
    /**
     * Calcul le complément de l'ensemble de cartes empaqueté donné
     * @param pkCardSet (long)
     *          ensemble des cartes empaqueté
     * @return (long) le complément de l'ensemble
     */
    public static long complement(long pkCardSet) {
        assert(isValid(pkCardSet));
        return ~pkCardSet&ALL_CARDS;
    }
    
    /**
     * Calcul l'union des deux ensembles de cartes empaquetés donnés
     * @param pkCardSet1 (long)
     *          premier ensemble de cartes empaqueté
     * @param pkCardSet2 (long)
     *          deuxième ensemble de cartes empaqueté
     * @return (long) l'union des deux ensembles
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert(isValid(pkCardSet1));
        assert(isValid(pkCardSet2));
        return pkCardSet1|pkCardSet2;
    }
    
    /**
     * Calcul l'intersection des deux ensembles de cartes empaquetés donnés
     * @param pkCardSet1 (long)
     *        premier ensemble de cartes empaqueté
     * @param pkCardSet2 (long)
     *          deuxième ensemble de cartes empaqueté  
     * @return (long) l'intersection des deux ensembles
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert(isValid(pkCardSet1));
        assert(isValid(pkCardSet2));
        return pkCardSet1&pkCardSet2;
    }
    
    /**
     * Calcul la différence entre le premier ensemble de cartes empaqueté donné et le second,
     * c-à-d l'ensemble des cartes qui se trouvent dans le premier ensemble mais pas dans le second
     * @param pkCardSet1 (long)
     *          premier ensemble de cartes empaqueté
     * @param pkCardSet2 (long)
     *          deuxième ensemble de cartes empaqueté  
     * @return (long) la différence des deux ensembles
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert(isValid(pkCardSet1));
        assert(isValid(pkCardSet2));
        return pkCardSet1 & ~pkCardSet2;
    }

    /**
     * Retourne le sous-ensemble de l'ensemble de cartes empaqueté donné constitué que
     * de cartes de la couleur qu'on a choisie
     * 
     * @param pkCardSet (long)
     *          l'ensemble de cartes empaquetées dont on veut extraire une couleur spécifique
     * @param color (Card.Color)
     *          la couleur qu'on veut extraire
     * @return (long) l'ensemble empaqueté de cartes dont on a choisi la couleur
     */
    
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert(isValid(pkCardSet));
        return Bits64.extract(pkCardSet, color.ordinal()*16, 16) << color.ordinal()*16;
        
    }
    
    /**
     * Met en format string un cardSet
     * @param pkCardSet (long)
     *          la séquence de 64 bits qui représente un ensemble de cartes
     * @return (String)
     *          un string des cartes contenues dans l'ensemble (comme s'il s'agissait d'un set)
     */
    public static String toString(long pkCardSet) {
        assert isValid(pkCardSet);
        StringJoiner j = new StringJoiner(",", "{", "}");
        for(int i = 0; i < size(pkCardSet); i++) {
            j.add(PackedCard.toString(get(pkCardSet, i)));
        }
        return j.toString();
          
    }
    
    
}

