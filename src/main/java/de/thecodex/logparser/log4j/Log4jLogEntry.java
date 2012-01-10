package de.thecodex.logparser.log4j;

import de.thecodex.logparser.RawLogEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Semantic log entry for log4j logs.
 */
public class Log4jLogEntry {

    private static final Pattern pattern = Pattern.compile("^([0-9-]+ [0-9:,]+) ([A-Z]+) ([\\S]+) (.*)", Pattern.DOTALL);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS");
    private static final Pattern STACKTRACE_DETECTION_PATTERN = Pattern.compile("^\\s+at\\s+[a-zA-Z0-9.]+", Pattern.MULTILINE);
    private static final Pattern FIRST_LINE_PATTERN = Pattern.compile("^\\s*at\\s+(de.thecodex.*)$", Pattern.MULTILINE);
    private static final Pattern EXCEPTION_PATTERN = Pattern.compile("^\\s*([A-Za-z0-9.]+Exception)(:.*)?$", Pattern.MULTILINE);

    private final RawLogEntry rawEntry;
    private final Date date;
    private final String logLevel;
    private final String threadName;
    private final String message;
    private final String exceptionClass;
    private final boolean exception;
    private final String ourFirstLine;

    public Log4jLogEntry(RawLogEntry rawEntry) {
        this(rawEntry, FIRST_LINE_PATTERN);
    }

    public Log4jLogEntry(RawLogEntry rawEntry, Pattern firstLinePattern) {
        this.rawEntry = rawEntry;

        Matcher m = pattern.matcher(firstLine(rawEntry.getRawLogMessage()));
        if (!m.matches()) {
            throw new IllegalArgumentException("Failed to parse log message: " + rawEntry.getRawLogMessage());
        }

        try {
            this.date = dateFormat.parse(m.group(1));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        this.logLevel = m.group(2);
        this.threadName = m.group(3);
        this.message = m.group(4);
        this.exception = detectException(rawEntry.getRawLogMessage());
        this.exceptionClass = detectExceptionClass(rawEntry.getRawLogMessage());
        this.ourFirstLine = detectOurFirstLine(rawEntry.getRawLogMessage(), firstLinePattern);
    }

    private String detectOurFirstLine(String msg, Pattern firstLinePattern) {
        Matcher m = firstLinePattern.matcher(msg);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    /**
     * Detects the exception within a raw log message.
     * The exception is the line which starts with a fully qualified classname, optionally followed by a ':' char.
     *
     * @param rawLogMessage The log message
     * @return The fully qualified exception class or null if none found
     */
    private String detectExceptionClass(String rawLogMessage) {
        Matcher m = EXCEPTION_PATTERN.matcher(rawLogMessage);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    public String getLogLevel() {
        //return this.rawEntry.getRawLogMessage().split("\n")[0].split(" ")[2].trim();
        return this.logLevel;
    }

    private String firstLine(String str) {
        int newLinePos = str.indexOf('\n');

        if (newLinePos >= 0) {
            return str.substring(0, newLinePos);
        } else {
            return str;
        }
    }

    private boolean detectException(String str) {
        return STACKTRACE_DETECTION_PATTERN.matcher(str).find();
    }

    public String getMessage() {
        return message;
    }

    public String getThreadName() {
        return threadName;
    }

    public Date getDate() {
        return date;
    }

    public boolean isException() {
        return exception;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public String getOurFirstLine() {
        return ourFirstLine;
    }

    public RawLogEntry getRawEntry() {
        return rawEntry;
    }
}
