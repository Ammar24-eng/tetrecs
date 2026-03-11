package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;
    private static final Logger logger = LogManager.getLogger(Grid.class);

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Generate the Grid
        grid = new SimpleIntegerProperty[cols][rows];

        //apply  SimpleIntegerProperty to every block in the grid



        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
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
     * Checks if piece can be played in the given position
     * @param theP the piece to check
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public boolean canPlayPiece(GamePiece theP, int x, int y){
        int[][] shape = theP.getBlocks();
        int startX = x - 1;
        int startY = y - 1;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {  // if the piece has a block at this position
                    if (startX + i < 0 || startX + i >= getCols() || startY + j  < 0 || startY + j >= getRows()) {
                        return false;  // position is outside the grid, can't play here
                    }
                    if (get(startX + i, startY + j) != 0) {
                        return false;  // position is already occupied, can't play here
                    }
                }
            }
        }

        return true;

        // all positions are valid  can play here
    }

    /**
     * Plays the piece on the given x and y coordinates on the grid
     * @param piece the piece to be played
     * @param centerY the y coordinate on the grid where the piece needs to be played
     * @param centerX the x coordinate on the grid
     */
    public void playPiece(GamePiece piece, int centerX, int centerY) {
        int[][] shape = piece.getBlocks();

        int startX = centerX - 1;
        int startY = centerY - 1;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                int value = shape[i][j];
                if (value != 0) {  // if the piece has a block at this position
                    set(startX+i, startY+j, value);
                    logger.info("grid coordinate");
                }
            }
        }
    }

}
