package uk.ac.soton.comp1206.scene;

import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
/**
 * Multiplayer scene which extends challenge Scene
 */
public class MultiplayerScene extends ChallengeScene {
    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);

    private Timer timer;

    /**
     * Makes sure the same commmunicator is being used from the LobbyScene
     */
    protected static Communicator comms = LobbyScene.comms;
    Color whitesparent = new Color(0,0,0,0.5);

    private ScrollPane container = new ScrollPane();

    private VBox messagesBox = new VBox();;
    private ScoresList scoresList;
    private Leaderboard leaderboard;

    /**
     * Creates an Observable list which is to be used to bind to Leaderboard class
     */
    protected ObservableList<Pair<String, Integer>> remoteScoreList;
    private final ArrayList<Pair<String, Integer>> remoteScores = new ArrayList<>();
    private final StringProperty names = new SimpleStringProperty();

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        leaderboard = new Leaderboard();
    }
    /**
     * Sets up the game creating a new multiplayer game and setting the next piece listener
     */
    @Override
    public void setupGame(){
        game = new MultiplayerGame(5, 5, bgPlayer, comms);
        game.setNextPieceListener(nextPieceListener);
    }
    /**
     * Initialises timer and sets listener for messages recieved from communicator
     */
    public void initialise(){
        super.initialise();
        comms.addListener(this::handleCommunication);

        resetTimer();
    }
    /**
     * resets the timer and sets the correct message to update scores to the server
     */
    private void resetTimer() {
        TimerTask refresh =new TimerTask() {
            @Override
            public void run() {
                comms.send("SCORES");
            }
        };
        timer = new Timer();
        timer.schedule(refresh, 0, 5000);
    }
    /**
     * Handles all the messages received from the server
     * @param s message received from the server
     */
    private void handleCommunication(String s) {
        String[] components = s.split(" ", 2);
        String toDo = components[0];
        String content  = (components[1]);
        if(toDo.equals("MSG")){
            Platform.runLater(() -> showMesasage(content));
        }
        if(toDo.equals("SCORES")){
            Platform.runLater(() -> updateScoreboard(content));
        }
    }
    /**
     * Updates the scoreboard
     * @param content the scores received from the server
     */
    private void updateScoreboard(String content) {
        remoteScoreList = FXCollections.observableArrayList(remoteScores);
        SimpleListProperty<Pair<String, Integer>> scoreWrapper =
                new SimpleListProperty<>(remoteScoreList);


        leaderboard.getStyleClass().add("leaderboard");
        leaderboard.returnScores().bind(scoreWrapper);
        leaderboard.returnNames().bind(names);
    }
    /**
     * Updates the chatbox with new messages
     * @param content the message recieved from the server
     */
    private void showMesasage(String content) {
        messagesBox.getChildren().add(new Text(content));
    }

    /**
     * Parts of the build process that are different to a normal challengescene
     */

    @Override
    public void nonMultiParts() {
        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/3,gameWindow.getWidth()/3);
    }
    /**
     * Adds the leaderboard and chatbox to the scene
     */
    public void build(){
        super.build();
        VBox multiplayerBits = new VBox();
        multiplayerBits.setBackground(new Background(new BackgroundFill(whitesparent, CornerRadii.EMPTY, Insets.EMPTY)));
        super.mainPane.setLeft(multiplayerBits);
        Label leaderLabel = new Label("Leaderboard");
        leaderLabel.getStyleClass().add("heading");
        leaderLabel.setAlignment(Pos.TOP_LEFT);
        leaderboard.setAlignment(Pos.TOP_LEFT);
        container.setPrefHeight(gameWindow.getHeight()/4);
        messagesBox.setBackground(new Background(new BackgroundFill(whitesparent, CornerRadii.EMPTY, Insets.EMPTY)));
        container.setContent(messagesBox);
        messagesBox.setPadding(new Insets(300,0,0,0));
        container.setBackground(new Background(new BackgroundFill(whitesparent, CornerRadii.EMPTY, Insets.EMPTY)));
        messagesBox.setAlignment(Pos.BOTTOM_LEFT);
        multiplayerBits.getChildren().addAll(leaderLabel, leaderboard, container);
        TextField sendText = new TextField();
        Button sendButton = new Button("Send");
        sendButton.setAlignment(Pos.BOTTOM_RIGHT);
        sendButton.setOnAction((e) -> {
            comms.send("MSG " + sendText.getText());
            container.setVvalue(container.getVmax());
            sendText.clear();
        });
        HBox messageBar = new HBox();
        messageBar.getChildren().addAll(sendText, sendButton);
        multiplayerBits.getChildren().add(messageBar);
    }
}