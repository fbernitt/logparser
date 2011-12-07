package de.thecodex.logparser.fetch;

/**
 * Created by IntelliJ IDEA.
 * User: folker
 * Date: 02.12.11
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */
public class FileInfo {
    private final String hostName;
    private final String remoteFile;
    private final String localFile;


    public FileInfo(String hostName, String remoteFile, String localFile) {
        this.hostName = hostName;
        this.remoteFile = remoteFile;
        this.localFile = localFile;
    }

    public String getHostName() {
        return hostName;
    }

    public String getRemoteFile() {
        return remoteFile;
    }

    public String getLocalFile() {
        return localFile;
    }
}
