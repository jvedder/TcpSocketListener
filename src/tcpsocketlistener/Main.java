package tcpsocketlistener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import tcpsocketlistener.logging.MySimpleFormatter2;

public class Main
{
    /**
     * Invoke the factory method to get a new Logger or return the existing
     * Logger of the fully-qualified class name. Set to static as there is one
     * logger per class.
     */
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static Handler loggerFileHandler = null;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

    public static void main(String[] args) throws IOException
    {
        openLogger();

        String timestamp;
        TcpServer server = new TcpServer();

        try
        {
            server.start();
        }
        catch (IOException ioe)
        {
            logger.log(Level.SEVERE, "Couldn't start server", ioe);
            System.exit(-1);
        }

        logger.info("Server started");
        System.out.println("Server started, Hit Enter to stop.");

        try
        {
            System.in.read();
        }
        catch (IOException ignored)
        {
        }

        logger.info("Server stop requested");

        try
        {
            server.stop();
            timestamp = Main.dateFormat.format(new Date());
            System.out.println(timestamp + " Server stopped.");
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Couldn't stop server", ex);

        }
        closeLogger();
    }

    private static void openLogger()
    {
        try
        {
            // Creating FileHandler with a logging custom formatter
            loggerFileHandler = new FileHandler("TcpSocketListener.%u.log");
            //loggerFileHandler.setFormatter(new TimeStampFormatter());
            loggerFileHandler.setFormatter(new MySimpleFormatter2());
            
            
            final Logger packageLogger = Logger.getLogger(Main.class.getPackage().getName());
            packageLogger.addHandler(loggerFileHandler);
            packageLogger.setLevel(Level.ALL);

            // Remove default output to Console
            // logger.setUseParentHandlers(false);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private static void closeLogger()
    {
        try
        {
            if (loggerFileHandler != null)
            {
                loggerFileHandler.flush();
                loggerFileHandler.close();
                loggerFileHandler = null;
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

}
