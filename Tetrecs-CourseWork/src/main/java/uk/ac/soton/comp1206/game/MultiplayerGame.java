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
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.Multimedia;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiplayerGame extends Game{
    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
    private Communicator comms;
    private IntegerProperty globalNextPieceFinder = new SimpleIntegerProperty();

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     * @param a the mediaplayer
     * @param c the communicator from lobbyscene
     */
    public MultiplayerGame(int cols, int rows, Multimedia a, Communicator c) {
        super(cols, rows, a);
        this.comms = c;
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
        comms.addListener(this::handleCommunication);
    }
    /**
     * Changes block clicked so that the new board is sent to the server
     */

    public void blockClicked(GameBlock gameBlock) {
        super.blockClicked(gameBlock);
        comms.send("BOARD " + boardToString());
    }

    /**
     * Changes the board state into a string which can be sent to the server in the correct format
     */
    private String boardToString() {
        StringBuilder board = new StringBuilder();

        for (int x = 0; x < cols; ++x) {
            for (int y = 0; y < rows; ++y) {
                int tmp = grid.get(x, y);
                board.append(tmp).append(" ");
            }
        }
        return board.toString().trim();
    }
    /**
     * handles all messages received from the server
     */
    private void handleCommunication(String s) {
        String[] components = s.split(" ", 2);
        String toDo = components[0];
        String content  = (components[1]);
        if (toDo.equals("PIECE")) {
            logger.info(content);
            Platform.runLater(() -> {
                globalNextPieceFinder.set(Integer.parseInt(content));
                logger.info(globalNextPieceFinder);
                spawnPiece();
            });
        }
    }

    /**
     * Overriding spawn piece so that server can be sent a message
     */

    public GamePiece spawnPiece() {
        Random random = new Random();
        int pieceNum = random.nextInt(GamePiece.PIECES);
        int rotation = random.nextInt(4);
        startTimer();
        return GamePiece.createPiece(pieceNum, rotation);
    }
    /**
     * so that the correct piece from the server can be called
     * @return reutrns the new spawned piece from server
     */
    public GamePiece spawnPieceNew(){
        Random random = new Random();
        AtomicInteger pieceNum = new AtomicInteger();
        Platform.runLater(() -> {
            pieceNum.set(globalNextPieceFinder.getValue());
        });
        int rotation = random.nextInt(4);
        startTimer();
        return GamePiece.createPiece(pieceNum.get(), rotation);
    }


    public long getTimerDelay(){
        delay = 12000 - (level.getValue() * 500);
        return delay;
    }
}
