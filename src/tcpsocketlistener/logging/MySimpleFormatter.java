package tcpsocketlistener.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A formatter for java.util.logging that outputs each log record as a simple,
 * single line.
 */

public class MySimpleFormatter extends Formatter
{
    /**
     * Format string for printing the log record
     */
    private static final String FORMAT = "%1$-7s: %2$s%3$s%n";

    /**
     * Formats the given LogRecord in the format:
     * 
     * <pre>
     *    INFO   : The application started
     *    WARNING: This is some warning message
     *    SEVERE : This is a severe message with exception
     *    java.lang.IllegalArgumentException: invalid argument
     *             at MyClass.mash(MyClass.java:9)
     *             at MyClass.crunch(MyClass.java:6)
     *             at MyClass.main(MyClass.java:3)
     * </pre>
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
        return String.format(FORMAT, record.getLevel().getName(), record.getMessage(), throwable);
    }
}
