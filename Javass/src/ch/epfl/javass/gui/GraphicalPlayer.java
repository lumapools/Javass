package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Représente l'interface graphique d'un joueur humain
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public class GraphicalPlayer {
	// positions graphiques
	private static final int FIRST_COLUMN = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int THIRD_COLUMN = 2;

	private static final int FIRST_ROW = 0;
	private static final int SECOND_ROW = 1;
	private static final int THIRD_ROW = 2;

	private static final int COL_SPAN = 1;
	private static final int ROW_SPAN = 3;

	// différentes dimensions/valeurs
	private static final int IMAGE_PLAYED_CARDS_WIDTH = 240;
	private static final int IMAGE_HAND_CARDS_WIDTH = 160;
	private static final int TRUMP_SIZE = 100;
	private static final int PLAYED_CARD_HEIGHT = 180;
	private static final int PLAYED_CARD_WIDTH = 120;
	private static final int HAND_CARD_HEIGHT = 120;
	private static final int HAND_CARD_WIDTH = 80;
	private static final int RADIUS_BLUR = 4;
	private static final double OPACITY_PLAYABLE = 1;
	private static final double OPACITY_NOTPLAYABLE = 0.2;
	private static final int INDEX_TEAM1 = 0;
	private static final int INDEX_TEAM2 = 1;
	private static final int INDEX_TEAMPL1 = 0;
	private static final int INDEX_TEAMPL2 = 1;
	private static final int INDEX_BOTPLAYER = 0;
	private static final int INDEX_RIGHTPLAYER = 1;
	private static final int INDEX_TOPPLAYER = 2;
	private static final int INDEX_LEFTPLAYER = 3;

	private static final String SCR_PANE_STYLE = "-fx-font: 16 Optima; -fx-background-color: lightgray; -fx-padding: 5px; -fx-alignment: center;";
	private static final String HALO_STYLE = "-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.5;";
	private static final String CARD_AND_TEXT_STYLE = "-fx-padding: 5px; -fx-alignment: center;";
	private static final String TRICKPANE_STYLE = "-fx-background-color: whitesmoke; -fx-padding: 5px; -fx-border-width: 3px 0px;"
			+ " -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;";
	private static final String VYBORDER_STYLE = "-fx-font: 16 Optima; -fx-background-color: white;";
	private static final String CARDS_STYLE = "-fx-background-color: lightgray; -fx-spacing: 5px; -fx-padding: 5px;";

	// attributs
	private StackPane mainPane;
	private PlayerId ownId;
	private List<PlayerId> playerIds = new ArrayList<PlayerId>();
	private Map<TeamId, List<PlayerId>> teams = new HashMap<>();
	private static final ObservableMap<Card, Image> CARDS = createCardsMap(IMAGE_PLAYED_CARDS_WIDTH);
	private static final ObservableMap<Card, Image> HAND_CARDS = createCardsMap(IMAGE_HAND_CARDS_WIDTH);
	private static final ObservableMap<Color, Image> TRUMP = createTrumpMap();
	private final String name;

	/**
	 * Constructeur de GraphicalPlayer
	 * 
	 * @param playerId (PlayerId) l'identité du joueur auquel l'interface correspond
	 * @param names    (Map<PlayerId, String>) une table associative associant les
	 *                 noms des joueurs à leur identité
	 * @param sB       (ScoreBean) les beans des scores
	 * @param tB       (TrickBean) les beans du pli
	 */
	public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> names, ScoreBean sB, TrickBean tB, HandBean hB,
			ArrayBlockingQueue<Card> blockQueue) {
		this.ownId = ownId;
		this.name = names.get(ownId);
		fillPlayerIds();
		fillTeams();
		BorderPane borderPane = new BorderPane(createTrickPane(ownId, names, tB), createScorePane(names, sB), null,
				createHandPane(hB, blockQueue), null);
		StackPane mainPane = new StackPane();
		mainPane.getChildren().addAll(borderPane, createVictoryPanes(names, sB).get(INDEX_TEAM1),
				createVictoryPanes(names, sB).get(INDEX_TEAM2));
		this.mainPane = mainPane;
	}

	/**
	 * Créer le stage de graphical player
	 * 
	 * @return (Stage) le stage
	 */
	public Stage createStage() {
		Stage stage = new Stage();
		Scene scene = new Scene(mainPane);
		stage.setTitle("Javass - " + name);
		stage.setScene(scene);
		return stage;
	}

	private static ObservableMap<Color, Image> createTrumpMap() {
		ObservableMap<Color, Image> trump = FXCollections.observableHashMap();
		for (Color c : Color.ALL) {
			String s = String.format("/trump_%d.png", c.ordinal());
			trump.put(c, new Image(s));
		}
		return trump;
	}

	private static ObservableMap<Card, Image> createCardsMap(int width) {
		ObservableMap<Card, Image> cards = FXCollections.observableHashMap();
		for (Color c : Color.ALL) {
			for (Rank r : Rank.ALL) {
				String s = String.format("/card_%d_%d_%d.png", c.ordinal(), r.ordinal(), width);
				cards.put(Card.of(c, r), new Image(s));
			}
		}
		return cards;
	}
	

	private void fillPlayerIds() {
		playerIds.add(ownId);
		for (int i = ownId.ordinal() + 1; ownId.ordinal() != i % PlayerId.COUNT; i++) {
			playerIds.add(PlayerId.ALL.get(i % PlayerId.COUNT));
		}
	}

	private void fillTeams() {
		List<PlayerId> team1 = new ArrayList<>();
		List<PlayerId> team2 = new ArrayList<>();
		for (PlayerId pId : playerIds) {
			if (pId.team() == TeamId.TEAM_1) {
				team1.add(pId);
			} else {
				team2.add(pId);
			}
		}
		teams.put(TeamId.TEAM_1, team1);
		teams.put(TeamId.TEAM_2, team2);
	}

	private Pane createScorePane(Map<PlayerId, String> names, ScoreBean scoreBean) {
		GridPane scorePane = new GridPane();
		for (TeamId teamId : TeamId.ALL) {
			// noms des équipes
			Text team = new Text(String.format("%s et %s : ", names.get(teams.get(teamId).get(INDEX_TEAMPL1)),
					names.get(teams.get(teamId).get(INDEX_TEAMPL2))));
			GridPane.setHalignment(team, HPos.RIGHT);

			// score actuel du tour
			Text actualScore = new Text();
			actualScore.textProperty().bind(Bindings.convert(scoreBean.turnPointsProperty(teamId)));
			GridPane.setHalignment(actualScore, HPos.RIGHT);

			// les points gagnés au dernier pli
			Text addedPoints = new Text();
			StringProperty s = new SimpleStringProperty();
			scoreBean.turnPointsProperty(teamId)
					.addListener((o, oV, nV) -> s.set(scoreBean.turnPointsProperty(teamId).get() == 0 ? ""
							: String.format(" (+%d)", ((int) nV - (int) (oV)))));
			addedPoints.textProperty().bind(s);
			GridPane.setHalignment(addedPoints, HPos.LEFT);

			// texte total
			Text total = new Text(" / Total : ");
			GridPane.setHalignment(total, HPos.LEFT);

			// score total (sans le tour courant)
			Text totalScore = new Text();
			totalScore.textProperty().bind(Bindings.convert(scoreBean.gamePointsProperty(teamId)));
			GridPane.setHalignment(totalScore, HPos.RIGHT);

			// les rajouter tous dans le pane
			scorePane.addRow(teamId.ordinal(), team, actualScore, addedPoints, total, totalScore);
		}
		scorePane.setStyle(SCR_PANE_STYLE);
		return scorePane;

	}

	private Pane createTrickPane(PlayerId playerId, Map<PlayerId, String> names, TrickBean tB) {
		List<VBox> list_vBox = new ArrayList<>();
		for (PlayerId pId : playerIds) {
			Text playerName = new Text(names.get(pId));
			playerName.setStyle("-fx-font: 14 Optima;");
			ImageView image = new ImageView();
			image.imageProperty().bind(Bindings.valueAt(CARDS, Bindings.valueAt(tB.trickProperty(), pId)));
			image.setFitWidth(PLAYED_CARD_WIDTH);
			image.setFitHeight(PLAYED_CARD_HEIGHT);

			Rectangle halo = new Rectangle();
			halo.setWidth(PLAYED_CARD_WIDTH);
			halo.setHeight(PLAYED_CARD_HEIGHT);
			halo.setStyle(HALO_STYLE);
			halo.setEffect(new GaussianBlur(RADIUS_BLUR));
			halo.visibleProperty().bind(tB.winningPlayerProperty().isEqualTo(pId));

			StackPane stackPane = new StackPane();
			stackPane.getChildren().addAll(image, halo);
			VBox cardAndText = new VBox();
			if (pId.equals(playerId)) {
				cardAndText.getChildren().addAll(stackPane, playerName);
			} else {
				cardAndText.getChildren().addAll(playerName, stackPane);
			}
			cardAndText.setAlignment(Pos.CENTER);
			cardAndText.setStyle(CARD_AND_TEXT_STYLE);
			list_vBox.add(cardAndText);
		}

		GridPane trickPane = new GridPane();
		// player en bas
		trickPane.add(list_vBox.get(INDEX_BOTPLAYER), SECOND_COLUMN, THIRD_ROW);
		// player à droite
		trickPane.add(list_vBox.get(INDEX_RIGHTPLAYER), THIRD_COLUMN, FIRST_ROW, COL_SPAN, ROW_SPAN);
		// player en haut
		trickPane.add(list_vBox.get(INDEX_TOPPLAYER), SECOND_COLUMN, FIRST_ROW);
		// player à gauche
		trickPane.add(list_vBox.get(INDEX_LEFTPLAYER), FIRST_COLUMN, FIRST_ROW, COL_SPAN, ROW_SPAN);

		ImageView atout = new ImageView();
		atout.setImage(TRUMP.get(Color.CLUB));
		atout.imageProperty().bind(Bindings.valueAt(TRUMP, tB.trumpProperty()));
		atout.setFitHeight(TRUMP_SIZE);
		atout.setFitWidth(TRUMP_SIZE);
		trickPane.add(atout, SECOND_COLUMN, SECOND_ROW);
		GridPane.setHalignment(atout, HPos.CENTER);

		trickPane.setStyle(TRICKPANE_STYLE);

		return trickPane;
	}

	private List<BorderPane> createVictoryPanes(Map<PlayerId, String> names, ScoreBean sB) {
		List<BorderPane> victoryPane = new ArrayList<>();
		for (TeamId team : TeamId.ALL) {
			StringExpression s = Bindings.format("%s et %s ont gagné avec %d points contre %d",
					names.get(teams.get(team).get(0)), names.get(teams.get(team).get(1)), sB.totalPointsProperty(team),
					sB.totalPointsProperty(team.other()));
			Text victoryText = new Text();
			victoryText.textProperty().bind(s);
			BorderPane victoryBorder = new BorderPane(victoryText);
			victoryBorder.visibleProperty().bind(sB.winningTeamProperty().isEqualTo(team));
			victoryBorder.setStyle(VYBORDER_STYLE);
			victoryPane.add(victoryBorder);
		}
		return victoryPane;
	}

	private Pane createHandPane(HandBean hB, ArrayBlockingQueue<Card> blockQueue) {
		HBox cards = new HBox();
		for (int i = 0; i < Jass.HAND_SIZE; ++i) {
			ImageView image = new ImageView();
			image.setFitWidth(HAND_CARD_WIDTH);
			image.setFitHeight(HAND_CARD_HEIGHT);
			image.imageProperty().bind(Bindings.valueAt(HAND_CARDS, Bindings.valueAt(hB.handProperty(), i)));
			int card_number = i;
			image.setOnMouseClicked(event -> {
				try {
					blockQueue.put(hB.handProperty().get(card_number));
				} catch (InterruptedException e) {
					throw new Error();
				}
			});

			BooleanProperty isPlayable = new SimpleBooleanProperty();
			isPlayable.bind(Bindings.createBooleanBinding(() -> {
				return hB.playableCardsProperty().contains(hB.handProperty().get(card_number));
			}, hB.handProperty(), hB.playableCardsProperty()));

			image.opacityProperty()
					.bind(Bindings.when(isPlayable).then(OPACITY_PLAYABLE).otherwise(OPACITY_NOTPLAYABLE));
			image.disableProperty().bind(isPlayable.not());
			cards.getChildren().add(image);
		}
		cards.setStyle(CARDS_STYLE);

		return cards;
	}
}