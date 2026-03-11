package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;
/**
 * Create a Listener which listens for when the next piece is played
 */
public interface NextPieceListener {

    /**
     * nextPiece function which takes two parameters
     * @param currentPiece passes the currentPiece so that currentBlock can be updated
     * @param nextPiece passes the nextPiece GamePiece so followingBlock can be updated
     */
    void nextPiece(GamePiece currentPiece, GamePiece nextPiece);

}
