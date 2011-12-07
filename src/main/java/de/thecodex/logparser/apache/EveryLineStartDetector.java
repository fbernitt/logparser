package de.thecodex.logparser.apache;

import de.thecodex.logparser.LogEntryStartDetector;

/**
 * Assumes every line is a new log entry
 */
public class EveryLineStartDetector implements LogEntryStartDetector {
    public boolean isStartOfEntry(String line) {
        return true;    // each line is a new log entry, every time
    }
}
