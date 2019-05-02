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
 * trump, qui contient l'atout courant, trick, qui contient le pli courant,
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

    public ReadOnlyObjectProperty<Color> trumpProperty() {
        return trump;
    }

    public void setTrump(Color trump) {
        this.trump.set(trump);
    }

    public ObservableMap<PlayerId, Card> trickProperty() {
        return FXCollections.unmodifiableObservableMap(
                (ObservableMap<PlayerId, Card>) trick.get());
    }

    public void setTrick(Trick newTrick) { 
        for(int i = 0; i < PlayerId.COUNT; ++i ) {
            trick.get().put(PlayerId.ALL.get(i), null);
        }
        if(newTrick.isEmpty()) {
            winningPlayer.set(null);
        }else {
            winningPlayer.set(newTrick.winningPlayer());
            for(int i = 0; i < newTrick.size(); i++) {
                trick.get().replace(newTrick.player(i), newTrick.card(i));
            }
        }
    }

    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }
}
