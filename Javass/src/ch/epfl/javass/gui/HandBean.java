package ch.epfl.javass.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * un bean JavaFX doté de deux propriétés :
 * 
 * hand, qui contient la main du joueur; playableCards, qui contient le
 * sous-ensemble de la main du joueur qui est actuellement jouable, et qui est
 * vide si ce n'est pas au joueur en question de jouer
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class HandBean {
    private ObjectProperty<List<Card>> hand = new SimpleObjectProperty<>(
            FXCollections.observableArrayList(
                    Arrays.asList(new Card[Jass.HAND_SIZE])));
    private ObjectProperty<Set<Card>> playableCards = new SimpleObjectProperty<>(
            FXCollections.observableSet());

    /**
     * Permet d'obtenir la propriété hand (List)
     * 
     * @return (ObservableList(Card)) la propriété en question
     */
    public ObservableList<Card> handProperty() {
        return FXCollections
                .unmodifiableObservableList((ObservableList<Card>) hand.get());
    }

    /**
     * Permet de modifier la propriété hand en prenant en argument un ensmble de
     * cartes de type CardSet. Lorsque setHand est appelée avec un ensemble
     * contenant 9 cartes, la totalité de la main est redéfinie, alors que si
     * elle est appelée avec un ensemble plus petit, les cartes qui ne font pas
     * partie de l'ensemble donné sont mises à null.
     * 
     * @param newHand
     *            (CardSet) l'ensemble donné
     */
    public void setHand(CardSet newHand) {
        if (newHand.size() == Jass.HAND_SIZE) {
            for (int i = 0; i < Jass.HAND_SIZE; i++) {
                hand.get().set(i, newHand.get(i));
            }
        } else {
            for (Card c : hand.get()) {
                if (c != null && !newHand.contains(c)) {
                    hand.get().set(hand.get().indexOf(c), null);
                }
            }
        }
    }

    /**
     * Permet d'obtenir la propriété playableCards (Set)
     * 
     * @return (ObservableSet(Card)) la propriété en question
     */
    public ObservableSet<Card> playableCardsProperty() {
        return FXCollections.unmodifiableObservableSet(
                (ObservableSet<Card>) playableCards.get());
    }

    /**
     * Permet de modifier la propriété playableCards en prenant en argument un
     * ensemble de cartes de type CardSet
     * 
     * @param newPlayableCards
     *            (CardSet) nouvelles cartes jouables
     */
    public void setPlayableCards(CardSet newPlayableCards) {
        playableCards.get().clear();
        for (int i = 0; i < newPlayableCards.size(); i++) {
            playableCards.get().add(newPlayableCards.get(i));
        }
    }
}
