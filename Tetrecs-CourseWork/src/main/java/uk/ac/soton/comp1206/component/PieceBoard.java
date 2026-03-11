package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.scene.InstructionsScene;

import java.util.Arrays;

/**
 * PieceBoard class which extends gameboard where some new functionality is required from gameboard
 * Such as setPiece.
 */

public class PieceBoard extends GameBoard{
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);
    private Boolean pieceBoardBool = true;

    /**
     * Constructor for PieceBoard, same as GameBoard
     * @param grid required to create a gameboard, allows size of board to be determined
     * @param width sets the width of the board
     * @param height sets the height of the board
     *these can be changed later on.
     */

    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);
    }
    /**
     * sets the given piece into the pieceboard
     * @param piece piece to be set
     */
    public void setPiece(GamePiece piece){
        int[][] pieceBlocks = piece.getBlocks();
        logger.info(Arrays.deepToString(pieceBlocks));
        for(int i = 0; i < 3; i++){
            for(int j=0; j<3; j++){
                grid.set(i,j,pieceBlocks[i][j]);
            }
        }
    }
    /**
     * Returns true if it is a pieceboard so that hovering doesnt work on it
     */
    public Boolean isPieceboard(){
        return pieceBoardBool;
    }
    /**
     * Hovering animation incase i want to use it on a pieceboard at somepoint, does nothing atm
     */
    public void mouseEnterBlock(GameBlock block){
        if(!isPieceboard()){
            block.hoverBlock();
        }
    }
}
