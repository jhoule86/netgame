/*
 * File: Player.java
 * Author: jhoule
 * Updated: 12 Jan 2010
 */

package netgame.player;

import netgame.game.ClientMove;
import netgame.game.ServerUpdate;

/**
 * the base class for Players in a networked game.
 * @author jhoule
 */
public abstract class Player
{

    public abstract void submitClientMove(ClientMove aMove);

    public abstract void processUpdateFromServer(ServerUpdate aUpdate);

}
