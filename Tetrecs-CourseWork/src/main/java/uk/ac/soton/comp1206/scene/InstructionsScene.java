package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
/**
 * Instructions scene with dynamically generated blocks and controls
 */
public class InstructionsScene extends BaseScene{
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
    private Multimedia bgMusic;

    /**
     * Constructs the instructions seen
     * @param gameWindow passes the window so same window is used
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
        bgMusic = new Multimedia();
    }
    /**
     * Initialise a new instructions scene and set up anything that needs to be done at the start
     */
    public void initialise() {
        gameWindow.getScene().setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE) {
                logger.info("Button pressed Escape");
                gameWindow.startMenu();
            }
        });

    }
    /**
     * Builds all the objects in the instructions scene such as gameblocks and image
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("help-background");
        root.getChildren().add(menuPane);
        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        var instructions = new Image(MenuScene.class.getResource("/images/Instructions.png").toExternalForm());
        var imageView = new ImageView(instructions);
        var imageBox = new HBox(imageView);
        var imageBoxH = new VBox(imageBox);
        imageBox.setAlignment(Pos.CENTER);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(gameWindow.getHeight()/2);
        mainPane.setCenter(imageBoxH);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        var pieces = new GridPane();
        var piecesBox = new HBox(pieces);
        var piecesVBox = new VBox(piecesBox);
        pieces.setHgap(10);
        pieces.setVgap(10);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(0));
        }}, 0, 0, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(1));
        }}, 1, 0, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(2));
        }}, 2, 0, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(3));
        }}, 3, 0, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(4));
        }}, 4, 0, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(5));
        }}, 0, 1, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(6));
        }}, 1, 1, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(7));
        }}, 2, 1, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(8));
        }}, 3, 1, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(9));
        }}, 4, 1, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(10));
        }}, 0, 2, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(11));
        }}, 1, 2, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(12));
        }}, 2, 2, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(13));
        }}, 3, 2, 1, 1);
        pieces.add(new PieceBoard(new Grid(3, 3), gameWindow.getHeight()/15, gameWindow.getHeight()/15) {{
            setPiece(GamePiece.createPiece(14));
        }}, 4, 2, 1, 1);

        Label title = new Label("Instructions");
        var titleBox = new HBox(title);
        Label piecesTitle = new Label("Game Pieces");
        piecesTitle.setAlignment(Pos.CENTER);
        piecesTitle.getStyleClass().add("heading");
        var PiecesHeading = new HBox(piecesTitle);
        PiecesHeading.setAlignment(Pos.CENTER);
        piecesVBox.getChildren().add(0, PiecesHeading);
        titleBox.setAlignment(Pos.CENTER);
        title.getStyleClass().add("heading");
        piecesBox.setAlignment(Pos.CENTER);
        piecesBox.setPadding(new Insets(10, 10, 10 ,10));
        mainPane.setTop(titleBox);
        mainPane.setBottom(piecesVBox);
    }
}
