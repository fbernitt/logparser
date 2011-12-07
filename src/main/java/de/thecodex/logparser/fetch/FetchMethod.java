package de.thecodex.logparser.fetch;

/**
 * Interface for file fetchers. All these fetchers work in pull mode.
 */
public interface FetchMethod {

    /**
     * Fetches remoteFile from hostName and stores it into localFile.
     *
     * @param fileInfo The file info
     */
    void fetch(FileInfo fileInfo);
}
