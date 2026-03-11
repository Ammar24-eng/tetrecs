package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.*;

/**
 * Lobby scene which has all the channels and create new channel option before a multiplayer game
 */

public class LobbyScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);

    private Timer timer;

    public ArrayList<String> ChannelList = new ArrayList<>();
    protected static Communicator comms;
    private BorderPane mainPane;

    private VBox ButtonsSection;

    private TextField enterName;
    private boolean doFunction = false;

    private VBox bigBox;
    private String userlist;

    private ScrollPane container = new ScrollPane();
    private VBox messagesBox = new VBox();
    Boolean Host = false;
    private StringProperty channelInfo;
    private boolean inChannel;
    private String currentChannel;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        comms = gameWindow.getCommunicator();
        inChannel = false;
    }
    /**
     * Initialise a new LObby Scene and set up anything that needs to be done at the start
     * Initialise the escape control so that you can exit and sends messages to the server to get a list of the
     * channels
     */
    @Override
    public void initialise() {
        this.scene.setOnKeyPressed((e) -> {
            if (e.getCode().equals(KeyCode.ESCAPE)) {
                gameWindow.startMenu();
            }
        });
        comms.send("LIST");
        comms.addListener(this::handleCommunication);

        resetTimer();
    }
    /**
     * Handles all messages received from the serverl
     * @param s the message received form the server
     */
    private void handleCommunication(String s) {
        String[] components = s.split(" ", 2);
        String toDo = components[0];
        String content  = (components[1]);
        if (toDo.equals("CHANNELS")) {
            Platform.runLater(() -> showChannels(content));
        }
        if(toDo.equals("USERS")){
            Platform.runLater(() -> showUsers(content));
        }
        if(toDo.equals("MSG")){
            Platform.runLater(() -> showMesasage(content));
        }
    }
    /**
     * Puts the received message into the chatBox
     *      * @param content the info received from message
     */
    private void showMesasage(String content) {
        messagesBox.getChildren().add(new Text(content));
        container.setVvalue(container.getVmax());
    }
    /**
     * Shows users connected to the selected channel
     *      * @param content the info received from message
     */
    private void showUsers(String content) {
        String[] users = content.split("\\R");
        userlist = "";
        for(String s: users){
            userlist += (s+ ", ");
        }
        logger.info(userlist);
        if(inChannel){
            logger.info("Userlist refereshed: " + userlist);
            channelInfo.set(currentChannel + " Users: " + userlist);
        }
    }
    /**
     * Shows available channels
     * @param content the info received from message
     */
    private void showChannels(String content){
        logger.info("Refreshing channels");
        logger.info(content);
        if(ChannelList != null) {
            ChannelList.clear();
        }

        var channelBox = new VBox();
        channelBox.setAlignment(Pos.BOTTOM_LEFT);
        logger.info("channels are :" + content);
        String[] channels = content.split("\\R");
        ChannelList.addAll(Arrays.asList(channels));
        mainPane.setBottom(channelBox);
        for(String channel : ChannelList){
            Text channelName = new Text(channel);
            channelName.getStyleClass().add("channelItem");
            channelBox.getChildren().add(channelName);
            channelBox.setAlignment(Pos.TOP_LEFT);
            channelName.setOnMouseClicked((e) -> {
                requestJoin(channel, false);
            });
        }
    }
    /**
     * Requests to join the channel
     * @param channel the channel that's to be requested
     * @param host if user is host allows to start the game
     */
    private void requestJoin(String channel, boolean host) {
        currentChannel = channel;
        messagesBox.getChildren().clear();
        comms.send("JOIN " + channel);
        inChannel = true;
        bigBox = new VBox();
        mainPane.setRight(bigBox);
        Color transparent = new Color(0,0,0,0);
        bigBox.setBackground(new Background(new BackgroundFill(transparent, CornerRadii.EMPTY, Insets.EMPTY)));
        bigBox.setMinSize(gameWindow.getWidth()/1.5, gameWindow.getHeight()/1.5);
        bigBox.setPadding(new Insets(20,20,10,10));
        comms.send("USERS");
        channelInfo = new SimpleStringProperty();
        Label channelInfoLabel = new Label();
        channelInfo.set(channel + " Users: " + userlist);
        channelInfoLabel.textProperty().bind(channelInfo);
        bigBox.getChildren().add(channelInfoLabel);
        container.setPrefSize(216, 400);
        container.setContent(messagesBox);
        messagesBox.getStyleClass().add("chatbox");
        TextField sendText = new TextField();
        Button sendButton = new Button("Send");
        sendButton.setAlignment(Pos.BOTTOM_RIGHT);
        sendButton.setOnAction((e) -> {
            comms.send("MSG " + sendText.getText());
            sendText.clear();
        });
        HBox messageBar = new HBox();
        messageBar.getChildren().addAll(sendText, sendButton);
        HBox buttons = new HBox();
        bigBox.getChildren().addAll(container, messageBar, buttons);
        Button leaveButton = new Button("Leave");
        buttons.getChildren().add(leaveButton);
        buttons.setPadding(new Insets(10,10,10,10));
        leaveButton.setOnAction((e) -> {
            comms.send("PART");
            inChannel = false;
            bigBox.getChildren().clear();
        });
        if(host) {
            Button startButton = new Button("Start");
            buttons.getChildren().add(startButton);
            startButton.setOnAction((e) -> {
                comms.send("START");
                gameWindow.loadScene(new MultiplayerScene(gameWindow));
            });
        }

    }
    /**
     * Refreshs ChannelList and if in a chanel refreshes the userlist
     */
    public void resetTimer(){
        TimerTask refresh =new TimerTask() {
            @Override
            public void run() {
                if(inChannel) {
                    comms.send("LIST");
                }
                comms.send("USERS");
                scene.setOnKeyPressed((e) -> {
                    if (e.getCode().equals(KeyCode.ESCAPE)) {
                        gameWindow.startMenu();
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(refresh, 0, 5000);
    }
    /**
     * Builds the basic screen with the create new lobby button and available servers
     */
    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("challenge-background");
        root.getChildren().add(menuPane);

        mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);
        var newLobby = new Button("New Lobby");
        ButtonsSection = new VBox(newLobby);
        newLobby.getStyleClass().add("buttonItem");
        ButtonsSection.setMinWidth(gameWindow.getWidth()/2);
        newLobby.setMinWidth(200);
        newLobby.setAlignment(Pos.TOP_LEFT);
        newLobby.setOnAction((e) -> {newLobbyFunction();});
        mainPane.getChildren().add(ButtonsSection);
    }
    /**
     * Creates a new lobby and sends the appropriate message to the server
     */
    private void newLobbyFunction() {
        if(!doFunction) {
            enterName = new TextField();
            enterName.setMinWidth(200);
            ButtonsSection.getChildren().add(enterName);
            doFunction = true;
            this.scene.setOnKeyPressed((e) -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    comms.send("CREATE " + enterName.getText());
                    requestJoin(enterName.getText(), true);
                }
            });
        }else{
            doFunction = false;
            ButtonsSection.getChildren().remove(enterName);
        }
    }
}
