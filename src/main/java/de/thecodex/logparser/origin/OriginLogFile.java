package de.thecodex.logparser.origin;

/**
 * Created by IntelliJ IDEA.
 * User: folker
 * Date: 25.11.11
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class OriginLogFile {

    private final OriginHost originHost;
    private final String fileName;

    public OriginLogFile(OriginHost originHost, String fileName) {
        this.originHost = originHost;
        this.fileName = fileName;
    }

    public OriginHost getOriginHost() {
        return originHost;
    }

    public String getFileName() {
        return fileName;
    }
}
