package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.Set;
/**
 * Listens for when lines are cleared
 */
public interface LineClearedListener {

    /**
     * Activates when lines are cleared
     * @param coords the coords whcih are cleared
     */
    public void linesCleared(Set<GameBlockCoordinate> coords);
}
