package ch.epfl.javass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
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

	@Override
	public void start(Stage primaryStage) throws Exception {
		int sizeArgs = getParameters().getRaw().size();
		if(sizeArgs != 4 && sizeArgs != 5) {
			System.out.println("Error");
			System.exit(1);
		}
		
		
		String player1 = getParameters().getRaw().get(0);
		String player2 = getParameters().getRaw().get(1);
		String player3 = getParameters().getRaw().get(2);
		String player4 = getParameters().getRaw().get(3);
		String seed = "";
		if(sizeArgs == 5) {
			seed = getParameters().getRaw().get(4);
		}
		
		List<String> defaultNames = Arrays.asList("Aline", "Bastien", "Colette", "David");
		
		List<String> pl1StrList = Arrays.asList(StringSerializer.split(':', player1));
		List<String> pl2StrList = Arrays.asList(StringSerializer.split(':', player2));
		List<String> pl3StrList = Arrays.asList(StringSerializer.split(':', player3));
		List<String> pl4StrList = Arrays.asList(StringSerializer.split(':', player4));
		
		List<List<String>> strLists = Arrays.asList(pl1StrList, pl2StrList, pl3StrList, pl4StrList);
		
		for(int i = 0; i < strLists.size(); i++) {
			System.out.println(strLists.get(i).get(0));
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
						default:
							break;
					}
				break;
				case "s":
					switch(strLists.get(i).size()) {
						case 1:
							players.add(new MctsPlayer(PlayerId.ALL.get(i), 1000, DEF_ITER));
							names.add(names.get(i));
							break;
						case 2:
							if(strLists.get(i).get(1).isEmpty()) {
								players.add(new MctsPlayer(PlayerId.ALL.get(i), 1000, Integer.parseInt(strLists.get(i).get(2))));
								names.add(names.get(i));
							}
							else {
								players.add(new MctsPlayer(PlayerId.ALL.get(i), Long.parseLong(strLists.get(i).get(2)), DEF_ITER));
								names.add(strLists.get(i).get(1));
							}
							break;
						case 3:
							players.add(new MctsPlayer(PlayerId.ALL.get(i), Long.parseLong(strLists.get(i).get(2)), DEF_ITER));
							names.add(strLists.get(i).get(1));
							break;
						default:
							break;
					}
				break;
				case "r":
					System.out.println(strLists.get(i).size());
					switch(strLists.get(i).size()) {
						case 1:
							players.add(new RemotePlayerClient(DEF_SERVER, RemotePlayerClient.PORT_NUMBER));
							names.add("Alice");
							break;
						case 2:
							if(strLists.get(i).get(1).isEmpty()) {
								players.add(new RemotePlayerClient(strLists.get(i).get(2), RemotePlayerClient.PORT_NUMBER));
								names.add(defaultNames.get(i));
							}
							else {
								players.add(new RemotePlayerClient(DEF_SERVER, RemotePlayerClient.PORT_NUMBER));
								names.add(strLists.get(i).get(1));
							}
							break;
						case 3:
							players.add(new RemotePlayerClient(strLists.get(i).get(2), RemotePlayerClient.PORT_NUMBER));
							names.add(strLists.get(i).get(1));
							break;
					}
				}
		}
		
		Map<PlayerId, Player> playersMap = new HashMap<PlayerId, Player>();
		Map<PlayerId, String> namesMap = new HashMap<PlayerId, String>();
		
		for(int i = 0; i < 4; i++) {
			playersMap.put(PlayerId.ALL.get(i), players.get(i));
			namesMap.put(PlayerId.ALL.get(i), names.get(i));
		}
		
		
		Thread gameThread = new Thread(() -> {
			JassGame g = new JassGame(0, playersMap, namesMap);
			while (! g.isGameOver()) {
			  g.advanceToEndOfNextTrick();
			  try { Thread.sleep(1000); } catch (Exception e) {}
			}
		    });
		gameThread.setDaemon(true);
		gameThread.start();
	}

}
