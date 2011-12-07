package de.thecodex.logparser.importer;

import de.thecodex.logparser.origin.OriginLogFile;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Imports log messages into a database.
 */
public class DatabaseImporter<T> {

    private static final Logger LOGGER = Logger.getLogger(DatabaseImporter.class);

    private final Connection connection;
    private final LogEntryImporter<T> entryImporter;

    public DatabaseImporter(Connection connection, LogEntryImporter<T> entryImporter) {
        this.connection = connection;
        this.entryImporter = entryImporter;
    }

    public void importLog(OriginLogFile file, Iterable<T> logEntries) {
        try {
            this.connection.setAutoCommit(false);
            try {
                this.entryImporter.prepare(this.connection);

                int logFileId = findLogFileId(file);
                for (T entry : logEntries) {
                    this.entryImporter.importEntry(this.connection, logFileId, entry);
                }

                this.connection.commit();
            } catch (Exception e) {
                this.connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void importLogInBatchMode(OriginLogFile file, Iterable<T> logEntries) {
        try {
            this.connection.setAutoCommit(false);
            try {
                int count=0;
                this.entryImporter.prepare(this.connection);

                long startTime = System.currentTimeMillis();

                int logFileId = findLogFileId(file);
                for (T entry : logEntries) {
                    this.entryImporter.batchImportEntry(this.connection, logFileId, entry);
                    if (count % 1000 == 0) {
                        long now = System.currentTimeMillis();
                        int seconds = (int)((now - startTime)/1000);
                        if (seconds > 0) {
                            int perSecond = count/seconds;
                            LOGGER.debug("Batch commit, imported " + count + " entries yet. That is " + perSecond + " entries per second.");
                        }
                        this.entryImporter.executeBatch(this.connection);
                    }
                    count++;
                }
                this.entryImporter.executeBatch(this.connection);

                this.connection.commit();
                LOGGER.info("Imported " + count + " log events");
            } catch (SQLException e) {
                LOGGER.info(e.getNextException().getMessage());
                this.connection.rollback();
                throw new RuntimeException(e);

            } catch (Exception e) {
                this.connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int findLogFileId (OriginLogFile file) {
        return new OriginDatabaseImporter(this.connection).findLogFileIdOrInsertNew(file);
    }
}
