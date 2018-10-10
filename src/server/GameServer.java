/*
 * File: GameServer.java
 * Author: jhoule
 * Updated: 12 Jan 2010
 */
package netgame.server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;
import java.net.SocketException;
import java.util.HashSet;

/**
 * The base class for servers for a networked game.
 * @author jhoule
 */
public abstract class GameServer
{

    /**
     * The name of this server.
     */
    private String myName = "GameServer";
    
    
    /**
     * the port that the Server communicates on.
     */
    protected int myPort = 0;
    /**
     * the on/off status of the Server.
     */
    protected boolean power = false;
    /**
     * the Listener Object for dealing with cilent connections.
     */
    private Listener myListener = null;
    /**
     * The separate ConnectionHandler Objects for
     * sending and receiving information to and from Clients.
     */
    private HashSet<ServerConnectionHandler> clientThreads = null;

    /**
     * No-arg Constructor to allow for extension. 
     */
    protected GameServer()
    {
        
    }
    
    /**
     * Constructor. Sets port used.
     * @param port - the port for communicating with Clients.
     */
    public GameServer(int port)
    {
        myPort = port;

    }

    /**
     * Constructor. Sets port used and unique name.
     * @param port - the port for communicating with Clients.
     * @param name - a name for the Server to present clients with.
     */
    public GameServer(int port, String name)
    {
        myPort = port;

        myName = name;
    }

    /**
     * Starts the server
     */
    private void start()
    {
        System.out.println(myName + "is starting up on port " + myPort + ".");

        clientThreads = new HashSet<ServerConnectionHandler>();

        myListener = new Listener();

        power = true;
        myListener.start();
    }


    /**
     * The thread that listens on the specified port and accepts new connections.
     * For each connection, a new Server.ConnectionHandler thread is created.
     *
     * @see ServerSocket
     * @see ConnectionHandler
     */
    private class Listener extends Thread
    {

        /**
         * Constructor
         */
        private Listener()
        {
        }

        /**
         * Infinite loop: accept a new connection, create a handler thread. 
         */
        @Override
        public void run()
        {
            // if running, should be on.
            power = true;


            Socket cSock = null;
            ServerSocket sSock = null;

            try
            {
                try
                {
                    sSock = new ServerSocket(myPort);
                } catch (BindException ex1)
                {
                    System.err.println("Cannot connect with this port: "
                            + myPort + "\n" + ex1.getMessage());
                }
            } catch (IOException ex)
            {
                StringBuilder builder = new StringBuilder();
                builder.append("Could not create server at port ");
                builder.append(myPort);
                builder.append(".");
                String eString = builder.toString();
                System.err.println(eString);
                Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, eString, ex);

                power = false;
            }

            while (power)
            {
                try
                {
                    cSock = sSock.accept();

                    new ServerConnectionHandler(cSock).start();

                } catch (IOException ex)
                {
                    Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, "socket issue on join", ex);
                    power = false;
                }
            }
        }
    }

    /**
     * A thread that handles the connection with one client. This thread
     * implements the server side of the protocol described in the discussion
     * section of chat. Communication between this thread and the corresponding
     * client is achieved through the provided socket.
     */
    protected class ServerConnectionHandler extends Thread
    {

//        private ConcurrentLinkedQueue<Message> messages;
        private Socket mySocket;
        private String uName;

        /**
         * Create a new thread to handle a client through the provided socket.
         * @param s - the socket used for bidirectional communication with the client
         */
        ServerConnectionHandler(Socket s)
        {
            mySocket = s;
//            messages = new ConcurrentLinkedQueue<Message>();

            try
            {
                mySocket.setKeepAlive(true);
                // to keep from blocking.
                mySocket.setSoTimeout(100);


            } catch (SocketException ex)
            {
                Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, "ERROR: socket operations failed.", ex);
            }
        }
