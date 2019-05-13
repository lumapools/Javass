package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Contient le programme principal permettant de jouer à une partie distante
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public class RemoteMain extends Application {
    /**
     * Appelle la méthode launch de Application
     * 
     * @param args
     *            Arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread remoteThread = new Thread(() -> {
            RemotePlayerServer pl = new RemotePlayerServer(
                    new GraphicalPlayerAdapter());
            System.out.println(
                    "La partie commencera à la connexion du client...");
            pl.run();
        });
        remoteThread.setDaemon(true);
        remoteThread.start();

    }

}
