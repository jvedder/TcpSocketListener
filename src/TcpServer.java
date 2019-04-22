import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class TcpServer
{
    private final String hostname = null;
    private final int myPort = 30303;
    private ServerSocket myServerSocket;
    private Set<Socket> openConnections = new HashSet<Socket>();
    private Thread myThread;
    
    /**
     * Start the server.
     *
     * @throws IOException
     *             if the socket is in use.
     */
    public void start() throws IOException
    {
        System.out.println("Server.start()");
        
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
                        new Runnable()
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
                        };
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                } while (!myServerSocket.isClosed());

                System.out.println("myThread Exited");
            }
        });
        myThread.setDaemon(true);
        myThread.setName("TcpSocket Listener");
        System.out.println("Starting myThread....");
        myThread.start();
    }

    
    /**
     * Stop the server.
     */
    public void stop()
    {
        System.out.println("Server.stop()");
        try
        {
            safeClose(myServerSocket);
            closeAllConnections();
            if (myThread != null) myThread.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
