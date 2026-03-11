package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Scene that is called after the game has ended to show all the scores and leaderboard stats
 */

public class ScoresScene extends BaseScene{
    private static final Logger logger = LogManager.getLogger(ScoresScene.class);

    /**
     * Local scores proprty to be observed
     */
    public ObservableList<Pair<String,Integer>> localScores = new SimpleListProperty<>();
    /**
     * remote scores property to be observed for leaderboard
     */
    public ObservableList<Pair<String, Integer>> remoteScores = new SimpleListProperty<>();
    private ScoresList scoresList;
    private ScoresList remoteScoresList;

    /**
     * String property for names
     */
    protected final StringProperty names = new SimpleStringProperty("");
    protected final StringProperty OnlineNames = new SimpleStringProperty("");

    protected final Communicator comms;
    Integer currentSessionScore;
    private String userName;

    private Multimedia bgPlayer;
    private ArrayList<Pair<String, Integer>> returnOnlineScores;
    /**
     * Contructs score scene
     * @param gameWindow the current window which is passed
     * @param currentScore the score from the game that has just been played
     */
    public ScoresScene(GameWindow gameWindow, Integer currentScore) {
        super(gameWindow);
        currentSessionScore = currentScore;
        comms = gameWindow.getCommunicator();
        bgPlayer = new Multimedia();
    }

    /**
     * Handles messages from the server
     * @param message the message from the server
     */

    public void handleCommunication(String message){
        String[] components = message.split(" ", 2);
        String command = components[0];

        if (command.equals("HISCORES")) {
            if (components.length > 1) {
                String data = components[1];
                loadOnlineScores(data);
            }
        }
    }

