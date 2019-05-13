package ch.epfl.javass;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Contient le programme principal permettant de jouer une partie locale
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public class LocalMain extends Application {
    private final static int MIN_ITERATIONS = 10;
    private final static double MIN_SECOND = 2;
    private final static int EXIT_ARG = 1;
    private final static List<String> DEFAULT_NAMES = Arrays.asList("Aline",
            "Bastien", "Colette", "David");
    private final static int DEFAULT_NB_ITERATIONS = 10_000;
    //constantes des composantes pour chaque joueur(/4 premiers arguments)
    private final static int INDEX_PLAYER_SPECIFICATION = 0;
    private final static int INDEX_PLAYER_NAME = 1;
    private final static int INDEX_PLAYER_ADD_INFO = 2;
    //5ème argument (optionnel)
    private final static int INDEX_SEED = 4;
    //nombres d'arguments
    private final static int NB_ARGS_WITHOUT_SEED = 4;
    private final static int NB_ARGS_WITH_SEED = 5;
    //nombre de composants (max possible)
    private final static int NB_COMPONENTS_FOR_DISTANT_AND_SIMULATED_PLAYER = 3;
    private final static int NB_COMPONENTS_FOR_HUMAN_PLAYER = 2;
    //autre
    private final static int PAUSE_AFTER_TRICK = 1000;  

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
        List<String> msg = getParameters().getRaw();
        Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
        Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
        Long[] r = new Long[5];
        Random mainRandom = new Random();
        if (msg.size() == NB_ARGS_WITH_SEED) {
            try {
                mainRandom = new Random(Long.parseLong(msg.get(INDEX_SEED)));
            } catch (NumberFormatException e) {
                printErrorAndExit(
                        "Erreur : la graine aléatoire n'est pas un entier long valide : "
                                + msg.get(INDEX_SEED));
            }
        } else if (msg.size() != NB_ARGS_WITHOUT_SEED && msg.size() != NB_ARGS_WITH_SEED) {
            explication();
        }
        for (int i = 0; i < r.length; ++i) {
            r[i] = mainRandom.nextLong();
        }
        
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            PlayerId pId = PlayerId.ALL.get(i);
            String[] arg = StringSerializer.split(':', msg.get(i));
            int nb_simulated_player = 0;
            int length_arg = arg.length;
            switch (arg[INDEX_PLAYER_SPECIFICATION]) {
            case "h":
                if (length_arg > NB_COMPONENTS_FOR_HUMAN_PLAYER) {
                    printErrorAndExit(
                            "Erreur : spécification du joueur comportes trop de composantes : "
                                    + arg[INDEX_PLAYER_ADD_INFO]);
                }
                ps.put(pId, new GraphicalPlayerAdapter());
                break;
            case "s":
                int nbIterations = DEFAULT_NB_ITERATIONS;
                if (length_arg >= NB_COMPONENTS_FOR_DISTANT_AND_SIMULATED_PLAYER) {
                    try {
                        nbIterations = Integer.parseInt(arg[INDEX_PLAYER_ADD_INFO]);
                        if (nbIterations < MIN_ITERATIONS) {
                            printErrorAndExit(
                                    "Erreur : le nombre d'itérations est inférieur à 10 : "
                                            + arg[INDEX_PLAYER_ADD_INFO]);
                        }
                    } catch (NumberFormatException e) {
                        printErrorAndExit(
                                "Erreur : le nombre d'itérations n'est pas un entier int valide : "
                                        + arg[INDEX_PLAYER_ADD_INFO]);
                    }
                }
                ps.put(pId,
                        new PacedPlayer(
                                new MctsPlayer(pId, r[nb_simulated_player + 1], nbIterations),
                                MIN_SECOND));
                ++nb_simulated_player;
                break;
            case "r":
                String adresseIP = "localhost";
                if (length_arg >= NB_COMPONENTS_FOR_DISTANT_AND_SIMULATED_PLAYER) {
                    adresseIP = arg[INDEX_PLAYER_ADD_INFO];
                }
                try {
                    ps.put(pId, new RemotePlayerClient(adresseIP,
                            RemotePlayerClient.PORT_NUMBER));
                } catch (IOException e) {
                    printErrorAndExit(
                            "Erreur : probème de connexion au serveur du joueur distant");
                }
                break;
            default:
                printErrorAndExit("Erreur : spécification de joueur invalide : "
                        + arg[INDEX_PLAYER_SPECIFICATION]);
                break;
            }

            //nom des joueurs
            if (length_arg >= 2 && !arg[INDEX_PLAYER_NAME].equals("")) {
                ns.put(pId, arg[INDEX_PLAYER_NAME]);
            } else {
                ns.put(pId, DEFAULT_NAMES.get(i));
            }

            //message d'erreur si trop de composantes (pour simulated et distant player)
            if (length_arg > NB_COMPONENTS_FOR_DISTANT_AND_SIMULATED_PLAYER) {
                printErrorAndExit(
                        "Erreur : spécification du joueur comportes trop de composantes : "
                                + arg[INDEX_PLAYER_ADD_INFO + 1]);
            }
        }
        
        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(r[0], ps, ns);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(PAUSE_AFTER_TRICK);
                } catch (Exception e) {
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void printErrorAndExit(String s) {
        System.err.println(s);
        System.exit(EXIT_ARG);
    }

    private void explication() {
        System.out.println(
                "Utilisation: java ch.epfl.javass.LocalMain <j1>...<j4> [<graine>] où : \n"
                        + "<jn> spécifie le joueur n, ainsi:\n"
                        + " h:<nom> un joueur humain(local) nommé <nom>\n"
                        + " s:<nom>:<nombre d'itérations> un joueur simulé(local) nommé <nom> et <nombre d'itérations> itérations de l'algorithme MCTS\n"
                        + " r:<nom>:<nom ou adresseIP> un joueur distant nommé <nom> et le serveur du joueur s'éxecute sur <nom ou adresse IP>.\n"
                        + "La première composante est obligatoire, mais les deux autres sont optionnelles. Par défaut les noms suivants sont attribués aux joueurs, dans l'ordre:\n"
                        + "Aline, Bastien, Colette et David. Le nombre d'itérations de l'algorithme MCTS est par défaut de 10 000 et le nom de l'hôte par défaut est localhost.");
        System.exit(EXIT_ARG);
    }
}
