/*
 * File: PlayerSeat.java
 * Author: jhoule
 * Updated: 12 Jan 2010
 */


package netgame.table;

import netgame.player.Player;

/**
 * A Seat at a PlayingTable for a Player to use for game playing.
 * @author jhoule
 */
public class Seat {

    /**
     * The Player that may or may not be seated in the Seat.
     */
    private Player myPlayer;
    
    /**
     * Returns the vacancy status of the seat.
     * @return true iff there isn't a player in the seat, false otherwise.
     */
    public boolean vacant()
    {
        return (myPlayer == null);
    }
    
    /**
     * Returns the Player sittng in the Seat.
     * @return the Player, or null if there is none.
     */
    public Player getPlayer()
    {
        if (vacant())
        {
            return null;
        }
        
        return myPlayer;
    }
    
    
}
