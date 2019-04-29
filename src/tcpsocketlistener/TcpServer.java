package tcpsocketlistener;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpServer
{
    private static final Logger logger = Logger.getLogger(TcpServer.class.getName());
    private final String hostname = null;
    private final int myPort = 30303;
    private ServerSocket myServerSocket;
    private Set<Socket> openConnections = new HashSet<Socket>();
    private Thread myThread;
    private long requestCount;

    /**
     * Start the server.
     *
     * @throws IOException
     *             if the socket is in use.
     */
    public void start() throws IOException
    {
        logger.finer("ENTRY");

        myServerSocket = new ServerSocket();
        myServerSocket
                .bind((hostname != null) ? new InetSocketAddress(hostname, myPort) : new InetSocketAddress(myPort));

        myThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("myThread.run()");
                do
                {
                    try
                    {
                        final Socket finalAccept = myServerSocket.accept();
                        registerConnection(finalAccept);
                        final InputStream inputStream = finalAccept.getInputStream();
                        exec(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                OutputStream outputStream = null;
                                try
                                {
                                    // outputStream =
                                    // finalAccept.getOutputStream();
                                    TcpSession session = new TcpSession(inputStream, outputStream,
                                            finalAccept.getInetAddress());
                                    while (!finalAccept.isClosed())
                                        session.execute();
                                }
                                catch (Exception e)
                                {
                                    // When the socket is closed by the client,
                                    // we throw our own SocketException
                                    // to break the "keep alive" loop above.
                                    if (!(e instanceof SocketException && "TcpSession Shutdown".equals(e.getMessage())))
                                        e.printStackTrace();
                                }
                                finally
                                {
                                    safeClose(outputStream);
                                    safeClose(inputStream);
                                    safeClose(finalAccept);
                                    unRegisterConnection(finalAccept);
                                }
                            }
                        });
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                } while (!myServerSocket.isClosed());

                System.out.println("myThread is done");
            }
        });
        myThread.setDaemon(true);
        myThread.setName("TcpServer Main Listener");

        logger.info("Starting TcpServer Main Listener");
        myThread.start();

        logger.finer("EXIT");
    }

    /**
     * Stop the server.
     */
    public void stop()
    {
        logger.finer("ENTRY");

        try
        {
            safeClose(myServerSocket);
            closeAllConnections();
            if (myThread != null) myThread.join();
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Exceptiion stopping thread", ex);
        }
        logger.finer("EXIT");
    }

    public void exec(Runnable code)
    {
        requestCount++;
        Thread t = new Thread(code);
        t.setDaemon(true);
        t.setName("TcpSession #" + requestCount);
        logger.info("Starting TcpSession #" + requestCount);
        t.start();
    }

    /**
     * Registers that a new connection has been set up.
     *
     * @param socket
     *            the {@link Socket} for the connection.
     */
    public synchronized void registerConnection(Socket socket)
    {
        openConnections.add(socket);
    }

    /**
     * Registers that a connection has been closed
     *
     * @param socket
     *            the {@link Socket} for the connection.
     */
    public synchronized void unRegisterConnection(Socket socket)
    {
        openConnections.remove(socket);
    }

    /**
     * Forcibly closes all connections that are open.
     */
    public synchronized void closeAllConnections()
    {
        for (Socket socket : openConnections)
            safeClose(socket);
    }

    private static final void safeClose(Closeable closeable)
    {
        if (closeable != null) try
        {
            closeable.close();
        }
        catch (IOException e)
        {
        }
    }

    private static final void safeClose(Socket closeable)
    {
        if (closeable != null) try
        {
            closeable.close();
        }
        catch (IOException e)
        {
        }
    }

    private static final void safeClose(ServerSocket closeable)
    {
        if (closeable != null) try
        {
            closeable.close();
        }
        catch (IOException e)
        {
        }
    }

}
