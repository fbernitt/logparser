package de.thecodex.logparser.fetch;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: folker
 * Date: 01.12.11
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class LogFileFetcher {

    private final FetchMethod fetchMethod;

    public LogFileFetcher () {
        this(new RSyncFetchMethod());
    }

    public LogFileFetcher(FetchMethod fetchMethod) {
        this.fetchMethod = fetchMethod;
    }

    public void fetch(Collection<FileInfo> files) {
        for (FileInfo fileInfo : files) {
            this.fetchMethod.fetch(fileInfo);
        }
    }
}
