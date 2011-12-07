package de.thecodex.logparser;

/**
 * Interface for log parsers.
 */
public interface LogParser<T extends LogEntry> extends Iterable<T> {
}
