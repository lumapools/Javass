package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Représentation d'une carte d'un jeu de 36 cartes
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */

//Une classe immuable
public final class Card {
    /**
     * Représentation de la version empaquetée de la carte
     */
    private final int reprVerPacked;
    
   /**
    * Constructeur privé
    * @param packed (int)
    *           carte empaquetée
    */
    private Card(int packed){
        reprVerPacked = packed;
    }
    
    /**
     * Appelle le constructeur et crée une carte
     * 
     * @param c (Color)
     *          la couleur de la carte 
     * @param r (Rank)
     *          le rang de la carte
     * @return (Card) une nouvelle carte qui a la couleur et le rang choisis
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }
    
    /**
     * Crée une carte à partir de sa forme empaquetée en 32 bits
     * 
     * @param packed (int)
     *          carte sous forme int (séquence de 32 bits)
     * 
     * @throws IllegalArgumentException si
     *          la carte n'est pas valide (c'est la méthode isValid  check qu'il s'agit bien d'une carte valide)
     * @return (Card) la carte crée à partir de sa forme int
     */
    public static Card ofPacked(int packed) {
        Preconditions.checkArgument(PackedCard.isValid(packed));
        return new Card(packed);
    }
    
    /**
     * Retourne la version empaquetée de la carte
     * @return (int) la version empaquetée de la carte
     */
    public int packed() {
        return reprVerPacked;
    }
    
    /**
     * Retourne la couleur de la carte
     * @return (Color) la couleur de la carte
     */
    public Color color() {
        return PackedCard.color(reprVerPacked);
    }
    
    /**
     * Retourne le rang de la carte
     * @return (Rang) le rang de la carte
     */
    public Rank rank() {
        return PackedCard.rank(reprVerPacked);
    }
    
    /**
     * Retourne vraie ssi le récepteur (c-à-d la carte à laquelle on applique la méthode)
     * est supérieur à la carte passée en argument, sachant que l'atout est trump
     * @param trump (Color)
     *          l'atout du jeu
     * @param that (Card)
     *          carte précédente
     * @return (boolean) vrai si la carte est supérieure, faux sinon (pas comparable y compris)
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.reprVerPacked, that.reprVerPacked);
    }
    
    /**
     * Retourne la valeur de la carte, sachant que l'atout est trump
     * @param trump (Color)
     *          l'atout du jeu
     * @return (int) la valeur de la carte
     */
    public int points(Color trump) {
        return PackedCard.points(trump, reprVerPacked);
    }
    
    /**
     * Redéfinition de la méthode equals de Object
     * Retourne vrai ssi le récepteur est égal à l'objet passé en argument,
     * c-à-d s'il représente la même carte
     * @return (boolean) vrai si ça représente la même carte, faux sinon
     */
    @Override
    public boolean equals(Object that0) {
        if(that0 == null) {
            return false;
        }else {
            if(that0.getClass() != getClass()) {
                return false;
            }else {
                Card card = (Card)that0;
                return (this.packed() == card.packed());
            }
        }
    }
    
    /**
     * Redéfiniton de la méthode hashCode de Object
     * Retourne la même valeur que la méthode packed
     * @return (int) la version empaquetée de la carte
     */
    @Override
    public int hashCode() {
        return packed();
    }
    
    /**
     * Redéfinition de la méthode toString de Object
     * @return (String) symbole de la couleur et le nom abrégé du rang
     */
    @Override
    public String toString() {
        return PackedCard.toString(reprVerPacked);
    }
    
    /**
     * 
     * représente la couleur d'une carte
     * type énuméré, publique et imbriqué dans la classe Card
     *
     */
    public enum Color{
        SPADE("\u2660"),
        HEART("\u2665"),
        DIAMOND("\u2666"),
        CLUB("\u2663");
        
        // symbole de la couleur
        private String image;
        
        /**
         * Constructeur d'enum Color
         * @param image (String)
         *          la notation du symbole en caractères
         */
        private Color(String image) {
            this.image = image;
        }
        
        /**
         * une liste immuable contenant toutes les valeurs du type 
         * énuméré, dans l'ordre de déclaration (couleur)
         */
        public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        
        /**
         * Le nombre de valeurs du type énuméré
         */
        public static final int COUNT = values().length;

        /**
         * Redéfinition de la méthode toString de Object
         * @return (String) le symbole correspondant à la couleur
         */
        @Override
        public String toString() {
            return image;
        }
        
    }
    
    /**
     * 
     * représente le rang d'une carte
     * type énuméré, publique et imbriqué dans la classe Card
     *
     */
    public enum Rank{
        SIX("6", 0),
        SEVEN("7", 1),
        EIGHT("8", 2),
        NINE("9", 7),
        TEN("10", 3),
        JACK("J", 8),
        QUEEN("Q", 4),
        KING("K", 5),
        ACE("A", 6);
        
        // représentation compacte du rang
        private String representation;
        // la position lorsque c'est l'atout
        private int orderTrump;
        
        /**
         * Constructeur d'enum Rank
         * @param representation (String)
         *          représentation compacte du rang
         * @param ordreTrump (int)
         *          la position lorsque c'est l'atout
         */
        private Rank(String representation, int ordreTrump) {
            this.representation = representation;
            this.orderTrump = ordreTrump;
        }
        
        /**
         * une liste immuable contenant toutes les valeurs du type 
         * énuméré, dans l'ordre de déclaration (rang)
         */
        public static final List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        
        /**
         * Le nombre de valeurs du type énuméré
         */
        public static final int COUNT = values().length;
        
        /**
         * Retourne la position lorsque c'est l'atout
         * @return(int) la position comprise entre 0 et 8, de la carte d'atout
         *           ayant ce rang, à savoir : 0 pour SIX, 1 pour SEVEN, …, 7 pour NINE et 8 pour JACK
         */
        public int trumpOrdinal() {
            return orderTrump;
        }
        
        /**
         * Redéfinition de la méthode toString de Object
         * @return (String) représentation compacte du rang
         */
        @Override
        public String toString() {
            return representation;
        }
    }
   
}


