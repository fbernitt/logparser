package de.thecodex.logparser.log4j;

import de.thecodex.logparser.RawLogEntry;
import de.thecodex.logparser.parser.RawLogParser;

import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Semantic parser for log4j logs.
 */
public class Log4jLogParser implements Iterable<Log4jLogEntry> {

    private final InputStream inputStream;
    private final Pattern firstLinePattern;

    public Log4jLogParser(InputStream inputStream) {
        this(inputStream, null);
    }

    public Log4jLogParser(InputStream inputStream, Pattern firstLinePattern) {
        this.inputStream = inputStream;
        this.firstLinePattern = firstLinePattern;
    }


    public Iterator<Log4jLogEntry> iterator() {
        return new Iterator<Log4jLogEntry>() {

            private final Iterator<RawLogEntry> rawIter = new RawLogParser(Log4jLogParser.this.inputStream).iterator();

            public boolean hasNext() {
                return this.rawIter.hasNext();
            }

            public Log4jLogEntry next() {
                return parseEntry();
            }

            private Log4jLogEntry parseEntry() {
                Pattern firstLinePattern = Log4jLogParser.this.firstLinePattern;
                if (firstLinePattern != null) {
                    return new Log4jLogEntry(this.rawIter.next(), firstLinePattern);
                } else {
                    return new Log4jLogEntry(this.rawIter.next());
                }
            }

            public void remove() {
                this.rawIter.remove();
            }
        };
    }
}
