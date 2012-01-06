package de.thecodex.logparser.fetch;

import java.util.Collection;

public class LogFileFetcher {

    private final FetchMethod fetchMethod;

    public LogFileFetcher() {
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
