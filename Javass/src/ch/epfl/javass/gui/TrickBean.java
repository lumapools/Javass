package ch.epfl.javass.gui;

import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * un bean JavaFX contenant le pli courant et doté de trois propriétés :
 * 
 * trump, qui contient l'atout courant; trick, qui contient le pli courant;
 * winningPlayer, qui contient le joueur menant le pli courant
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class TrickBean {
    private ObjectProperty<Color> trump = new SimpleObjectProperty<>();
    private ObjectProperty<Map<PlayerId, Card>> trick = new SimpleObjectProperty<>(
            FXCollections.observableHashMap());
    private ObjectProperty<PlayerId> winningPlayer = new SimpleObjectProperty<>();

    /**
     * Permet d'obtenir la propriété trump contenant une valeur de type Color
     * 
     * @return (ReadOnlyObjectProperty(Color)) la propriété en question
     */
    public ReadOnlyObjectProperty<Color> trumpProperty() {
        return trump;
    }

    /**
     * Permet de modifier la propriété trump contenant une valeur de type Color
     * 
     * @param trump
     *            (Color) l'atout
     */
    public void setTrump(Color trump) {
        this.trump.set(trump);
    }

    /**
     * Permet d'obtenir la propriété trick contenant le pli courant exposer
     * comme une table associative associant à chaque joueur la carte qu'il a
     * joué dans le pli courant, qui est nulle si le joueur en question n'a pas
     * encore joué
     * 
     * @return (ObservableMap(PlayerId, Card)) la propriété en question
     */
    public ObservableMap<PlayerId, Card> trickProperty() {
        return FXCollections.unmodifiableObservableMap(
                (ObservableMap<PlayerId, Card>) trick.get());
    }

    /**
     * Permet de modifier la propriété trick en prenant un argument de type
     * Trick et se charge de modifier en fonction de la table associative
     * contenant le pli Cette méthode se charge également de changer celle de la
     * propriété winningPlayer, qui est une propriété en lecture seule pour
     * laquelle aucune méthode de modification (setter) n'existe.
     * 
     * @param newTrick
     *            (Trick) nouveau pli
     */
    public void setTrick(Trick newTrick) {
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            trick.get().put(PlayerId.ALL.get(i), null);
        }
        if (newTrick.isEmpty()) {
            winningPlayer.set(null);
        } else {
            winningPlayer.set(newTrick.winningPlayer());
            for (int i = 0; i < newTrick.size(); i++) {
                trick.get().replace(newTrick.player(i), newTrick.card(i));
            }
        }
    }

    /**
     * Permet d'obtenir la propriété winningPlayer contenant une valeur de type
     * PlayerId
     * 
     * @return (ReadOnlyObjectProperty(PlayerId)) la propriété en question
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }
}
