/*
 * File: GameClient.java
 * Author: jhoule
 * Updated: 12 Jan 2010
 */
package netgame.client;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for clients for a networked game.
 * @author jhoule
 */
public class GameClient
{

    /**
     * The status of a disconnected client.
     */
    public final static int STATUS_DISCONNECTED = 0;
    public final static int STATUS_CONNECTED = 1;
    public final static int STATUS_QUEUED = 3;
    public final static int STATUS_SEATED = 9;
    public final static int STATUS_PLAYING = 25;
    /**
     * the name of this client.
     */
    protected String myName = "GameClient";
    /**
     * the port that the client uses to communicate with the Server.
     */
    protected int myPort = 0;
    /**
     * The connection handler for the client to use for connecting with the server.
     */
    private ClientConnectionHandler myHandler;
    /**
     * The reader for getting input from the server.
     */
    private BufferedReader input;
    /**
     * The writer for sending info to the server.
     */
    private BufferedWriter output;
    /**
     * the status code (connected, playing or queued, etc)
     */
    private int myStatus;
    /**
     * The Socket used for communicating with the Server.
     */
    private Socket mySocket;
    /**
     * The name of the Server that the client is connected to, if any.
     */
    private String serverName;

    /**
     * Returns the connection status of the client.
     * @return true iff the client is connected to a server, false otherwise.
     */
    public boolean connected()
    {
        return ((myStatus & 1) != 0);
    }

    /**
     * gets the name of the server that the client is conversing with.
     * returns null when the client is disconnected.
     * @return the Server's name, null if none.
     */
    public String getServer()
    {
        if (connected())
        {
            return serverName;
        }

        return null;
    }

    /**
     * Thread handling the connection with the server. This thread reads incomingm
     * messages from the server and forwards them to the user interface.
     */
    protected class ClientConnectionHandler
            extends Thread
    {

//        /**
//         * A message to use for in/out.
//         */
//        private Message mess;
        /**
         * Constructor
         */
        protected ClientConnectionHandler()
        {
            super("Cli_Hand");
//            mess = null;
        }

        /**
         * Returns state of socket
         * @return true iff the socket isn't connected, false if still connected.
         */
        protected boolean socketClosed()
        {
            return (mySocket.isClosed() || !mySocket.isConnected());
        }

//        /**
//         * Reads a message in from the server, sends it to the UI.
//         * Depending on the message type, creates more messages to send to UI
//         * for friendly notifications
//         * 
//         * @return true if socket still connected after read attempt
//         */
//        protected boolean readMsg()
//        {
//            if (!socketClosed())
//            {
//                // get messages
//                String msgStr = null;
//                try
//                {
//                    try
//                    {
//                        msgStr = in.readLine();
//                    } catch (SocketTimeoutException exs)
//                    {
//                        // no messages, but still connected.
//                        return true;
//                    }
//                    if (msgStr == null)
//                    {
//                        // disconnected.
//                        myUI.display(new Message(Message.Type.INFO, NAME, "you have been disconnected from the server."));
//                        return false;
//                    }
//
//                } catch (IOException ex)
//                {
//                    msgStr = null;
//
//                    return false;
//                }
//
//                try
//                {
//                    mess = Message.parse(msgStr);
//                    Message.Type t = mess.type;
//
//                    // special handling for control messages.
//                    if (mess.isControl())
//                    {
//                        StringBuilder builder = new StringBuilder();
//
//                        // joins and leaves should have friendly info about this sent.
//                        if (t.equals(Message.Type.JOIN))
//                        {
//
//                            String csv = mess.message;
//                            String[] names = csv.split(String.valueOf(','));
//                            String n;
//
//                            if (csv.contains(","))
//                            {
//                                n = csv.substring(csv.lastIndexOf(',') + 1, csv.length());
//                            } else
//                            {
//                                n = names[0];
//                            }
//
//                            if (newServer)
//                            {
//                                builder.append("welcome, " + n + "!");
//
//                                newServer = false;
//                            } else
//                            {
//                                builder.append(n + " has joined us!");
//                            }
//
//                        } else
//                        {
//                            if (t.equals(Message.Type.LEAVE))
//                            {
//                                builder.append(mess.source);
//                                builder.append(" has left us.");
//                            }
//                        }
//
//                        String s = builder.toString();
//
//                        if (!s.isEmpty())
//                        {
//                            myUI.display(new Message(Message.Type.INFO, Server.NAME, builder.toString()));
//                        }
//                    }
//
//
//                    myUI.display(mess);
//
//                } catch (Exception ex)
//                {
//                    mess = new Message(Message.Type.INFO, NAME, "Message is formatted incorrectly.");
//                    myUI.display(mess);
//                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "message did not format correctly", ex);
//                }
//            }
//            return true;
//        }
        /**
         * Infinite loop: read a message from the server, forward it to the user
         * interface.
         *
         */
        @Override
        public void run()
        {
            try
            {
                mySocket.setSoTimeout(500);
            } catch (SocketException ex)
            {
                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, "could not set timeout", ex);
            }
            while (connected() && !interrupted())
            {
                // TODO: do reaction to server or whatever,
                // and figure out if we need to stay connected.
//                connected = readMsg();
            }


            if (output != null)
            {
                try
                {
                    output.close();
                } catch (IOException ex)
                {
                    Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, 
                            "ERROR: trouble closing streams.", ex);
                }
            }

            if (mySocket != null)
            {
                try
                {
                    mySocket.close();
                } catch (IOException ex)
                {
                    Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, 
                            "ERROR: trouble closing socket.", ex);
                }
            }

        }
    }
    // TODO: base this off of the Chat Client.
}
