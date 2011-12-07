package de.thecodex.logparser.apache;

import de.thecodex.logparser.parser.RawLogParser;
import de.thecodex.logparser.RawLogEntry;

import java.io.InputStream;
import java.util.Iterator;

/**
 * Parser for our apache logs.
 */
public class ApacheLogParser implements Iterable<ApacheLogEntry> {

    private final InputStream inputStream;

    public ApacheLogParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Iterator<ApacheLogEntry> iterator() {

        return new Iterator<ApacheLogEntry>() {

            private final Iterator<RawLogEntry> rawIter = new RawLogParser(ApacheLogParser.this.inputStream, new EveryLineStartDetector()).iterator();

            public boolean hasNext() {
                return this.rawIter.hasNext();
            }

            public ApacheLogEntry next() {
                return new ApacheLogEntry(this.rawIter.next());
            }

            public void remove() {
                this.rawIter.remove();
            }
        };
    }
}
