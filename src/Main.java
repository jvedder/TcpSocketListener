import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Main
{

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
   

    public static void main(String[] args) throws IOException
    {
        String timestamp;
        TcpServer server = new TcpServer();

        try
        {
            server.start();
        }
        catch (IOException ioe)
        {
            timestamp = Main.dateFormat.format(new Date());
            System.err.println(timestamp + " Couldn't start server:" + ioe);
            System.exit(-1);
        }

        timestamp = Main.dateFormat.format(new Date());
        System.out.println(timestamp + " Server started, Hit Enter to stop.");

        try
        {
            System.in.read();
        }
        catch (IOException ignored)
        {
        }

        server.stop();
        timestamp = Main.dateFormat.format(new Date());
        System.out.println(timestamp + " Server stopped.");
    }


}
