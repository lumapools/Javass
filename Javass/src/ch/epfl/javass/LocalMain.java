package ch.epfl.javass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public class LocalMain extends Application {
	
	private List<Player> players = new ArrayList<Player>();
	private List<String> names = new ArrayList<String>();
	private final int DEF_ITER = 10_000;
	private final String DEF_SERVER = "localhost";
	private final List<String> defaultNames = Arrays.asList("Aline", "Bastien", "Colette", "David");

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Random rng = new Random();
		
		int sizeArgs = getParameters().getRaw().size();
		if(sizeArgs != 4 && sizeArgs != 5) {
			msgUtilisation();
			System.exit(1);
		}
		
		long[] rngSim = new long[5];
		
		
		String player1 = getParameters().getRaw().get(0);
		String player2 = getParameters().getRaw().get(1);
		String player3 = getParameters().getRaw().get(2);
		String player4 = getParameters().getRaw().get(3);
		rng = new Random();
		String seed = "";
		long rngJass = rng.nextLong();
		if(sizeArgs == 5) {
			seed = getParameters().getRaw().get(4);
			try {
				long randomSeed = Long.parseLong(seed);
				rng = new Random(randomSeed);
				for(int i = 0; i < 5; i++) {
					rngSim[i] = rng.nextLong();
				}
			}
			catch (Exception e) {
				System.err.println("Erreur: La graine doit être de type long");
				System.exit(1);
			}	
		}
		else {
			for(int i = 0; i < 4; i++) {
				rngSim[i] = rng.nextLong();
			}
		}
		
		
		
		List<String> pl1StrList = Arrays.asList(StringSerializer.split(':', player1));
		List<String> pl2StrList = Arrays.asList(StringSerializer.split(':', player2));
		List<String> pl3StrList = Arrays.asList(StringSerializer.split(':', player3));
		List<String> pl4StrList = Arrays.asList(StringSerializer.split(':', player4));
		
		List<List<String>> strLists = Arrays.asList(pl1StrList, pl2StrList, pl3StrList, pl4StrList);
		
		for(int i = 0; i < strLists.size(); i++) {
			switch(strLists.get(i).get(0)) {
				case "h":
					switch(strLists.get(i).size()) {
						case 1 :
							players.add(new GraphicalPlayerAdapter());
							names.add(defaultNames.get(i));
							break;
						case 2:
							players.add(new GraphicalPlayerAdapter());
							names.add(strLists.get(i).get(1));
							break;
					}
				break;
				case "s":
					switch(strLists.get(i).size()) {
						case 1:
							players.add(new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i), rngSim[i], DEF_ITER), 2));
							names.add(defaultNames.get(i));
							break;
						case 2:
							if(strLists.get(i).get(1).isEmpty()) {
								players.add(new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i), rngSim[i], Integer.parseInt(strLists.get(i).get(2))), 2));
								names.add(defaultNames.get(i));
								break;

							}
							else {
								players.add(new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i), rngSim[i], DEF_ITER), 2));
								names.add(strLists.get(i).get(1));
								break;

							}
						case 3:
							players.add(new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i), rngSim[i], DEF_ITER), 2));
							names.add(strLists.get(i).get(1));
							break;
					}
				break;
				case "r":
					System.out.println(strLists.get(i).size());
					switch(strLists.get(i).size()) {
						case 1:
							players.add(new RemotePlayerClient(DEF_SERVER, RemotePlayerClient.PORT_NUMBER));
							names.add(defaultNames.get(i));
							break;
						case 2:
							if(strLists.get(i).get(1).isEmpty()) {
								players.add(new RemotePlayerClient(strLists.get(i).get(2), RemotePlayerClient.PORT_NUMBER));
								names.add(defaultNames.get(i));
								break;
							}
							else {
								players.add(new RemotePlayerClient(DEF_SERVER, RemotePlayerClient.PORT_NUMBER));
								names.add(strLists.get(i).get(1));
								break;
							}
						case 3:
							players.add(new RemotePlayerClient(strLists.get(i).get(2), RemotePlayerClient.PORT_NUMBER));
							names.add(strLists.get(i).get(1));
							break;
					}
				break;
				default: 
					System.err.println("Erreur: Spécification de joueur invalide: " + strLists.get(i).get(0));
					System.exit(1);
					break;
				}
		}
		
		Map<PlayerId, Player> playersMap = new HashMap<PlayerId, Player>();
		Map<PlayerId, String> namesMap = new HashMap<PlayerId, String>();
		
		
		for(int i = 0; i < 4; i++) {
			playersMap.put(PlayerId.ALL.get(i), players.get(i));
			namesMap.put(PlayerId.ALL.get(i), names.get(i));
		}
		
		
		Thread gameThread = new Thread(() -> {
			JassGame g = new JassGame(rngJass, playersMap, namesMap);
			while (! g.isGameOver()) {
			  g.advanceToEndOfNextTrick();
			  try { Thread.sleep(1000); } catch (Exception e) {}
			}
		    });
		gameThread.setDaemon(true);
		gameThread.start();
	}
	
	private void msgUtilisation() {
		System.out.println("Utilisation java ch.epfl.javass.LocalMain <j1><j2><j3><j4>(<graine>)");
		System.out.println("<jn> humain défini ainsi:   h:<nom>");
		System.out.println("<jn> simulé défini ainsi:   s:<nom>:<iterations>");
		System.out.println("<jn> distant défini ainsi:  r:<nom>:<Adresse IP ou nom de l'hôte>");
		System.out.println("Erreur lorsque :");
		System.out.println("1: La première composante d'une spécification de joueur n'est pas h, s ou r");
		System.out.println("2: La spécification de joueurs comporte trop de composantes (ex: h:Marie:12)");
		System.out.println("3: Il y a une erreur de connexion au serveur distant");
		System.out.println("4: La graine aléatoire n'est pas un entier de type long valide");
		System.out.println("5: Le nombre de simulations pour un joueur simulé n'est pas entier ou est inférieur à 10.");
	}

}
