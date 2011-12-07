package de.thecodex.logparser.parser;

import de.thecodex.logparser.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Simple parser for log4j logfiles.
 * <p/>
 * Memory consuption is low, only current log lines are on heap.
 * Only iterable once!
 */
public class RawLogParser implements LogParser<RawLogEntry> {


    public static LogParser<RawLogEntry> createRawParser(InputStream inputStream)  {
        return new RawLogParser(inputStream);
    }

    private static class KeepRawLogEntryConverter implements LogEntryConverter<RawLogEntry> {

        public RawLogEntry convert(RawLogEntry rawLogEntry) {
            return rawLogEntry;
        }
    }

    private static class ConvertingLogIterator<T extends LogEntry> implements Iterable<T> {

        private final RawLogIterator rawLogIterator;
        private final LogEntryConverter<T> converter;

        public ConvertingLogIterator (RawLogIterator rawLogIterator, LogEntryConverter<T> converter) {
            this.rawLogIterator = rawLogIterator;
            this.converter = converter;
        }

        public Iterator<T> iterator() {
            return new Iterator<T>() {

                public boolean hasNext() {
                    return ConvertingLogIterator.this.rawLogIterator.hasNext();
                }

                public T next() {
                    return ConvertingLogIterator.this.converter.convert(ConvertingLogIterator.this.rawLogIterator.next());
                }

                public void remove() {
                    ConvertingLogIterator.this.rawLogIterator.remove();
                }
            };
        }
    }

    private final PeekableLineReader peekReader;
    private final LogEntryStartDetector detector;


    public RawLogParser(InputStream inputStream) {
        this(inputStream, "^[0-9]{4}.*");
    }

    public RawLogParser(InputStream inputStream, String newEntryPattern) {
        this.peekReader = new PeekableLineReader(new InputStreamReader(inputStream));
        this.detector = new RegExStartDetector(newEntryPattern);
    }

    public RawLogParser(InputStream inputStream, LogEntryStartDetector detector) {
        this.peekReader = new PeekableLineReader(new InputStreamReader(inputStream));
        this.detector = detector;
    }

    public Iterator<RawLogEntry> iterator() {
        return new ConvertingLogIterator(new RawLogIterator(this.peekReader, this.detector), new KeepRawLogEntryConverter()).iterator();
    }

}
