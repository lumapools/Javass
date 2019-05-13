package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Possède des méthodes permettant de manipuler des plis empaquetés dans des
 * valeurs de type int
 * 
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

    /**
     * Représente un pli empaqueté invalide
     */
    public static final int INVALID = ~0;

    private PackedTrick() {
    }

    /**
     * Check si un pli est valide ou pas
     * 
     * @param pkTrick
     *            (int) le pli empaqueté dans une séquence de 32 bits
     * @return (boolean) vrai si le pli est valide et faux sinon
     */
    public static boolean isValid(int pkTrick) {
        int card1 = Bits32.extract(pkTrick, BIT_START_C1, NB_BITS_FOR_CARD);
        int card2 = Bits32.extract(pkTrick, BIT_START_C2, NB_BITS_FOR_CARD);
        int card3 = Bits32.extract(pkTrick, BIT_START_C3, NB_BITS_FOR_CARD);
        int card4 = Bits32.extract(pkTrick, BIT_START_C4, NB_BITS_FOR_CARD);

        if (Bits32.extract(pkTrick, BIT_START_INDEX,
                NB_BITS_FOR_INDEX) > MAX_INDEX_TRICK) {
            return false;
        }
        return (PackedCard.isValid(card1) && PackedCard.isValid(card2)
                && PackedCard.isValid(card3) && PackedCard.isValid(card4)
                || (PackedCard.isValid(card1) && PackedCard.isValid(card2)
                        && PackedCard.isValid(card3)
                        && card4 == PackedCard.INVALID)
                || (PackedCard.isValid(card1) && PackedCard.isValid(card2)
                        && card3 == PackedCard.INVALID
                        && card4 == PackedCard.INVALID)
                || (PackedCard.isValid(card1) && card2 == PackedCard.INVALID
                        && card3 == PackedCard.INVALID
                        && card4 == PackedCard.INVALID)
                || (card1 == PackedCard.INVALID && card2 == PackedCard.INVALID
                        && card3 == PackedCard.INVALID
                        && card4 == PackedCard.INVALID));
    }

    /**
     * Retourne un pli vide qui contient juste le premier joueur voulu et la
     * couleur d'atout voulue
     * 
     * @param trump
     *            (Card.Color) la couleur d'atout voulu
     * @param firstPlayer
     *            (PlayerId) le premier joueur voulu
     * @return (int) le premier pli du jeu qui ne contient aucune carte, juste
     *         le joueur qui commence le pli et l'atout
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return (Bits32.pack(firstPlayer.ordinal(), NB_BITS_FOR_PLAYER,
                trump.ordinal(), NB_BITS_FOR_TRUMP) << BIT_START_PLAYER)
                | Bits32.mask(0, NB_BITS_FOR_CARDS);
    }

    /**
     * Retourne un pli auquel on a ajouté un index, car on passe d'un pli au
     * prochain. Le joueur gagnant va devenir le premier joueur et l'atout reste
     * inchangé. Si le pli est le dernier du tour, retourne un pli invalide
     * 
     * @param pkTrick
     *            (int) le pli empaqueté qui est le pli précédent celui qu'on
     *            veut retourner
     * @return (int) le prochain pli empaqueté sans les cartes (ou un pli
     *         invalide si le pli courant est le dernier)
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);
        int nextIndex = index(pkTrick) + 1;
        int currentTrump = trump(pkTrick).ordinal();
        int nextPlayer = winningPlayer(pkTrick).ordinal();
        if (isLast(pkTrick)) {
            return PackedTrick.INVALID;
        }
        return Bits32.pack(nextIndex, NB_BITS_FOR_INDEX, nextPlayer,
                NB_BITS_FOR_PLAYER, currentTrump,
                NB_BITS_FOR_TRUMP) << NB_BITS_FOR_CARDS
                | Bits32.mask(0, NB_BITS_FOR_CARDS);
    }

    /**
     * Retourne vrai ssi le pli est le dernier du tour
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @return (boolean) vrai ssi c'est le dernier tour, sinon faux
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return index(pkTrick) == MAX_INDEX_TRICK;
    }

    /**
     * Retourne vrai ssi le pli est vide, c-à-d s'il ne contient aucune carte
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @return (boolean) vrai ssi c'est vide, sinon faux
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);
        return !PackedCard.isValid(card(pkTrick, 0));
    }

    /**
     * Retourne vrai ssi le pli est plein, c-à-d s'il contient 4 cartes
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @return (boolean) vrai si c'est plein, sinon faux
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        return size(pkTrick) == CARDS_PER_TRICK;
    }

    /**
     * Retourne la taille du pli, c-à-d le nombre de cartes qu'il contient
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @return (int) le nombre de cartes/taille du pli
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);
        for (int i = 0; i < CARDS_PER_TRICK; i++) {
            if (Bits32.extract(pkTrick, NB_BITS_FOR_CARD * i,
                    NB_BITS_FOR_CARD) == PackedCard.INVALID) {
                return i;
            }
        }
        return CARDS_PER_TRICK;
    }

    /**
     * Retourne l'atout du pli
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @return (Color) l'atout
     */
    public static Color trump(int pkTrick) {
        assert isValid(pkTrick);
        return Card.Color.ALL.get(
                Bits32.extract(pkTrick, BIT_START_TRUMP, NB_BITS_FOR_TRUMP));
    }

    /**
     * Retourne le joueur d'index donné dans le pli, le joueur d'index 0 étant
     * le premier du pli
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @param index
     *            (int) l'index
     * @return (PlayerId) le joueur d'index donné
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick);
        assert (index >= 0 && index < NB_PLAYER);
        return PlayerId.ALL.get(
                (Bits32.extract(pkTrick, BIT_START_PLAYER, NB_BITS_FOR_PLAYER)
                        + index) % NB_PLAYER);
    }

    /**
     * Retourne l'index du pli
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @return (int) l'index
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);
        return Bits32.extract(pkTrick, BIT_START_INDEX, NB_BITS_FOR_INDEX);
    }

    /**
     * Retourne la version empaquetée de la carte du pli à l'index donné
     * (supposée avoir été posée)
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @param index
     *            (int) l'index de la carte
     * @return (int) carte empaquetée
     */
    public static int card(int pkTrick, int index) {
        assert isValid(pkTrick);
        assert (index >= 0 && index < CARDS_PER_TRICK);
        return Bits32.extract(pkTrick, index * NB_BITS_FOR_CARD,
                NB_BITS_FOR_CARD);
    }

    /**
     * Retourne un pli identique à celui donné (supposé non plein), mais à
     * laquelle la carte donnée a été ajoutée
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @param pkCard
     *            (int) carte empaquetée
     * @return (int) pli empaqueté ayant la carte
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick);
        assert !isFull(pkTrick);
        assert PackedCard.isValid(pkCard);
        int shiftBeforeCard = NB_BITS_FOR_CARD * size(pkTrick);
        int shiftAfterCard = NB_BITS_FOR_CARD * (size(pkTrick) + 1);
        return (pkCard << shiftBeforeCard | Bits32.mask(0, shiftBeforeCard)
                | Bits32.mask(shiftAfterCard, Integer.SIZE - shiftAfterCard))
                & pkTrick;
    }

    /**
     * Retourne la couleur de base du pli, c-à-d la couleur de sa première carte
     * (supposée avoir été jouée)
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @return (Color) la couleur de base
     */
    public static Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        return PackedCard
                .color(Bits32.extract(pkTrick, BIT_START_C1, NB_BITS_FOR_CARD));
    }

    /**
     * Retourne le sous-ensemble (empaqueté) des cartes de la main pkHand qui
     * peuvent être jouées comme prochaine carte du pli pkTrick (supposé non
     * plein)
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @param pkHand
     *            (long) les cartes dans les mains
     * @return (long) le sous-ensemble empaqueté des cartes qui peuvent être
     *         jouées
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert isValid(pkTrick);
        assert PackedCardSet.isValid(pkHand);

        // Si personne n'a encore joué
        if (isEmpty(pkTrick)) {
            return pkHand;
        }

        // atout demandé
        long trump_in_hands = PackedCardSet.subsetOfColor(pkHand,
                trump(pkTrick));
        if (baseColor(pkTrick) == trump(pkTrick)) {
            if (PackedCardSet.isEmpty(trump_in_hands)
                    || trump_in_hands == PackedCardSet.trumpAbove(
                            PackedCard.pack(trump(pkTrick), Rank.NINE))) {
                return pkHand;
            }
            return trump_in_hands;
        }

        // atout pas demandé
        int s = size(pkTrick);
        int nb = 0;
        long betterTrump = trump_in_hands;
        for (int i = 1; i < s; ++i) {
            if (PackedCard.color(card(pkTrick, i)).equals(trump(pkTrick))) {
                betterTrump &= PackedCardSet.trumpAbove(card(pkTrick, i));
                nb = i;
            }
        }
        // les atouts qui sont plus forts que celui qui a coupé, et qui sont
        // dans mes mains
        long baseColor_in_hands = PackedCardSet.subsetOfColor(pkHand,
                baseColor(pkTrick));
        // si au moins une personne a coupé
        if (nb != 0) {
            // n'a pas de meilleur atout
            if (PackedCardSet.isEmpty(betterTrump)) {
                if (PackedCardSet.isEmpty(baseColor_in_hands)) {
                    if (pkHand == trump_in_hands) {
                        return trump_in_hands;
                    }
                    return PackedCardSet.difference(pkHand, trump_in_hands);
                }
                return baseColor_in_hands;
            }
            // a de meilleur atout
            if (PackedCardSet.isEmpty(baseColor_in_hands)) {
                return betterTrump
                        | PackedCardSet.difference(pkHand, trump_in_hands);
            }
            return betterTrump | baseColor_in_hands;
        }
        // personne n'a coupé, peut pas suivre
        if (PackedCardSet.isEmpty(baseColor_in_hands)) {
            return pkHand;
        }
        // personne n'a coupé, mais peut suivre
        return baseColor_in_hands | trump_in_hands;
        
    }

    /**
     * Retourne la valeur du pli, en tenant compte des « 5 de der »
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @return (int) la valeur du pli
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);
        int points = 0;
        for (int i = 0; i < size(pkTrick); ++i) {
            points += PackedCard.points(trump(pkTrick), card(pkTrick, i));
        }
        if (isLast(pkTrick)) {
            points += Jass.LAST_TRICK_ADDITIONAL_POINTS;
        }
        return points;
    }

    /**
     * Retourne l'identité du joueur menant le pli (supposé non vide)
     * 
     * @param pkTrick
     *            (int) le pli courant
     * @return (PlayerId) Le joueur ayant gagné ce pli
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);
        assert !isEmpty(pkTrick);
        int wP = 0;
        for (int i = 0; i < size(pkTrick); i++) {
            if (PackedCard.isBetter(trump(pkTrick), card(pkTrick, i),
                    card(pkTrick, wP))
                    && PackedCard.isValid(card(pkTrick, i))) {
                wP = i;
            }
        }
        return player(pkTrick, wP);
    }

    /**
     * Retourne une représentation textuelle du pli
     * 
     * @param pkTrick
     *            (int) pli empaqueté
     * @return (String) (Tour : x, les cartes jouées, nombre de points)
     */
    public static String toString(int pkTrick) {
        assert isValid(pkTrick);
        StringBuilder s = new StringBuilder("( Pli : ");
        s.append(index(pkTrick)).append(", ");
        for (int i = 0; i < size(pkTrick); ++i) {
            s.append(PackedCard.toString(card(pkTrick, i))).append(", ");
        }
        s.append("nombre de points: ").append(points(pkTrick)).append(")");
        return s.toString();
    }
}