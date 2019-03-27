package ch.epfl.javass.jass;


import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Possède des méthodes permettant de manipuler des plis empaquetés dans des valeurs de type int
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class PackedTrick {
    
    /*
     * Constantes liées aux tailles de bits
     */
    private static final int NB_BITS_FOR_CARD = 6;
    private static final int NB_BITS_FOR_INDEX = 4;
    private static final int NB_BITS_FOR_PLAYER = 2;
    private static final int NB_BITS_FOR_TRUMP = 2;
    private static final int NB_BITS_FOR_CARDS = 24;

    /*
     * Constantes liées aux index de début de chaque groupe de bits
     */
    private static final int BIT_START_C1 = 0;
    private static final int BIT_START_C2 = 6;
    private static final int BIT_START_C3 = 12;
    private static final int BIT_START_C4 = 18;
    private static final int BIT_START_INDEX = 24;
    private static final int BIT_START_PLAYER = 28;
    private static final int BIT_START_TRUMP = 30;
    
    /*
     * Constantes liées aux valeurs
     */
    private static final int MAX_INDEX_TRICK = 8;
    
    /* 
     * Constantes lié au jeu
    */
    private static final int CARDS_PER_TRICK = 4;
    private static final int NB_PLAYER = 4;
    private static final int ALL_1_CARD = PackedCard.INVALID;
    
    /**
     * Représente un pli empaqueté invalide
     */
    public static final int INVALID = ~0;
   
            
    /**
     * Constructeur privé, car non instantiable
     */
    private PackedTrick() {}
    
    /**
     * Check si un pli est valide ou pas
     * @param pkTrick (int)
     *          le pli empaqueté dans une séquence de 32 bits
     * @return (boolean) vrai si le pli est valide et faux sinon
     */
    public static boolean isValid(int pkTrick) {
        int card1 = Bits32.extract(pkTrick, BIT_START_C1, NB_BITS_FOR_CARD);
        int card2 = Bits32.extract(pkTrick, BIT_START_C2, NB_BITS_FOR_CARD);
        int card3 = Bits32.extract(pkTrick, BIT_START_C3, NB_BITS_FOR_CARD);
        int card4 = Bits32.extract(pkTrick, BIT_START_C4, NB_BITS_FOR_CARD);
        
        
        if(Bits32.extract(pkTrick, BIT_START_INDEX, NB_BITS_FOR_INDEX) <= MAX_INDEX_TRICK) {
            // toutes les cartes sont valides
            if(PackedCard.isValid(card1) && (PackedCard.isValid(card2)) && PackedCard.isValid(card3) && PackedCard.isValid(card4)) {
                return true;
            }
            else {
                    // première condition: toutes les cartes invalides
                if ((!PackedCard.isValid(card4) && !PackedCard.isValid(card3) && !PackedCard.isValid(card2) && !PackedCard.isValid(card1)
                      && card1 == ALL_1_CARD && card2 == ALL_1_CARD && card3 == ALL_1_CARD && card4 == ALL_1_CARD)
                                                                        ||
                    // 2ème, 3ème et 4ème cartes invalides
                    (!PackedCard.isValid(card4) && !PackedCard.isValid(card3) && !PackedCard.isValid(card2)  && PackedCard.isValid(card1)
                      && card2 == ALL_1_CARD && card3 == ALL_1_CARD && card4 == ALL_1_CARD)
                                                                        ||  
                    // 3ème et 4ème cartes invalides
                    (!PackedCard.isValid(card4) && !PackedCard.isValid(card3) && PackedCard.isValid(card2) && PackedCard.isValid(card1)
                      && card3 == ALL_1_CARD && card4 == ALL_1_CARD)
                                                                        ||
                    // 4ème carte invalide seulement
                    (!PackedCard.isValid(card4) && PackedCard.isValid(card3) && PackedCard.isValid(card2) && PackedCard.isValid(card1)
                      && card4 == ALL_1_CARD)) {
                    return true;
                    }
                else {
                    return false;
                }
            }
            
        }
        else {
            return false;
        }
           
    }
    
    /**
     * Retourne un pli vide qui contient juste le premier joueur voulu et la couleur d'atout voulu
     * @param trump (Card.Color)
     *          la couleur d'atout voulu
     * @param firstPlayer (PlayerId)
     *          le premier joueur voulu
     * @return (int) le premier pli du jeu qui ne contient aucune carte, juste le joueur qui commence le pli et l'atout
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return (Bits32.pack(firstPlayer.ordinal(), NB_BITS_FOR_PLAYER, trump.ordinal(), 
        		NB_BITS_FOR_TRUMP) << BIT_START_PLAYER) | Bits32.mask(0,NB_BITS_FOR_CARDS);
    }
    
    /**
     * Retourne un pli auquel on a ajouté un index, car on passe d'un pli au prochain. Le joueur gagnant va devenir le premier joueur et l'atout reste inchangé
     * @param pkTrick (int)
     *          le pli empaqueté qui est le pli précédent celui qu'on veut retourner
     * @return (int) le prochain pli empaqueté, sans les cartes
     */
    public static int nextEmpty(int pkTrick) {
        assert(isValid(pkTrick));
        //System.out.println(index(pkTrick));
        int nextIndex = index(pkTrick) + 1;
        int currentTrump = trump(pkTrick).ordinal();
        int nextPlayer = winningPlayer(pkTrick).ordinal();
        if(nextIndex ==  Jass.TRICKS_PER_TURN) {
            return PackedTrick.INVALID;
        }
        else {
        	//System.out.println(nextIndex);
            return Bits32.pack(PackedCard.INVALID, NB_BITS_FOR_CARD, 
            		PackedCard.INVALID, NB_BITS_FOR_CARD, 
            		PackedCard.INVALID, NB_BITS_FOR_CARD, 
            		PackedCard.INVALID, NB_BITS_FOR_CARD, 
            		nextIndex, NB_BITS_FOR_INDEX, nextPlayer, NB_BITS_FOR_PLAYER, 
            		  currentTrump, NB_BITS_FOR_TRUMP);
        }
    }
    
    /**
     * Retourne vrai ssi le pli est le dernier du tour
     * @param pkTrick (int)
     *          pli empaqueté
     * @return (boolean) vrai ssi c'est le dernier tour, sinon faux
     */
    public static boolean isLast(int pkTrick) {
        assert(isValid(pkTrick));
        return index(pkTrick) == MAX_INDEX_TRICK;
    }
    
    /**
     * Retourne vrai ssi le pli est vide, c-à-d s'il ne contient aucune carte
     * @param pkTrick (int)
     *          pli empaqueté
     * @return (boolean) vrai ssi c'est vide, sinon faux
     */
    public static boolean isEmpty(int pkTrick) {
        assert(isValid(pkTrick));
        return Bits32.extract(pkTrick, BIT_START_C1, NB_BITS_FOR_CARDS) == Bits32.mask(0, NB_BITS_FOR_CARDS);
    }
    
    /**
     * Retourne vrai ssi le pli est plein, c-à-d s'il contient 4 cartes
     * @param pkTrick (int)
     *          pli empaqueté
     * @return (boolean) vrai si c'est plein, sinon faux
     */
    public static boolean isFull(int pkTrick) {
        //assert(isValid(pkTrick));
        for(int i = 0; i<CARDS_PER_TRICK; i++) {
            if(Bits32.extract(pkTrick, NB_BITS_FOR_CARD*i, NB_BITS_FOR_CARD) == PackedCard.INVALID) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Retourne la taille du pli, c-à-d le nombre de cartes qu'il contient
     * @param pkTrick (int)
     *          pli empaqueté
     * @return (int) le nombre de cartes/taille du pli
     */
    public static int size(int pkTrick) {
        assert(isValid(pkTrick));
        for(int i = 0; i<CARDS_PER_TRICK; i++) {
            if(Bits32.extract(pkTrick, NB_BITS_FOR_CARD*i, NB_BITS_FOR_CARD) == PackedCard.INVALID) {
                return i;
            }
        }
        return CARDS_PER_TRICK;
    }
    
    /**
     * Retourne l'atout du pli
     * @param pkTrick (int)
     *          pli empaqueté
     * @return (Color) l'atout
     */
    public static Color trump(int pkTrick) {
        assert(isValid(pkTrick));
        return Card.Color.ALL.get(Bits32.extract(pkTrick, BIT_START_TRUMP, NB_BITS_FOR_TRUMP));
    }
    
    /**
     * Retourne le joueur d'index donné dans le pli, le joueur d'index 0 étant le premier du pli
     * @param pkTrick (int)
     *          pli empaqueté
     * @param index (int)
     *          l'index
     * @return (PlayerId) le joueur d'index donné
     */
    public static PlayerId player(int pkTrick, int index) {
        assert(isValid(pkTrick));
        assert(index >= 0 && index < NB_PLAYER);
        return PlayerId.ALL.get((Bits32.extract(pkTrick, BIT_START_PLAYER, NB_BITS_FOR_PLAYER) + index) % NB_PLAYER);
    }
    
    /**
     * Retourne l'index du pli
     * @param pkTrick (int)
     *          pli empaqueté
     * @return (int) l'index
     */
    public static int index(int pkTrick) {
        assert(isValid(pkTrick));
        return Bits32.extract(pkTrick, BIT_START_INDEX, NB_BITS_FOR_INDEX);
    }
    
    /**
     * Retourne la version empaquetée de la carte du pli à l'index donné (supposée avoir été posée)
     * @param pkTrick (int)
     *          pli empaqueté
     * @param index (int)
     *          l'index de la carte
     * @return (int) carte empaquetée
     */
    public static int card(int pkTrick, int index) {
        assert(isValid(pkTrick));
        assert(index >= 0 && index < CARDS_PER_TRICK);
        return Bits32.extract(pkTrick, index*NB_BITS_FOR_CARD, NB_BITS_FOR_CARD);
    }
    
    /**
     * Retourne un pli identique à celui donné (supposé non plein), mais à laquelle la carte donnée a été ajoutée
     * @param pkTrick (int)
     *          pli empaqueté
     * @param pkCard (int)
     *          carte empaquetée
     * @return (int) pli empaqueté ayant la carte
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert(isValid(pkTrick));
        assert(PackedCard.isValid(pkCard));
        int shiftBeforeCard = NB_BITS_FOR_CARD * size(pkTrick);
        int shiftAfterCard = NB_BITS_FOR_CARD * (size(pkTrick) + 1);
        return (pkCard<<shiftBeforeCard | Bits32.mask(0,shiftBeforeCard) | Bits32.mask(shiftAfterCard, Integer.SIZE-shiftAfterCard))&pkTrick;
    }
    
    /**
     * Retourne la couleur de base du pli, c-à-d la couleur de sa première carte (supposée avoir été jouée)
     * @param pkTrick (int)
     *          pli empaqueté
     * @return (Color) la couleur de base
     */
    public static Color baseColor(int pkTrick) {
        assert(isValid(pkTrick));
        assert(PackedCard.isValid(Bits32.extract(pkTrick, BIT_START_C1, NB_BITS_FOR_CARD)));
        return PackedCard.color(Bits32.extract(pkTrick, BIT_START_C1, NB_BITS_FOR_CARD));
    }
    
    /**
     * Retourne le sous-ensemble (empaqueté) des cartes de la main pkHand qui peuvent être jouées comme prochaine carte du pli pkTrick (supposé non plein)
     * @param pkTrick (int)
     *          pli empaqueté
     * @param pkHand (long)
     *          les cartes dans les mains
     * @return (long) le sous-ensemble empaqueté des cartes qui peuvent être jouées
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert(isValid(pkTrick));
        assert(PackedCardSet.isValid(pkHand));
        
        // Si personne n'a encore joué
        if(isEmpty(pkTrick)) {
            return pkHand;
        }
        
        // atout demandé
        if(baseColor(pkTrick) == trump(pkTrick)) {
            if(PackedCardSet.isEmpty(PackedCardSet.subsetOfColor(pkHand, trump(pkTrick))) || PackedCardSet.subsetOfColor(pkHand, trump(pkTrick)) == PackedCardSet.trumpAbove(PackedCard.pack(trump(pkTrick), Rank.NINE))) {
                return pkHand;
            }
            return PackedCardSet.subsetOfColor(pkHand, trump(pkTrick));
        }
        
        //atout pas demandé
        int s = size(pkTrick);
        int nb = 0;
        for(int i = 0; i < s; ++i) {
            if(PackedCard.color(card(pkTrick, i)) == trump(pkTrick)) {
                ++nb;
            }
        }
        
        //si au moins une personne a coupé
        long betterTrump = PackedCardSet.trumpAbove(card(pkTrick, nb)) & pkHand; //les atouts qui sont plus forts que celle qui a coupé, et qui sont dans mes mains
        if(nb != 0) {
            if(PackedCardSet.isEmpty(betterTrump)){
                if(PackedCardSet.isEmpty(PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick)))) {
                    if(pkHand == PackedCardSet.subsetOfColor(pkHand, trump(pkTrick))) {
                        return PackedCardSet.subsetOfColor(pkHand, trump(pkTrick));
                    }
                    return PackedCardSet.difference(pkHand, PackedCardSet.subsetOfColor(pkHand, trump(pkTrick)));
                }
                return PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick));
            }
            if(PackedCardSet.isEmpty(PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick)))) {
                return betterTrump | PackedCardSet.difference(pkHand, PackedCardSet.subsetOfColor(pkHand, trump(pkTrick)));
            }
            return betterTrump | PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick));
        }
        //personne n'a coupé, peut pas suivre
        if(PackedCardSet.isEmpty(PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick)))){
            return pkHand;
        }
        //personne n'a coupé, mais peut suivre
        return PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick)) | PackedCardSet.subsetOfColor(pkHand, trump(pkTrick));
    }
    
    /**
     * Retourne la valeur du pli, en tenant compte des « 5 de der »
     * @param pkTrick (int)
     *          pli empaqueté
     * @return (int) la valeur du pli
     */
    public static int points(int pkTrick) {
        assert(isValid(pkTrick));
        int points = 0;
        for(int i =0; i<size(pkTrick); ++i) {
            points += PackedCard.points(trump(pkTrick), card(pkTrick, i));
        }
        if(isLast(pkTrick)) {
            points += Jass.LAST_TRICK_ADDITIONAL_POINTS;
        }
        return points;
    }
    
    /**
     * Retourne l'identité du joueur menant le pli (supposé non vide)
     * @param pkTrick (int)
     *          le pli courant
     * @return (PlayerId) Le joueur ayant gagné ce pli
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert(isValid(pkTrick));
        int wP = 0;
        for(int i = 0; i < size(pkTrick); i++) {
            if(PackedCard.isBetter(trump(pkTrick), card(pkTrick, i), card(pkTrick,wP)) && PackedCard.isValid(card(pkTrick, i))){
                wP = i;
            }
        }
        return player(pkTrick, wP);
    }
    
    /**
     * 
     * @param pkTrick
     * @return
     */
    public static String toBinaryString(int pkTrick) {
        return String.format("%32s",Long.toBinaryString(pkTrick)).replace(" ", "0").
                replaceAll("^(..)(..)(....)(......)(......)(......)(......)","$1_$2_$3_$4_$5_$6_$7");
    }
    
    /**
     * Retourne une représentation textuelle du pli
     * @param pkTrick (int)
     *          pli empaqueté
     * @return (String) (Tour : x, les cartes jouées, nombre de points)
     */
    public static String toString(int pkTrick) {
        assert(isValid(pkTrick));
        String representation =  "Started by: " + player(pkTrick, 0) + "(" +  "Tour : " + index(pkTrick) + ", ";
        for(int i = 0; i<size(pkTrick); ++i) {
            representation += PackedCard.toString(card(pkTrick, i)) + ", ";
        }
        representation += "nombre de points: " + points(pkTrick) + ")";
        return representation;
    }
}