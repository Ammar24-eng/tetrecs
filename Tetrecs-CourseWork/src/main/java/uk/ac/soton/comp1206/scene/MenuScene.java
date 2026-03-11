package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.File;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private Multimedia bgMusic;

    private ImageView imageView;

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
        bgMusic = new Multimedia();
    }
    /**
     * Animates the title
     */
    public void titleAnimation(){
        RotateTransition animate = new RotateTransition();
        animate.setNode(imageView);
        animate.setFromAngle(-25);
        animate.setToAngle(25);
        animate.setDuration(Duration.seconds(1.5));
        animate.setAutoReverse(true);
        animate.setCycleCount(Animation.INDEFINITE);
        animate.play();
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);


        var title = new Image(MenuScene.class.getResource("/images/TetrECS.png").toExternalForm());
        imageView = new ImageView(title);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(100);
        mainPane.setCenter(imageView);


        var singlePlayer = new Button("Single player");
        singlePlayer.getStyleClass().add("menuItem");
        var multiPlayer = new Button("Multiplayer");
        multiPlayer.getStyleClass().add("menuItem");
        var howTo = new Button("Instructions");
        howTo.getStyleClass().add("menuItem");
        var exit = new Button("Exit");
        exit.getStyleClass().add("menuItem");
        var menuButtons = new VBox(10, singlePlayer, multiPlayer, howTo, exit);
        menuButtons.setAlignment(Pos.CENTER);
        menuButtons.getStyleClass().add("menu");
        mainPane.setBottom(menuButtons);


        singlePlayer.setOnAction(this::startGame);
        howTo.setOnAction(this::helpScreen);
        exit.setOnAction(this::quit);
        multiPlayer.setOnAction(this::startMulti);

        bgMusic.playMusic("menu.mp3");
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        titleAnimation();
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }
    /**
     * Handle when the instructions button is pressed
     * @param event event
     */
    private void helpScreen(ActionEvent event){
        gameWindow.startHelp();
    }
    /**
     * Handle when the multiplayer button is pressed
     * @param event event
     */
    private void startMulti(ActionEvent event){
        gameWindow.startMulti();
    }
    /**
     * Handle when the quit button is pressed
     * @param event event
     */
    private void quit(ActionEvent event){
        Platform.exit();
        System.exit(0);
    }

}
