package de.thecodex.logparser.log4j.importer;

import de.thecodex.logparser.importer.LogEntryImporter;
import de.thecodex.logparser.log4j.Log4jLogEntry;

import java.sql.*;

/**
 * Imports Log4JEntries into the database.
 */
public class Log4jLogEntryImporter implements LogEntryImporter<Log4jLogEntry> {

    private PreparedStatement insertStmt;

    public void prepare(Connection connection) throws SQLException {
        this.insertStmt = connection.prepareStatement("INSERT INTO tomcat_log_events (log_time, level, message, raw_msg, is_exception, exception_class, our_first_line, logfile_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
    }

    public void importEntry(Connection connection, int logFileId, Log4jLogEntry entry) throws SQLException {
        fillInsertFields(entry, logFileId);
        this.insertStmt.execute();
    }

    public void batchImportEntry(Connection connection, int logFileId, Log4jLogEntry entry) throws SQLException {
        fillInsertFields(entry, logFileId);
        this.insertStmt.addBatch();
    }

    public void executeBatch(Connection connection) throws SQLException {
         this.insertStmt.executeBatch();
    }

    private void fillInsertFields(Log4jLogEntry entry, int logFileId) throws SQLException {
        this.insertStmt.setTimestamp(1, new Timestamp(entry.getDate().getTime()));
        this.insertStmt.setString(2, entry.getLogLevel());
        this.insertStmt.setString(3, entry.getMessage());
        this.insertStmt.setString(4, entry.getRawEntry().getRawLogMessage());
        this.insertStmt.setBoolean(5, entry.isException());
        this.insertStmt.setString(6, entry.getExceptionClass());
        this.insertStmt.setString(7, entry.getOurFirstLine());
        this.insertStmt.setInt(8, logFileId);
    }
}
