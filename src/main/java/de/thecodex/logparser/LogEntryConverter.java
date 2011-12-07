package de.thecodex.logparser;

/**
 * Converts a raw log entry into the expected target format.
 */
public interface LogEntryConverter<T extends LogEntry> {

    /**
     * Converts a raw log entry into the target format.
     *
     * @param rawLogEntry The raw log entry
     * @return The target log entry
     */
    T convert (RawLogEntry rawLogEntry);

}
