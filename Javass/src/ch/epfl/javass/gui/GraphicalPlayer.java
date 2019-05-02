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

    // différentes dimensions
    private static final int IMAGE_CARDS_WIDTH = 240;
    private static final int TRUMP_SIZE = 100;
    private static final int HEIGHT_CARD = 180;
    private static final int WIDTH_CARD = 160;
    private static final int RADIUS_BLUR = 4;
    private static final int GRIDPANE_GAP = 10;

    //attributs
    private StackPane mainPane;
    private PlayerId playerId;
    private ArrayBlockingQueue<Card> queue;
    private List<PlayerId> playerIds = new ArrayList<PlayerId>();
    private Map<TeamId, List<PlayerId>> teams = new HashMap<>();
    private static final ObservableMap<Card, Image> CARDS = createCardsMap(
            IMAGE_CARDS_WIDTH);
    private final ObservableMap<Card, Image> HAND_MAP = createCardsMap(160);
    private static final ObservableMap<Color, Image> TRUMP = createTrumpMap();

    /**
     * Constructeur de GraphicalPlayer
     * 
     * @param playerId
     *            (PlayerId) l'identité du joueur auquel l'interface correspond
     * @param names
     *            (Map<PlayerId, String>) une table associative associant les
     *            noms des joueurs à leur identité
     * @param sB
     *            (ScoreBean) les beans des scores
     * @param tB
     *            (TrickBean) les beans du pli
     * 
     * @param hB
     * 				(HandBean) les beans de la main
     * 
     * @param queue
     * 				(ArrayBlockingQueue<Card>) la queue de communication
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> names,
            ScoreBean sB, TrickBean tB, HandBean hB, ArrayBlockingQueue<Card> queue) {
        this.playerId = playerId;
        this.queue = queue;
        fillPlayerIds();
        fillTeams();
        BorderPane borderPane = new BorderPane(
                createTrickPane(playerId, names, tB), createScorePane(names, sB),
                null, createHandPane(hB), null);
        StackPane mainPane = new StackPane();
        mainPane.getChildren().addAll(borderPane,
                createVictoryPanes(names, sB).get(0),
                createVictoryPanes(names, sB).get(1));
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
        stage.setTitle("Javass " + playerId);
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
                String s = String.format("/card_%d_%d_%d.png", c.ordinal(),
                        r.ordinal(), width);
                cards.put(Card.of(c, r), new Image(s));
            }
        }
        return cards;
    }

    private void fillPlayerIds() {
        playerIds.add(playerId);
        for (int i = playerId.ordinal() + 1; playerId.ordinal() != i
                % PlayerId.COUNT; i++) {
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

    private Pane createScorePane(Map<PlayerId, String> names,
            ScoreBean scoreBean) {

        GridPane scorePane = new GridPane();
        for (TeamId teamId : TeamId.ALL) {
            Text team = new Text(String.format("%s et %s : ",
                    names.get(teams.get(teamId).get(0)),
                    names.get(teams.get(teamId).get(1))));
            GridPane.setHalignment(team, HPos.RIGHT);
            Text actualScore = new Text();
            GridPane.setHalignment(actualScore, HPos.RIGHT);
            Text addedPoints = new Text();
            GridPane.setHalignment(actualScore, HPos.LEFT);
            Text total = new Text(" / Total : ");
            GridPane.setHalignment(total, HPos.LEFT);
            Text totalScore = new Text();
            GridPane.setHalignment(actualScore, HPos.RIGHT);
            actualScore.textProperty().bind(Bindings.format("%d",
                    scoreBean.turnPointsProperty(teamId)));
            totalScore.textProperty().bind(Bindings.format("%d",
                    scoreBean.gamePointsProperty(teamId)));
            scoreBean.turnPointsProperty(teamId).addListener((o, oV, nV) -> {
            	if((int)nV - (int)(oV) < 0) {
            		addedPoints.setText("");
            	}
            	else { 
            		addedPoints.setText(
                        String.format("(+ %d)", ((int) nV - (int) (oV))));
            	}
            });

            scorePane.addRow(teamId.ordinal(), team, actualScore, addedPoints,
                    total, totalScore);

        }
        scorePane.setStyle(
                "-fx-font: 16 Optima; -fx-background-color: lightgray; -fx-padding: 5px; -fx-alignment: center;");
        return scorePane;

    }

    private Pane createTrickPane(PlayerId playerId, Map<PlayerId, String> names,
            TrickBean tB) {
        List<VBox> list_vBox = new ArrayList<>();
        for (PlayerId pId : playerIds) {
            Text playerName = new Text(names.get(pId));
            playerName.setStyle("-fx-font: 14 Optima;");
            ImageView image = new ImageView();
            image.imageProperty().bind(Bindings.valueAt(CARDS,
                    Bindings.valueAt(tB.trickProperty(), pId)));
            image.setFitWidth(WIDTH_CARD);
            image.setFitHeight(HEIGHT_CARD);

            Rectangle halo = new Rectangle();
            halo.setWidth(WIDTH_CARD);
            halo.setHeight(HEIGHT_CARD);
            halo.setStyle("-fx-arc-width: 20;\n" + "-fx-arc-height: 20;\n"
                    + "-fx-fill: transparent;\n" + "-fx-stroke: lightpink;\n"
                    + "-fx-stroke-width: 5;\n" + "-fx-opacity: 0.5;");
            halo.setEffect(new GaussianBlur(RADIUS_BLUR));
            halo.visibleProperty()
                    .bind(tB.winningPlayerProperty().isEqualTo(pId));

            StackPane haloPane = new StackPane();
            haloPane.getChildren().addAll(image, halo);
            VBox cardAndText = new VBox();
            if (pId.equals(playerId)) {
                cardAndText.getChildren().addAll(haloPane, playerName);
            } else {
                cardAndText.getChildren().addAll(playerName, haloPane);
            }
            cardAndText.setAlignment(Pos.CENTER);
            list_vBox.add(cardAndText);
        }

        GridPane trickPane = new GridPane();
        // player en bas
        trickPane.add(list_vBox.get(0), SECOND_COLUMN, THIRD_ROW);
        // player à droite
        trickPane.add(list_vBox.get(1), THIRD_COLUMN, FIRST_ROW, COL_SPAN,
                ROW_SPAN);
        // player en haut
        trickPane.add(list_vBox.get(2), SECOND_COLUMN, FIRST_ROW);
        // player à gauche
        trickPane.add(list_vBox.get(3), FIRST_COLUMN, FIRST_ROW, COL_SPAN,
                ROW_SPAN);

        ImageView atout = new ImageView();
        atout.setImage(TRUMP.get(Color.CLUB));
        atout.imageProperty().bind(Bindings.valueAt(TRUMP, tB.trumpProperty()));
        atout.setFitHeight(TRUMP_SIZE);
        atout.setFitWidth(TRUMP_SIZE);
        trickPane.add(atout, SECOND_COLUMN, SECOND_ROW);
        GridPane.setHalignment(atout, HPos.CENTER);

        trickPane.setStyle("-fx-background-color: whitesmoke;\n"
                + "-fx-padding: 5px;\n" + "-fx-border-width: 3px 0px;\n"
                + "-fx-border-style: solid;\n" + "-fx-border-color: gray;\n"
                + "-fx-alignment: center;");
        trickPane.setHgap(GRIDPANE_GAP);
        trickPane.setVgap(GRIDPANE_GAP);

        return trickPane;
    }

    private List<BorderPane> createVictoryPanes(Map<PlayerId, String> names,
            ScoreBean sB) {
        List<BorderPane> victoryPane = new ArrayList<>();
        for (TeamId team : TeamId.ALL) {
            StringExpression s = Bindings.format(
                    "%s et %s ont gagné avec %d points contre %d",
                    names.get(teams.get(team).get(0)),
                    names.get(teams.get(team).get(1)),
                    sB.totalPointsProperty(team),
                    sB.totalPointsProperty(team.other()));
            Text victoryText = new Text();
            victoryText.textProperty().bind(s);
            BorderPane victoryBorder = new BorderPane(victoryText);
            victoryBorder.visibleProperty()
                    .bind(sB.winningTeamProperty().isEqualTo(team));
            victoryBorder.setStyle(
                    "-fx-font: 16 Optima;\n" + "-fx-background-color: white;");
            victoryPane.add(victoryBorder);
        }
        return victoryPane;
    }
    
    private HBox createHandPane(HandBean hB) {
    	HBox handPane = new HBox();
    	handPane.setStyle("-fx-background-color: lightgray; -fx-spacing: 5px; -fx-padding: 5px;");
    	for(int i = 0; i < Jass.HAND_SIZE; i++) {
    		ImageView cardImage = new ImageView();
    		cardImage.setFitWidth(80);
            cardImage.setFitHeight(120);
    		cardImage.imageProperty().bind(Bindings.valueAt(HAND_MAP, Bindings.valueAt(hB.handProperty(), i)));
    		handPane.getChildren().add(cardImage);
    		int index = i;
    		BooleanProperty isPlayable = new SimpleBooleanProperty();
            isPlayable.bind(Bindings.createBooleanBinding(() -> {
                return hB.playableCardsProperty()
                        .contains(hB.handProperty().get(index));
            }, hB.handProperty(), hB.playableCardsProperty()));
    		
    		cardImage.opacityProperty().bind(Bindings.when(isPlayable).then(1).otherwise(0.2));
    		
    		
    		cardImage.disableProperty().bind(isPlayable.not());
    		
    		cardImage.setOnMouseClicked(event -> {
    			try {
					queue.put(hB.handProperty().get(index));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		});;
    		
    	}
    		
    	return handPane;
    }
}