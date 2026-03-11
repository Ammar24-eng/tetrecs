package uk.ac.soton.comp1206.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.event.GameEndListener;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.scene.Multimedia;
import uk.ac.soton.comp1206.event.NextPieceListener;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    GamePiece currentPiece;

    // Bindable properties for game state
    public IntegerProperty Score;
    public IntegerProperty level;
    public IntegerProperty lives;
    public IntegerProperty multiplier;
    public NextPieceListener nextPieceListener;
    public GamePiece followingPiece;
    private GameBoard board;
    Multimedia gameSounds;

    public LineClearedListener lineClearedListener;
    public long delay;
    public Timeline timer;

    GameLoopListener gameLoopListener;

    GameEndListener gameEndListener;



    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     * @param a the media player
     */
    public Game(int cols, int rows, Multimedia a) {
        gameSounds = a;
        this.cols = cols;
        this.rows = rows;
        Score = new SimpleIntegerProperty(0);
        level = new SimpleIntegerProperty(0);
        lives = new SimpleIntegerProperty(3);
        multiplier = new SimpleIntegerProperty(1);

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        delay = getTimerDelay();
        timer = new Timeline(new KeyFrame(Duration.millis(delay), event -> gameLoop()));
        timer.setCycleCount(Timeline.INDEFINITE);
        startTimer();
        currentPiece = spawnPiece();
        followingPiece = spawnPiece();
        nextPieceListener.nextPiece(spawnPiece(), followingPiece);
    }
    /**
     * Starts the timer and resets gameloop
     */
    public void startTimer(){
        delay = getTimerDelay();
        gameLoopListener.setOnGameLoop();
        timer.stop();
        timer = new Timeline(new KeyFrame(Duration.millis(delay), event -> gameLoop()));
        timer.getKeyFrames().setAll(new KeyFrame(Duration.millis(delay), event -> {
            gameLoopListener.setOnGameLoop();
            gameLoop();
        }));
        timer.play();
    }
    /**
     * Initialises gameloop which reduces lives and resets pieces
     */
    public void gameLoop(){
        logger.info("running game loop");
        Platform.runLater(() -> {
            lives.set(lives.getValue()-1);
            gameSounds.playAudio("lifelose.wav");
            logger.info("changing current piece");
            currentPiece = spawnPiece();
            followingPiece = spawnPiece();
            nextPieceListener.nextPiece(currentPiece, followingPiece);
            multiplier.set(1);
            if(lives.getValue() < 0){
                gameEndListener.setOnGameEnd();
            }
        });

        startTimer();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        logger.info(x + "," + y);


        // Update the new value at coordinates (x, y) in the grid
        // grid.set(x, y, newValue);


        if (grid.canPlayPiece(currentPiece, x, y)) {
            logger.info("piece played");
            grid.playPiece(currentPiece, x, y);
            nextPiece();
            gameSounds.playAudio("place.wav");
            afterPiece();

        }else{
            gameSounds.playAudio("fail.wav");
        }
    }

    public void gameboardRightClicked(GameBoard board){
        currentPiece.rotate();

    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Spawns a new random piece
     * @return returns the new piece with the rotation
     */

    public GamePiece spawnPiece() {
        Random random = new Random();
        int pieceNum = random.nextInt(GamePiece.PIECES);
        int rotation = random.nextInt(4);
        startTimer();
        return GamePiece.createPiece(pieceNum, rotation);
    }
    /**
     * Gets the next piece and calls the next piece listener
     */
    public void nextPiece(){
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }
    /**
     * after a piece has been played it checks to see if anything needs clearing and calls score() to adjust score
     */
    public void afterPiece() {
        logger.info("Removing full lines (afterPiece)");
        Set<GameBlockCoordinate> coordinates = new HashSet<>();
        int numRowsCleared=0;
        int numColsCleared=0;
        HashSet<Integer> rowsToClear = new HashSet<>();
        for (int y = 0; y < rows; y++) {
            boolean isFull = true;
            for (int x = 0; x < cols; x++) {
                if (grid.get(x, y) == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                rowsToClear.add(y);
                numRowsCleared++;
            }
        }
        HashSet<Integer> colsToClear = new HashSet<>();
        for (int x = 0; x < cols; x++) {
            boolean isFull = true;
            for (int y = 0; y < rows; y++) {
                if (grid.get(x, y) == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                colsToClear.add(x);
                numColsCleared++;
            }
        }
        for (int y : rowsToClear) {
            for (int x = 0; x < cols; x++) {
                coordinates.add(new GameBlockCoordinate(x,y));
                grid.set(x, y, 0);
            }
        }
        for (int x : colsToClear) {
            for (int y = 0; y < rows; y++) {
                coordinates.add(new GameBlockCoordinate(x,y));
                grid.set(x, y, 0);
            }
        }
        int numLinesCleared = numColsCleared+numRowsCleared;
        logger.info("Cleared {} lines", numLinesCleared);
        int blocksCleared = (numLinesCleared * rows) - (numRowsCleared * numColsCleared);
        logger.info("Calculating blocks overlap and passing to score method");
        score(numLinesCleared, blocksCleared);
        logger.info("adjusting Multiplier");
        if(numLinesCleared > 0){
            multiplier.set(multiplier.getValue()+1);
            logger.info(multiplier.getValue());
            gameSounds.playAudio("clear.wav");
            lineClearedListener.linesCleared(coordinates);
        }
        if(numLinesCleared == 0){
            multiplier.set(1);
        }
    }
    /**
     * Readjusts score and level based on new score
     * @param lines takes lines cleared for formula for score
     * @param blocks takes amount of blocks cleared for formula for score
     */
    public void score(int lines, int blocks){
        int toAdd = lines * blocks * 10 * multiplier.getValue();
        Score.set(Score.getValue() + toAdd);
        int oldLevel = level.getValue();
        logger.info("Old Level: " + oldLevel);
        level.set(Score.getValue() / 1000);
        logger.info("New Level:" + level.getValue());
        if(level.getValue() > oldLevel){
            gameSounds.playAudio("level.wav");
            logger.info("playing level.wav");
        }
    }
    /**
     * sets the nextPieceListener
     * @param npl listener from other class
     */
    public void setNextPieceListener(NextPieceListener npl){
        this.nextPieceListener = npl;
    }
    /**
     * returns the currentPiece that can be played
     * @return the curernt piece
     */
    public GamePiece getCurrentPiece(){
        return currentPiece;
    }

    /**
     * Rotates the current piece
     * @param piece piece to be rotated
     */
    public void rotateCurrentPiece(GamePiece piece){
        piece.rotate();
    }
    /**
     * Swaps the current piece and the following piece
     */
    public void swapCurrentPiece(){
        GamePiece temp = followingPiece;
        followingPiece = currentPiece;
        currentPiece = temp;
    }

    /**
     * returns the piece after the curernt piece / the following piece
     * @return returns the next piece
     */
    public GamePiece getFollowingPiece() {
        return followingPiece;
    }
    /**
     * Sets the linecleared listener when a line is cleared
     * @param listener from another class so it activates
     */
    public void setOnLineCleared(LineClearedListener listener){
        lineClearedListener = listener;
    }
    /**
     * gets the new delay depending on level
     * @return returns the delay
     */
    public long getTimerDelay(){
        delay = 12000 - (level.getValue() * 500);
        return delay;
    }
    /**
     * activates gameloop listener
     * @param listener the listener from another class
     */
    public void setOnGameLoop(GameLoopListener listener) {
        gameLoopListener = listener;
    }
    /**
     * activates GameEnd listener
     * @param listener the listener from another class
     */
    public void setOnGameEnd(GameEndListener listener){
        gameEndListener = listener;
    }
    /**
     * Stops the timer on the game, which will also stop gameloop from being called etc
     */
    public void stopGame(){
        timer.stop();
    }
}