    /**
     * Intilises everything and sends the appropriate messages to the server
     */
    @Override
    public void initialise() {
        remoteScoresList = new ScoresList();
        comms.addListener(this::handleCommunication);
        comms.send("HISCORES");
        //comms.send("HISCORES DEFAULT"); //for testing send

        gameWindow.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                logger.info("Button pressed Escape");
                gameWindow.startMenu();
            }
        });
    }
    /**
     * Builds the initial scene with either your score displayed or a text field to input your name so that the highscore
     * can be updated
     */
    @Override
    public void build() {
        bgPlayer.playMusic("end.wav");
        localScores = FXCollections.observableArrayList(loadScores());
        scoresList = new ScoresList();
        localScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        AtomicReference<SimpleListProperty<Pair<String, Integer>>> scoresWrap = new AtomicReference<>(new SimpleListProperty<>(localScores));
        scoresList = new ScoresList();
        scoresList.returnScores().bind(scoresWrap.get());
        scoresList.returnNames().bind(names);
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());



        var ScoresPane = new StackPane();
        ScoresPane.setMaxWidth(gameWindow.getWidth());
        ScoresPane.setMaxHeight(gameWindow.getHeight());
        ScoresPane.getStyleClass().add("menu-background");
        root.getChildren().add(ScoresPane);

        var mainPane = new BorderPane();
        ScoresPane.getChildren().add(mainPane);
        Label title = new Label("Game Over!");
        var titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        title.getStyleClass().add("title");
        mainPane.setTop(titleBox);
        Boolean change = false;
        for(Pair<String, Integer> i : scoresList.returnScores()){
            logger.info("Checking score for " + i.getKey() + " with value of: " + i.getValue());
            logger.info("Current user's score is: " + currentSessionScore);
            if(currentSessionScore > i.getValue()){
                change = true;
            }
        }
        if(change){
            Label enterNameLabel = new Label("Enter Name: ");
            TextField enterName = new TextField();
            enterNameLabel.getStyleClass().add("scoreitem");
            var infoAdder = new VBox();
            Button submitName = new Button("Submit Score");
            submitName.getStyleClass().add("score");
            infoAdder.setPadding(new Insets(10,10,10,10));
            infoAdder.getChildren().addAll(enterNameLabel, enterName, submitName);
            mainPane.setCenter(infoAdder);
            infoAdder.setAlignment(Pos.CENTER);
            submitName.setOnAction((e) -> {
                userName = enterName.getText();
                ArrayList<Pair<String, Integer>> gonnaWriteThisOne = new ArrayList<>();
                for(Pair<String, Integer> i : scoresList.returnScores()){
                    gonnaWriteThisOne.add(i);
                }
                gonnaWriteThisOne.add(new Pair<>(userName, currentSessionScore));
                writeScores(gonnaWriteThisOne);
                localScores = FXCollections.observableArrayList(loadScores());
                scoresList = new ScoresList();
                localScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                scoresWrap.set(new SimpleListProperty<>(localScores));
                scoresList = new ScoresList();
                scoresList.returnScores().bind(scoresWrap.get());
                scoresList.returnNames().bind(names);
                infoAdder.getChildren().clear();
                finishBuild(mainPane);
            });
        }
        else{

            Label enterNameLabel = new Label("Your Score: " + currentSessionScore);
            enterNameLabel.setPadding(new Insets(10,10,50,10));
            enterNameLabel.getStyleClass().add("scoreitem");
            var infoAdder = new VBox();
            Button submitName = new Button("Continue");
            submitName.setPadding(new Insets(0,0,0,0));
            submitName.getStyleClass().add("score");
            infoAdder.setPadding(new Insets(10,10,10,10));
            infoAdder.getChildren().addAll(enterNameLabel, submitName);
            mainPane.setCenter(infoAdder);
            infoAdder.setAlignment(Pos.CENTER);
            submitName.setOnAction((e) -> {
                ArrayList<Pair<String, Integer>> gonnaWriteThisOne = new ArrayList<>();
                for(Pair<String, Integer> i : scoresList.returnScores()){
                    gonnaWriteThisOne.add(i);
                }
                writeScores(gonnaWriteThisOne);
                localScores = FXCollections.observableArrayList(loadScores());
                scoresList = new ScoresList();
                localScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                scoresWrap.set(new SimpleListProperty<>(localScores));
                scoresList = new ScoresList();
                scoresList.returnScores().bind(scoresWrap.get());
                scoresList.returnNames().bind(names);
                infoAdder.getChildren().clear();
                finishBuild(mainPane);
            });
        }

    }
    /**
     * finishs the build with the two leaderboards
     * @param mainPane the pane so that the leaderboards can be added
     */
    public void finishBuild(BorderPane mainPane){
        Boolean toSend = false;
        for(Pair<String, Integer> i : returnOnlineScores){
            if(currentSessionScore > i.getValue()){
                toSend = true;
            }
        }
        if(toSend) {
            returnOnlineScores.add(0, new Pair<String, Integer>(userName, currentSessionScore));
            comms.send("HISCORE " + userName + ":" + currentSessionScore);
        }

        Label localScoresLabel = new Label("Local Scores");
        localScoresLabel.setTextAlignment(TextAlignment.CENTER);
        localScoresLabel.getStyleClass().add("heading");
        var scoresBox = new VBox(localScoresLabel, scoresList);
        scoresBox.setAlignment(Pos.CENTER);
        scoresBox.setPadding(new Insets(20,20,10,60));
        mainPane.setLeft(scoresBox);
        Label remoteScoresLabel = new Label("Online Scores");
        remoteScoresLabel.setTextAlignment(TextAlignment.CENTER);
        remoteScoresLabel.getStyleClass().add("heading");
        var remoteScoresBox = new VBox(remoteScoresLabel, remoteScoresList);
        remoteScoresBox.setAlignment(Pos.CENTER);
        remoteScoresBox.setPadding(new Insets(20,60,10,20));
        remoteScoresList.updateList();
        mainPane.setRight(remoteScoresBox);
        Label closeGame = new Label("esc to menu.");
        closeGame.setTextAlignment(TextAlignment.CENTER);
        closeGame.getStyleClass().add("heading");
        mainPane.setBottom(closeGame);
    }
    /**
     * Loads the scoresf rom the text files
     * @return returns loaded scores
     */
    public ArrayList<Pair<String, Integer>> loadScores(){
        ArrayList<Pair<String, Integer>> toReturn = new ArrayList<>();

        try{
            var scoreFile = Paths.get("localScores.txt");
            List<String> scores = Files.readAllLines(scoreFile);
            for (String score : scores) {
                String[] parts = score.split(":");
                toReturn.add(new Pair<>(parts[0], Integer.parseInt(parts[1])));
                logger.info("Name: " + parts[0] + " Score : " + parts[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return toReturn;
    }
    /**
     * writes scores to the textfile
     * @param scores scores to be written
     */
    public void writeScores(List<Pair<String, Integer>> scores) {
        scores.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        try {
            Path scoresFile = Paths.get("localScores.txt");
            StringBuilder toWrite = new StringBuilder();
            if(Files.notExists(scoresFile)) {
                for (int i = 0; i < 10; ++i) {
                    toWrite.append("Test").append(i).append(":").append("0").append("\n");
                }

            }
            else {

                for (Pair<String, Integer> score : scores) {
                    String scoreString = score.getKey();
                    toWrite.append(scoreString).append(":").append(score.getValue()).append("\n");
                }
                Files.writeString(scoresFile, toWrite.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Loads scores from the server
     * @param s the message from the server
     */
    public void loadOnlineScores(String s){
        remoteScoresList.getChildren().clear();
        String[] scoreLines = s.split("\\R");
        ArrayList<Pair<String,Integer>> toReturn = new ArrayList<>();

        for (String scoreLine : scoreLines) {
            String[] parts = scoreLine.split(":", 2);
            toReturn.add(new Pair<>(parts[0], Integer.parseInt(parts[1])));
        }

        toReturn.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        returnOnlineScores = toReturn;
        remoteScores = FXCollections.observableArrayList(toReturn);
        SimpleListProperty<Pair<String, Integer>> remoteScoresWrap = new SimpleListProperty<>(remoteScores);
        remoteScoresList.returnScores().bind(remoteScoresWrap);
        remoteScoresList.returnNames().bind(OnlineNames);
        logger.info("Online scored loaded");
        logger.info(toReturn);
        logger.info(returnOnlineScores);
        remoteScoresList.updateList();

    }
}
