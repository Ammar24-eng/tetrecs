package uk.ac.soton.comp1206.component;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    public Boolean center;

    public Boolean isPlacepoint;

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        center = false;
        isPlacepoint = false;

        //fixed width and height
        setWidth(width);
        setHeight(height);



        //initial paint


        paint();




        //call the internal updateValue method


        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value

            if(!center){
                paintColor(COLOURS[value.get()]);
            }else{
                drawCircle(COLOURS[value.get()]);
            }
            if(isPlacepoint){
                var gc = getGraphicsContext2D();
                Light.Distant light = new Light.Distant();
                light.setAzimuth(-135.0);

                Lighting lighting = new Lighting();
                lighting.setLight(light);
                lighting.setSurfaceScale(5.0);
                gc.applyEffect(lighting);
                isPlacepoint = false;
            }

        }
    }

    /**
     * Paint this canvas white transparent so that hover animations can also be seen
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill
        Color blacksparent = new Color(0,0,0,0.5);
        gc.setFill(blacksparent);
        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     * adds some effects to the blocks to make them look nicer
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        gc.setFill(colour);
        gc.fillRect(0,0, width, height);
        Glow glow = new Glow();
        InnerShadow shadow = new InnerShadow();
        shadow.setColor(Color.BLACK);
        glow.setLevel(0.9);
        gc.applyEffect(glow);
        gc.applyEffect(shadow);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    /**
     * draws a circle on the block, this is called for the center piece on the currentblock pieceboard
     * @param colour, sets the colour of the circle
     */

    public void drawCircle(Paint colour){
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        gc.setFill(colour);
        gc.fillRect(0,0, width, height);
        Glow glow = new Glow();
        InnerShadow shadow = new InnerShadow();
        shadow.setColor(Color.BLACK);
        glow.setLevel(0.9);
        gc.applyEffect(glow);
        gc.applyEffect(shadow);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);

        Color blacksparent = new Color(0,0,0,0.5);

        gc.setFill(blacksparent);
        logger.info("x:"+width+" y:" +height);
        gc.fillOval(width/2 - 15, height/2 - 15, 30, 30);
    }


    /**
     * allows for the block to be set to a block that needs a circle to be drawn on it
     */
    public void setCircleBlock(){
        center = true;
    }
    /**
     * changes the effects on the block to give a hovering effect when called.
     */
    public void hoverBlock(){
        paint();
        var gc = getGraphicsContext2D();
        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135.0);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);
        gc.applyEffect(lighting);
    }

    /**
     * When called the current block is set as the point where the block should be placed from.
     */
    public void setPlacepoint(){
        isPlacepoint = true;
    }

    /**
     * Fades the block out from white to transparent again
     */
    public void fadeOut(){

        var timer = new AnimationTimer() {
            private long prevTime = 0;
            double transparent = 0;
            @Override
            public void handle(long l) {
                long dt = l - prevTime;
                var gc = getGraphicsContext2D();
                if(dt>1e8){
                    prevTime=l;
                    gc.clearRect(0,0,width,height);

                    //Fill
                    Color whitesparent = new Color(1,1,1,1-transparent);
                    gc.setFill(whitesparent);
                    gc.fillRect(0,0, width, height);

                    //Border
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(0,0,width,height);
                    transparent+=0.1;
                    if(transparent>=0.9){
                        stop();
                        paintEmpty();
                    }
                }
            }
        };
        timer.start();

    }





}
