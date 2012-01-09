package de.thecodex.logparser;

import de.thecodex.logparser.fetch.FileInfo;
import de.thecodex.logparser.fetch.LogFileFetcher;
import de.thecodex.logparser.importer.DatabaseImporter;
import de.thecodex.logparser.log4j.Log4jLogEntry;
import de.thecodex.logparser.log4j.Log4jLogParser;
import de.thecodex.logparser.origin.OriginHost;
import de.thecodex.logparser.origin.OriginLogFile;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;


public class FetchAndImport {

    private static final Logger LOGGER = Logger.getLogger(FetchAndImport.class);

    private final String localFolder;

    private final DatabaseImporter<Log4jLogEntry> importer;
    private final List<String> hostNames;

    public FetchAndImport(DatabaseImporter<Log4jLogEntry> importer, String localFolder) {
        this(importer, localFolder, Arrays.asList("127.0.0.1"));
    }

    public FetchAndImport(DatabaseImporter<Log4jLogEntry> importer, String localFolder, List<String> hostNames) {
        this.importer = importer;
        this.localFolder = localFolder;
        this.hostNames = hostNames;

        createLocalFolder();
    }

    public void fetchAndImport(String fileName) {
        List<FileInfo> infos = createFileInfosForHostsAndFile(fileName);

        fetchFiles(infos);
        importFiles(infos);
    }

    private void importFiles(List<FileInfo> infos) {
        for (FileInfo info : infos) {
            try {
                LOGGER.info("Importing log " + info.getLocalFile() + " from host " + info.getHostName());
                this.importer.importLogInBatchMode(new OriginLogFile(new OriginHost(info.getHostName(), "127.0.0.1"), info.getRemoteFile()), new Log4jLogParser(createInputStream(info)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private InputStream createInputStream(FileInfo info) throws IOException {
        if (info.getLocalFile().endsWith("gz")) {
            return new GZIPInputStream(new FileInputStream(new File(info.getLocalFile())));
        } else {
            return new FileInputStream(new File(info.getLocalFile()));
        }
    }

    private void fetchFiles(List<FileInfo> infos) {
        ensureLocalParentFoldersExist(infos);

        new LogFileFetcher().fetch(infos);
    }

    private void ensureLocalParentFoldersExist(List<FileInfo> infos) {
        for (FileInfo info : infos) {
            new File(info.getLocalFile()).getParentFile().mkdirs();
        }
    }

    private List<FileInfo> createFileInfosForHostsAndFile(String fileName) {
        ArrayList<FileInfo> infos = new ArrayList<FileInfo>(this.hostNames.size());

        for (String hostName : this.hostNames) {
            infos.add(fileInfoForHostAndFile(hostName, fileName));
        }

        return infos;
    }

    private FileInfo fileInfoForHostAndFile(String hostName, String remoteFile) {
        String relativeLocalFile = hostName + File.separator + new File(remoteFile).getName();
        String localFile = this.
                localFolder + relativeLocalFile;

        return new FileInfo(hostName, remoteFile, localFile);
    }

    private void createLocalFolder() {
        File file = new File(this.localFolder);
        file.mkdirs();
    }
}
