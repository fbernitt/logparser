package de.thecodex.logparser;

/**
 * Detects the start of a log entry within the log stream. Necessary to split log events.
 */
public interface LogEntryStartDetector {

    boolean isStartOfEntry (String line);
}
