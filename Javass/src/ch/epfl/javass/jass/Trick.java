package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * Représente un pli
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class Trick {
   
    // nombre de joueurs
    private static final int NB_PLAYER = 4;
    //minimum index
    private static final int MIN_INDEX = 0;
    
    /**
     * Représentation empaquetée du pli
     */
    private final int packedTrick;
    
    
    /**
     * Représente un pli empaqueté invalide
     */
    public static final Trick INVALID = new Trick(PackedTrick.INVALID);
    
    /**
     * Constructeur privé
     * @param packed (int)
     *          version empaquetée d'un pli
     */
    private Trick(int packed) {
        packedTrick = packed;
        
    }
    
    
   /**
    * Retourne un pli vide qui contient seulement l'atout et le premier joueur donné
    *  
    * @param trump (Color)
    *           la couleur d'atout
    * @param firstPlayer (PlayerId)
    *           le premier joueur du pli
    * @return (Trick) un pli vide qui contient seulement l'atout et le premier joueur
    */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }
    
    /**
     * Crée un pli à partir de la version empaquetée voulue du pli. Si le pli empaqueté n'est 
     * pas valide, il lance une IllegalArgumentException
     * @param packed (int)
     *          la version empaquetée sous forme de 32 bits du pli
     * @return (Trick) un pli dont la forme empaquetée est packed
     */
    public static Trick ofPacked(int packed) {
    	System.out.println(PackedTrick.toBinaryString(packed));
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }
    
    /**
     * Retourne la version empaquetée du pli qui représente ce pli
     * @return (int) la version empaquetée de 32 bits de ce pli
     */
    public int packed() {
        return packedTrick;
    }
    
    /**
     * Retourne le pli prochain sans les cartes. Le premier joueur et le numéro 
     * du pli sont mis à jour
     * @return (Trick) le prochain pli vide.
     * @throws IllegalArgumentException si le pli n'est pas plein
     */
    public Trick nextEmpty() throws IllegalStateException {
        if(!PackedTrick.isFull(packedTrick)) {
            throw new IllegalStateException();
        }
        else {
            return new Trick(PackedTrick.nextEmpty(packedTrick));
        }
    }
    
    /**
     * Check si le pli est vide ou pas
     * @return (boolean) true si le pli est vide, false sinon
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(packedTrick);
    }
    
    /**
     * Check si le pli est plein ou pas
     * @return (boolean) true si le pli est plein, false sinon
     */
    public boolean isFull() {
        return PackedTrick.isFull(packedTrick);
    }
    
    /**
     * Check si ce pli est le dernier pli du jeu
     * @return (boolean) vrai si c'est le dernier pli du jeu, false sinon
     */
    public boolean isLast() {
        return PackedTrick.isLast(packedTrick);
    }
    
    /**
     * Retourne le nombre de cartes jouées dans le pli
     * @return (int) le nombre de cartes jouées dans le pli
     */
    public int size() {
        return PackedTrick.size(packedTrick);
    }
    
    /**
     * Retourne la couleur d'atout du pli
     * @return (Color) la couleur d'atout du pli
     */
    public Color trump() {
        return PackedTrick.trump(packedTrick);
    }
    
    /**
     * Retourne l'index du pli
     * @return (int) l'index du pli
     */
    public int index() {
        return PackedTrick.index(packedTrick);
    }
    
    /**
     * Retourne le joueur d'index donné dans le pli, le joueur d'index 0 étant le premier du pli
     * @param index (int)
     *          l'index qu'on aimerait regarder
     * @return (PlayerId) le joueur à la position index
     * @throws IndexOutOfBoundsException si l'index n'est pas compris entre 0 (inclus) et 4 (exclus)
     */
    public PlayerId player(int index) throws IndexOutOfBoundsException {
        if(index < MIN_INDEX || index >= NB_PLAYER) {
            throw new IndexOutOfBoundsException();
        }
        else {
            return PackedTrick.player(packedTrick, index);
        }
    }
    
    /**
     * Retourne la carte du pli à l'index voulu
     * @param index (int) l'index auquel de la carte voulue
     * @return (Card) la carte qui se trouve à l'index voulu
     * @throws IndexOutOfBoundsException si l'index est soit plus petit que 0, soit si l'index est plus grand que la taille du pli
     */
    public Card card(int index) throws IndexOutOfBoundsException {
        if(index < MIN_INDEX || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }
        else {
            return Card.ofPacked(PackedTrick.card(packedTrick, index));
        }
    }
    
    /**
     * Returne un pli auquel la carte voulue a été ajoutée
     * @param c (Card) la carte à ajouter au pli
     * @return (Trick) le pli avec la carte ajoutée
     * @throws IllegalStateException si le pli est déjà plein
     */
    public Trick withAddedCard(Card c) throws IllegalStateException {
        if(isFull()) {
            throw new IllegalStateException();
        }
        return new Trick(PackedTrick.withAddedCard(packedTrick, c.packed()));
    }
    

    /**
     * Retourne la couleur de base du pli
     * @return (Color) la couleur de base
     * @throws IllegalStateException si la pli est vide
     */
    public Color baseColor() throws IllegalStateException{
        if(isEmpty()) {
            throw new IllegalStateException();
        }
        return PackedTrick.baseColor(packedTrick);
    }
    
    /**
     * Retourne les cartes jouables
     * @param hand (CardSet)
     * @return (CardSet) retourne les cartes jouables en format empqueté
     * @throws IllegalStateException si le pli est plein
     */
    public CardSet playableCards(CardSet hand) throws IllegalStateException{
        if(isFull()) {
            throw new IllegalStateException();
        }
        return CardSet.ofPacked((PackedTrick.playableCards(packedTrick, hand.packed())));
    }
    
    /**
     * Retourne la valeur du pli, en tenant compte des « 5 de der »
     * @return (int) la valeur du pli
     */
    public int points() {
        return PackedTrick.points(packedTrick);
    }
    
    /**
     * Retourne l'identité du joueur menant le pli ou lève IllegalStateException si le pli est vide
     * @return (PlayerId) le joueur ayant gagné le pli
     * @throws IllegalStateException si le pli est vide
     */
    public PlayerId winningPlayer() throws IllegalStateException{
        if(isEmpty()) {
            throw new IllegalStateException();
        }
        return PackedTrick.winningPlayer(packedTrick);
    }
    
    /**
     * Redéfinition de la méthode equals de Object
     * @return (boolean) vrai si ça représente le même ensemble, faux sinon
     */
    @Override
    public boolean equals(Object that0) {
        if(that0 == null) {
            return false;
        }
        else {
            if(that0.getClass() != getClass()) {
                return false;
            }
            else {
                Trick pli = (Trick)that0;
                return (this.packed() == pli.packed());
            }
        }
    }
    
    /**
     * Redéfiniton de la méthode hashCode de Object
     * @return (int) la version empaqueté du pli
     */
    @Override
    public int hashCode() {
        return packedTrick;
    }
    
    /**
     * Redéfinition de la méthode toString de Object
     * @return (String) (Tour : x, les cartes jouées, nombre de points)
     */
    @Override
    public String toString() {
        return PackedTrick.toString(packedTrick);
    }
}