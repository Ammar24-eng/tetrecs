package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.*;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);

    /**
     * game object
     */
    public Game game;

    /**
     * gameboard object
     */
    public GameBoard board;

    /**
     * media player
     */
    public Multimedia bgPlayer;
    private PieceBoard currentBlock;
    private PieceBoard followingBlock;
    int currentX;
    int currentY;
    boolean mouseMode;
    Timeline timeline;

    /**
     * nextPieceListener object with method override
     */
    NextPieceListener nextPieceListener = new NextPieceListener() {
        @Override
        public void nextPiece(GamePiece currentPiece, GamePiece nextPiece) {
            currentBlock.setPiece(currentPiece);
            followingBlock.setPiece(nextPiece);
        }

    };
    private Label levelLabel;
    private Label scoreLabel;
    private Label livesLabel;
    private Label multiplierLabel;
    private Label hiScoreLabel;
    public IntegerProperty hiScore = new SimpleIntegerProperty();
    private GameLoopListener gameLoopListener;
    private long delay;
    private Rectangle timerBar;
    private long timerBarIWidth;
    private AnimationTimer barTimer;

    ScaleTransition transition;
    public BorderPane mainPane;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
        bgPlayer = new Multimedia();
        mouseMode = true;
    }
    /**
     * Parts of the challenge scene that I want to change in multiplayerScene
     */
    public void nonMultiParts(){
        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
    }
    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        nonMultiParts();
        mainPane.setCenter(board);

        //Handle block on gameboard grid being clicked

        board.setOnBlockClick(this::blockClicked);
        board.setOnContextMenuRequested((e) -> gameboardRightClicked());
        game.setOnLineCleared(this::lineCleared);
        game.setOnGameLoop(this::timerAnimation);
        game.setOnGameEnd(this::gameOver);

        scoreLabel = new Label();
        scoreLabel.getStyleClass().add("score");
        levelLabel = new Label();
        levelLabel.getStyleClass().add("level");
        livesLabel = new Label();
        livesLabel.getStyleClass().add("lives");
        multiplierLabel = new Label();
        multiplierLabel.getStyleClass().add("score");
        scoreLabel.textProperty().bind(game.Score.asString("Score: %d"));
        levelLabel.textProperty().bind(game.level.asString("Level: %d"));
        livesLabel.textProperty().bind(game.lives.asString("Lives: %d"));
        multiplierLabel.textProperty().bind(game.multiplier.asString("Multiplier: %d"));

        hiScore.set(getHighScore());
        hiScoreLabel = new Label();
        hiScoreLabel.textProperty().bind(hiScore.asString("High Score: %d"));
        hiScoreLabel.getStyleClass().add("score");


        currentBlock = new PieceBoard(new Grid(3, 3), gameWindow.getWidth()/5, gameWindow.getWidth()/5 );
        currentBlock.getBlock(1,1).setCircleBlock();
        followingBlock = new PieceBoard(new Grid(3, 3), gameWindow.getWidth()/6, gameWindow.getWidth()/6);
        currentBlock.setOnMouseClicked((e) -> {if(e.getButton()== MouseButton.PRIMARY){
            gameboardRightClicked();
        }
        });
        followingBlock.setOnMouseClicked((e) -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                game.swapCurrentPiece();
                currentBlock.setPiece(game.getCurrentPiece());
                followingBlock.setPiece(game.getFollowingPiece());
                bgPlayer.playAudio("transition.wav");
            }});

        var infoPane = new VBox(10, currentBlock,followingBlock, levelLabel, scoreLabel, livesLabel, multiplierLabel, hiScoreLabel);
        mainPane.setRight(infoPane);
        infoPane.setPadding(new Insets(10, 50, 0, 50));
        board.setStyle("-fx-background-color: rgba(255,255,255,0);");
        timerBar = new Rectangle();
        timerBar.setHeight(10);
        timerBarIWidth = gameWindow.getWidth() - 50;
        timerBar.setWidth(timerBarIWidth);
        timerBar.setFill(Color.GREEN);

        var bottomBox = new HBox(timerBar);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10,10,50, 10));
        mainPane.setBottom(bottomBox);

        bgPlayer.playMusic("game.wav");



    }
    /**
     * Creates the animation for the timer bar
     */
    private void timerAnimation(){
        logger.info("running timer Animation");
        timerBar.setScaleX(1);
        timeline = new Timeline();
        timerBar.setFill(Color.GREEN);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(game.delay),
                new KeyValue (timerBar.scaleXProperty(), 0)));
        timeline.playFromStart();
        FillTransition colorChange = new FillTransition();
        colorChange.setDuration(Duration.millis((game.delay)));
        colorChange.setShape(timerBar);
        colorChange.setToValue(Color.RED);
        colorChange.play();
    }


    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }
    /**
     * calls the fadeOut command for the lines that were cleared
     * @param coords coordiantes for the lines that were cleared
     */
    private void lineCleared(Set<GameBlockCoordinate> coords){
        if(game.Score.getValue() > getHighScore()){
            hiScore.set(game.Score.getValue());
        }
        board.fadeOut(coords);
    }
    /**
     * Rotates pieces when gameboard is right clicked
     */
    private void gameboardRightClicked(){
        game.rotateCurrentPiece(game.getCurrentPiece());
        currentBlock.setPiece(game.getCurrentPiece());
        bgPlayer.playAudio("rotate.wav");
    }


    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5, bgPlayer);
        game.setNextPieceListener(nextPieceListener);



    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        timeline = new Timeline();
        endGame();
        game.start();
        currentBlock.setPiece(game.getCurrentPiece());
        followingBlock.setPiece(game.getFollowingPiece());
        timerAnimation();
        initialiseControls();



    }
    /**
     * Initialises all the controls for the challenge scene
     */
    public void initialiseControls(){
        board.setOnMouseMoved((e) -> {
            mouseMode = true;
            currentX = board.currentBlock.getX();
            currentY = board.currentBlock.getY();
        });
        gameWindow.getScene().setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE) {
                logger.info("Button pressed Escape");
                endGame();
                game.stopGame();
                gameWindow.startMenu();
            }
            if(event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
                logger.info("Button pressed W or up arrow");
                if(mouseMode){
                    mouseMode = false;
                    board.mouseExitBlock(board.getBlock(currentX,currentY));
                    currentX = 0;
                    currentY = 0;
                    board.mouseEnterBlock(board.getBlock(currentX,currentY));
                }else{
                    if(currentY != 0){
                        board.mouseExitBlock(board.getBlock(currentX,currentY));
                        currentY -=1;
                        board.mouseEnterBlock(board.getBlock(currentX,currentY));
                    }
                }
            }
            if(event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN) {
                logger.info("Button pressed S");
                if(mouseMode){
                    mouseMode = false;
                    board.mouseExitBlock(board.getBlock(currentX,currentY));
                    currentX = 0;
                    currentY = 0;
                    board.mouseEnterBlock(board.getBlock(currentX,currentY));
                }else{
                    if(currentY != 4){
                        board.mouseExitBlock(board.getBlock(currentX,currentY));
                        currentY +=1;
                        board.mouseEnterBlock(board.getBlock(currentX,currentY));
                    }
                }
            }
            if(event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                logger.info("Button pressed A");
                if(mouseMode){
                    mouseMode = false;
                    board.mouseExitBlock(board.getBlock(currentX,currentY));
                    currentX = 0;
                    currentY = 0;
                    board.mouseEnterBlock(board.getBlock(currentX,currentY));
                }else{
                    if(currentX != 0){
                        board.mouseExitBlock(board.getBlock(currentX,currentY));
                        currentX -=1;
                        board.mouseEnterBlock(board.getBlock(currentX,currentY));
                    }
                }
            }
            if(event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                logger.info("Button pressed D");
                if(mouseMode){
                    mouseMode = false;
                    board.mouseExitBlock(board.getBlock(currentX,currentY));
                    currentX = 0;
                    currentY = 0;
                    board.mouseEnterBlock(board.getBlock(currentX,currentY));
                }else{
                    if(currentX != 4){
                        board.mouseExitBlock(board.getBlock(currentX,currentY));
                        currentX +=1;
                        board.mouseEnterBlock(board.getBlock(currentX,currentY));
                    }
                }
            }
            if(event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.X) {
                logger.info("Button pressed Enter or X");
                game.blockClicked(board.getBlock(currentX, currentY));
            }
            if(event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.R) {
                logger.info("Button pressed SPACE or R");
                game.swapCurrentPiece();
                currentBlock.setPiece(game.getCurrentPiece());
                followingBlock.setPiece(game.getFollowingPiece());
                bgPlayer.playAudio("transition.wav");
            }
            if(event.getCode() == KeyCode.OPEN_BRACKET || event.getCode() == KeyCode.Q || event.getCode() == KeyCode.Z) {
                logger.info("Button pressed [ or Q or Z");
                game.rotateCurrentPiece(game.getCurrentPiece());
                game.rotateCurrentPiece(game.getCurrentPiece());
                game.rotateCurrentPiece(game.getCurrentPiece());
                currentBlock.setPiece(game.getCurrentPiece());
                bgPlayer.playAudio("rotate.wav");
            }
            if(event.getCode() == KeyCode.CLOSE_BRACKET || event.getCode() == KeyCode.E || event.getCode() == KeyCode.C) {
                logger.info("Button pressed ] or E or C");
                game.rotateCurrentPiece(game.getCurrentPiece());
                currentBlock.setPiece(game.getCurrentPiece());
                bgPlayer.playAudio("rotate.wav");
            }
        });
    }
    /**
     * Ends the game by reseting all the bindable properties
     */
    public void endGame(){
        game.Score.set(0);
        game.lives.set(3);
        game.multiplier.set(1);
        game.level.set(0);
    }
    /**
     * calls the scoreScene and stops music and the timer
     */
    public void gameOver(){
        Integer scoreToSend = game.Score.getValue();
        game.stopGame();
        endGame();
        bgPlayer.stopMusic();
        gameWindow.startScores(scoreToSend);
    }
    /**
     * Gets the highscores from the local text file.
     * @return returns the scores
     */
    public Integer getHighScore(){
        Integer toReturn = 0;
        try{
            var scoreFile = Paths.get("localScores.txt");
            List<String> scores = Files.readAllLines(scoreFile);
            if(scores.equals("")){
                return null;
            }
            String[] parts = scores.get(0).split(":");
            toReturn = Integer.parseInt(parts[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return toReturn;
    }
}
