package uk.ac.soton.comp1206.component;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;
import uk.ac.soton.comp1206.scene.ScoresScene;

import java.util.ArrayList;

/**
 * ScoreList custom UI component which extends VBox since it will show the scores in a vertical manner.
 * Allows me to add it easily in my ScoresScene
 */

public class ScoresList extends VBox{

    private SimpleListProperty<Pair<String,Integer>> scores = new SimpleListProperty<>();;
    StringProperty name = new SimpleStringProperty();

    ArrayList<HBox> scoresToAnimate = new ArrayList<>();


    /**
     * ScoresList constructor which just sets the listeners for the scores and name variables which
     * allow it to update when any changes are made to the binded variables
     */
    public ScoresList(){
        scores.addListener((InvalidationListener) (c) -> this.updateList());
        name.addListener((e) -> this.updateList());
    }

    /**
     * creates the reveal animation for each person on the leaderboard one by one not all together
     */
    public void reveal(){
        ArrayList<Transition> transitions = new ArrayList<>();
        for (HBox scoreBox : scoresToAnimate) {
            FadeTransition fade = new FadeTransition(new Duration(1000), scoreBox);
            fade.setFromValue(0);
            fade.setToValue(1);
            transitions.add(fade);
        }

        SequentialTransition transition =
                new SequentialTransition(transitions.toArray(Animation[]::new));
        transition.play();
    }

    /**
     * Updates the UI with new scores and calls reveal animation on them
     */
    public void updateList() {
        int count = 0;
        scoresToAnimate.clear();
        getChildren().clear();
        for (Pair<String, Integer> stringIntegerPair : scores) {
            if(count > 10){
                break;
            }
            HBox scoreLine = new HBox();
            scoreLine.setOpacity(0);
            scoreLine.getStyleClass().add("scoreitem");
            scoreLine.setAlignment(Pos.CENTER);

            Text playerName = new Text(stringIntegerPair.getKey());
            playerName.getStyleClass().add("scoreitem");
            playerName.setTextAlignment(TextAlignment.CENTER);
            HBox.setHgrow(playerName, Priority.ALWAYS);

            Text playerScore = new Text((stringIntegerPair.getValue()).toString());
            playerScore.getStyleClass().add("points");
            playerScore.setTextAlignment(TextAlignment.CENTER);
            HBox.setHgrow(playerScore, Priority.ALWAYS);

            scoreLine.getChildren().addAll(playerName, playerScore);
            scoresToAnimate.add(scoreLine);

            count++;
        }
        for(HBox boxy : scoresToAnimate){
            getChildren().add(boxy);
        }
        reveal();
    }

    /**
     * returns scores for binding
     * @return  returns the scores
     */
    public ListProperty<Pair<String, Integer>> returnScores() {
        return scores;
    }
    /**
     * returns names for binding
     * @return returns the names
     */
    public StringProperty returnNames() {
        return name;
    }

}
