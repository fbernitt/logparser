package de.thecodex.logparser;

/**
 * A RawLogEntry represents a raw log message in a log file.
 */
public class RawLogEntry implements LogEntry {
    private final String rawLogMessage;
    private final int lineCount;

    public RawLogEntry(String rawLogMessage) {
        this.rawLogMessage = rawLogMessage;
        this.lineCount = countLines(rawLogMessage);
    }

    public String getRawLogMessage() {
        return rawLogMessage;
    }

    public int getLineCount() {
        return lineCount;
    }

    private static int countLines (String msg) {
        int count=0;
        for (char c : msg.toCharArray()) {
            if (c == '\n') {
                count++;
            }
        }
        return count;
    }
}
