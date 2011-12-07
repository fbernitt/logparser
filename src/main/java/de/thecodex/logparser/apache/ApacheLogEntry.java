package de.thecodex.logparser.apache;

import de.thecodex.logparser.RawLogEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Semantic log entry for apache logs.
 */
public class ApacheLogEntry {

    private static final Pattern logPattern = Pattern.compile("^([0-9.]+) - - \\[([^\\]]+)\\] \"([A-Z]+) (.*) HTTP....\" ([0-9]+) [0-9-]+ \"([^\"]*)\" \"([^\"]*)\".*");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z");

    private final String ip;
    private final String uri;
    private final String method;
    private final int statusCode;
    private final String referer;
    private final Date date;
    private final String browser;

    public ApacheLogEntry(RawLogEntry rawEntry) {
        Matcher matcher = logPattern.matcher(rawEntry.getRawLogMessage());
        if (!matcher.matches()) {
            throw new UnsupportedOperationException("Could not parse logline: " + rawEntry.getRawLogMessage());
        }

        this.ip = matcher.group(1);
        try {
            this.date = dateFormat.parse(matcher.group(2));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.method = matcher.group(3).trim();
        this.uri = matcher.group(4).trim();
        this.statusCode = Integer.valueOf(matcher.group(5));
        this.referer = matcher.group(6);
        this.browser = matcher.group(7);
    }

    public String getUri() {
        return uri;
    }

    public String getIp() {
        return ip;
    }

    public String getMethod() {
        return method;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReferer() {
        return referer;
    }

    public Date getDate() {
        return date;
    }

    public String getBrowser() {
        return browser;
    }
}
