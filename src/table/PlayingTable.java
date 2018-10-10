/*
 * File: PlayingTable.java
 * Author: jhoule
 * Updated: 12 Jan 2010
 */


package netgame.table;

import fuzzydice.dice.list.SizeFixedSelectionList;

/**
 * A table with Seats for Players to occupy as they play games.
 * @author jhoule
 */
public class PlayingTable {
    
    /**
     * The Set of seats at the table.
     */
    private SizeFixedSelectionList<Seat> mySeats;

    /**
     * The amount of seats at the table.
     * @return the size of the table.
     */
    public int size()
    {
        return mySeats.size();
    }
    
    /**
     * Attempts to locate a vacant seat at the table.
     * Iff a vancacy is found, returns that Seat.
     * Iff no seats are available, returns null.
     * @return a vacant seat, iff one is found, null otherwise.
     */
    public Seat getVacant()
    {
        Seat s = null;
        for (int i = 0; i < mySeats.size(); i++)
        {
            if (mySeats.get(i).vacant())
                s = mySeats.get(i);
        }
        
        return s;
    }
    
}
