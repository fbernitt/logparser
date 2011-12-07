package de.thecodex.logparser.log4j;

import de.thecodex.logparser.parser.RawLogParser;
import de.thecodex.logparser.RawLogEntry;

import java.io.InputStream;
import java.util.Iterator;

/**
 * Semantic parser for log4j logs.
 */
public class Log4jLogParser implements Iterable<Log4jLogEntry> {

    private final InputStream inputStream;

    public Log4jLogParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }


    public Iterator<Log4jLogEntry> iterator() {
        return new Iterator<Log4jLogEntry>() {

            private final Iterator<RawLogEntry> rawIter = new RawLogParser(Log4jLogParser.this.inputStream).iterator();

            public boolean hasNext() {
                return rawIter.hasNext();
            }

            public Log4jLogEntry next() {
                return new Log4jLogEntry(rawIter.next());
            }

            public void remove() {
                rawIter.remove();
            }
        };
    }
}
