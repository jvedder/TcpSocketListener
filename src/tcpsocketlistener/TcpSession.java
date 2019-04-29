package tcpsocketlistener;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class TcpSession
{
    private static final Logger logger = Logger.getLogger(TcpSession.class.getName());
    public static final int BUFSIZE = 8192;
    private PushbackInputStream inputStream;
    private final OutputStream outputStream;
    private String remoteIp;
    private int rlen;
    private int eol;

    public TcpSession(InputStream inputStream, OutputStream outputStream, InetAddress inetAddress)
    {
        this.inputStream = new PushbackInputStream(inputStream, BUFSIZE);
        this.outputStream = outputStream;
        this.remoteIp = inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress() ? "127.0.0.1"
                : inetAddress.getHostAddress().toString();
    }

    public void execute() throws IOException
    {
        logger.finer("ENTRY");

        try
        {
            // Read the first 8192 bytes.
            // The full header should fit in here.
            // Apache's default header limit is 8KB.
            // Do NOT assume that a single read will get the entire header
            // at once!
            byte[] buf = new byte[BUFSIZE];
            rlen = 0;
            int read = -1;
            while (true)
            {
                try
                {
                    logger.info("Reading from Socket Stream");
                    read = inputStream.read(buf, 0, BUFSIZE - rlen);
                }
                catch (Exception e)
                {
                    logger.info("TcpSession Shutdown (general exception)");

                    safeClose(inputStream);
                    safeClose(outputStream);
                    throw new SocketException("TcpSession Shutdown");
                }
                if (read == -1)
                {
                    logger.info("TcpSession Shutdown (read == -1)");

                    // socket was been closed
                    safeClose(inputStream);
                    safeClose(outputStream);
                    throw new SocketException("TcpSession Shutdown");
                }

                // rlen += read;
                // TODO: Check for buf[] full (rlen >= BUFSIZE)

                logger.info("Read " + read + " bytes.");
                String line = new String(buf, 0, read, StandardCharsets.UTF_8);
                System.out.println(line);

                // eol = findEndOfLine(buf, rlen);
                // if (eol > 0)
                // {
                // if (eol < rlen) inputStream.unread(buf, eol, rlen - eol);
                // String line = new String(buf, 0, eol,
                // StandardCharsets.UTF_8);
                // System.out.println(line);
                // rlen = 0;
                // }

            }

        }
        catch (SocketException e)
        {
            logger.warning("SocketException");
            // throw it out to close socket object (finalAccept)
            throw e;
        }
        // catch (SocketTimeoutException ste)
        // {
        //     logger.warning("SocketTimeoutException (unreachable)");
        //     safeClose(outputStream);        
        //     throw ste;
        // }
        // catch (IOException ioe)
        // {
        //     logger.warning("IOException (unreachable)");
        //     safeClose(outputStream);
        // }
        finally
        {
            logger.finer("EXIT");
        }

    }

    /**
     * Find byte index if the end of line
     */
    private int findEndOfLine(final byte[] buf, int rlen)
    {
        int eol = 0;
        while (eol < rlen)
        {
            if (buf[eol] == '\n') return eol + 1;
            eol++;
        }
        return 0;
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
