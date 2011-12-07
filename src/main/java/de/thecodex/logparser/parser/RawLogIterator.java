package de.thecodex.logparser.parser;

import de.thecodex.logparser.LogEntryStartDetector;
import de.thecodex.logparser.RawLogEntry;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over log entries within
 */
public class RawLogIterator implements Iterator<RawLogEntry> {

    private final PeekableLineReader reader;
    private final LogEntryStartDetector detector;

    public RawLogIterator(PeekableLineReader reader, LogEntryStartDetector detector) {
        this.reader = reader;
        this.detector = detector;
    }

    public boolean hasNext() {
        try {
            return this.reader.peekLine() != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RawLogEntry next() {
        try {
            String logMsg = nextLogMessage(this.reader);
            if (logMsg == null || logMsg.trim().isEmpty()) {
                throw new NoSuchElementException("No more log entries available!");
            }

            return new RawLogEntry(logMsg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("It's not possible to remove log messages from stream!");
    }

    private String nextLogMessage(PeekableLineReader reader) throws IOException {
        if (!isLogMessageAvailable(reader)) {
            return null;
        }

        return parseNextLogMessage(reader);
    }

    /**
     * Reads the next log message. Assumes that at least one line is available in reader!
     *
     * @param reader The log source reader
     * @return The log message
     * @throws java.io.IOException On IO problems
     */
    private String parseNextLogMessage(PeekableLineReader reader) throws IOException {
        StringBuilder lines = new StringBuilder();
        lines.append(reader.readLine());

        // read until next entry will starts
        String nextLine = reader.peekLine();
        while (nextLine != null && !isFirstLineOfNextEntry(nextLine)) {
            lines.append("\n");
            lines.append(reader.readLine());
            nextLine = reader.peekLine();
        }

        return lines.toString();
    }

    private boolean isFirstLineOfNextEntry(String nextLine) {
        return this.detector.isStartOfEntry(nextLine);
    }

    private boolean isLogMessageAvailable(PeekableLineReader reader) throws IOException {
        String line = reader.peekLine();
        return (line != null);
    }
}
