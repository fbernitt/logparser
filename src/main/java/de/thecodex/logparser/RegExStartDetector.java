package de.thecodex.logparser;

import java.util.regex.Pattern;

/**
 * Detector using regular expressions.
 */
public class RegExStartDetector implements LogEntryStartDetector {

    private final Pattern pattern;

    public RegExStartDetector (String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public boolean isStartOfEntry(String line) {
        return this.pattern.matcher(line).find();
    }
}
