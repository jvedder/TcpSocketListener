package tcpsocketlistener.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A formatter for java.util.logging that outputs each log record as a simple,
 * single line with a time stamp.
 */
public class TimeStampFormatter extends Formatter
{
    /**
     * Format string for printing the log record
     */
    private static final String FORMAT = "[%1$td-%1$tb-%1$tY %1$tT] %2$-7s: %3$s: %4$s%5$s%n";

    /**
     * Local variable used to hold and the time stamp of the log record in date
     * and time format.
     */
    private final Date timeStamp = new Date();

    /**
     * Formats the given LogRecord to show TimeStamp, Severity, Class Name,
     * Message, and optional stack trace when an exception is included.
     * 
     * Adapted from the Oracle implementation of
     * java.util.logging.SimpleFormatter (which is shared under GNU General
     * Public License version 2)
     * 
     * @param record
     *            the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record)
    {
        timeStamp.setTime(record.getMillis());

        String throwable = "";
        if (record.getThrown() != null)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        
        String msg = record.getMessage();
        if (msg.equals("ENYRTY") || msg.equals("EXIT")) 
        {
            msg += " " + record.getSourceMethodName();
        }
        
        return String.format(FORMAT, timeStamp, record.getLevel().getName(), msg, getClass().getName(),
                record.getMessage(), throwable);
    }
}