//
//        /**
//         * The behavior of the server with respect to one client. This involves
//         * getting the initial JOIN message, checking the validity of the alias,
//         * forward messages from this client to other clients,
//         * forward messages from other clients to this client,
//         * notify other clients of the arrival and departure of this client,
//         * notify this client of the arrival and departure of other clients.
//         */
//        @Override
//        public void run()
//        {
//            BufferedReader in = null;
//            PrintWriter out = null;
//
//            Message mess = null, err = null;
//            boolean go = true;
//
//            // set up IO
//            try
//            {
//                in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
//                out = new PrintWriter(
//                        new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream())), true);
//            } catch (IOException ex)
//            {
//                go = false;
//                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "ERROR: problems setting up readers and writers for Server.", ex);
//            }
//
//            // parse the message
//            if (go)
//            {
//                try
//                {
//                    try
//                    {
//                        mess = Message.parse(in.readLine());
//                       
//                    } catch (IOException ex)
//                    {
//                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "could not read in message.", ex);
//                        go = false;
//                    }
//                } catch (Exception ex)
//                {
//                    go = false;
//                    err = new Message(Type.INFO, NAME, "bad message format");
//                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "ERROR: problem reading message.", ex);
//                }
//
//            }
//
//            // make sure this is a join message
//            if (go && mess != null && !mess.type.equals(Type.JOIN))
//            {
//                // first message was not join.
//                err = new Message(Type.INFO, NAME, "need to JOIN first.");
//
//
//                go = false;
//            }
//
//            // get the source, if should keep going.
//            if (go)
//            {
//                uName = mess.source;
//            }
//
//            // make sure source is not same name as server.
//            if (go && uName.equals(NAME))
//            {
//                err = new Message(Type.INFO, NAME, "Cannot join: Your username cannot be equal to mine.");
//                go = false;
//
//            }
//
//            // make sure server doesn't already have this alias used.
//            if (go && isServing(uName))
//            {
//                err = new Message(Type.INFO, NAME, "Cannot join: Someone with your alias already has connected");
//                go = false;
//            }
//            InetAddress addr;
//
//
//            // add the connection handler to the set in the server.
//            // and tell other users about it.
//            if (go)
//            {
//                addUser(this, mess);
//
//                addr = mySocket.getLocalAddress();
//
//                System.out.println("New user " + uName + " connected from " + addr.getHostAddress());
//            } else
//            {
//                if (err != null)
//                {
//                    out.println(err.toString());
//                }
//            }
//
//            // start the loop of getting messages and sending messages out.
//            while (!isInterrupted() && go)
//            {
//
//                // make sure socket didn't close.
//                if (mySocket.isClosed() || !mySocket.isConnected())
//                {
//
//                    go = false;
//                    sayGoodbye(this);
//
//                    try
//                    {
//                        in.close();
//                        out.close();
//                    } catch (IOException ex)
//                    {
//                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "trouble closing streams", ex);
//                    }
//                } else
//                {
//                    // get messages
//                    String msgStr = null;
//                    try
//                    {
//                        try
//                        {
//                            msgStr = in.readLine();
//                            if (msgStr == null)
//                            {
//                                go = false;
//                            }
//                        } catch (SocketTimeoutException ex1)
//                        {
//                            // no messages
//                            go = true;
//                        }
//
//                    } catch (IOException ex)
//                    {
//                        go = false;
//                        msgStr = null;
//                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "could not read in line", ex);
//                    }
//
//                    while (msgStr != null)
//                    {
//                        try
//                        {
//                            mess = Message.parse(msgStr);
//
//                            if (mess.type.equals(Message.Type.MESSAGE))
//                            {
//                                sendAllUsers(mess);
//                            } else
//                            {
//                                mess = null;
//                                mess = new Message(Message.Type.INFO, Server.NAME, "Message is of wrong type.");
//                            }
//
//                        } catch (Exception ex)
//                        {
//
//                            mess = new Message(Message.Type.INFO, Server.NAME, "Message is formatted incorrectly.");
//                            out.print(mess.toString());
//                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "ERROR: trouble parsing message.", ex);
//
//                        }
//
//                        msgStr = null;
//                        try
//                        {
//                            try
//                            {
//                                msgStr = in.readLine();
//                                if (msgStr == null)
//                                {
//                                    go = false;
//                                }
//                            } catch (SocketTimeoutException ex1)
//                            {
//                                // no messages
//                                go = true;
//                            }
//
//                        } catch (IOException ex)
//                        {
//                            go = false;
//                            msgStr = null;
//                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "could not read in line", ex);
//                        }
//                    }
//
//                    // send messages to client.
//
//                    if (go && ! messages.isEmpty())
//                    {
//                        Iterator<Message> it = messages.iterator();
//
//                        while (it.hasNext())
//                        {
//                            Message cur = it.next();
//
//                            out.println(cur.toString());
//
//                        }
//                        messages.clear();
//                    }
//                }
//            }
//
//            if (!mySocket.isClosed())
//            {
//
//                sayGoodbye(this);
//                try
//                {
//                    in.close();
//                } catch (IOException ex)
//                {
//                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Error: trouble closign input stream.", ex);
//                }
//                out.close();
//
//            }
//        }
//
//        /**
//         * Adds the message to the queue of messages to send to the associated
//         * client.
//         * @param m - the message to add to be sent.
//         */
//        private void sendMessage(Message m)
//        {
//            messages.add(m);
//        }
    }
    // TODO: base this off of the Chat server.
}
