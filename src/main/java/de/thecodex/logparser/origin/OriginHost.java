package de.thecodex.logparser.origin;

/**
 * Defines the origin of a log message.
 */
public class OriginHost {

    public static final OriginHost LOCALHOST = new OriginHost("localhost", "127.0.0.1");

    private final String hostName;
    private final String ip;

    public OriginHost(String hostName, String ip) {
        this.hostName = hostName;
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public String getIp() {
        return ip;
    }
}
