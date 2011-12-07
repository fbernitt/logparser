package de.thecodex.logparser.apache.importer;

import de.thecodex.logparser.apache.ApacheLogEntry;
import de.thecodex.logparser.importer.LogEntryImporter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * LogEntryImporter for apache logs.
 */
public class ApacheLogEntryImporter implements LogEntryImporter<ApacheLogEntry> {

    private PreparedStatement insertStmt;

    public void prepare(Connection connection) throws SQLException {
        this.insertStmt = connection.prepareStatement("INSERT INTO apache_log_events (ip, log_time, method, status_code, uri, referer, browser) VALUES(?, ?, ?, ?, ?, ?, ?)");
    }

    public void importEntry(Connection connection, int logFileId, ApacheLogEntry entry) throws SQLException {
        fillInsertFields(entry, logFileId);
        this.insertStmt.execute();
    }

    private void fillInsertFields(ApacheLogEntry entry, int logFileId) throws SQLException {
        this.insertStmt.setString(1, entry.getIp());
        this.insertStmt.setDate(2, new java.sql.Date(entry.getDate().getTime()));
        this.insertStmt.setString(3, entry.getMethod());
        this.insertStmt.setInt(4, entry.getStatusCode());
        this.insertStmt.setString(5, entry.getUri());
        this.insertStmt.setString(6, entry.getReferer());
        this.insertStmt.setString(7, entry.getBrowser());
    }

    public void batchImportEntry(Connection connection, int logFileId, ApacheLogEntry entry) throws SQLException {
        fillInsertFields(entry, logFileId);
        this.insertStmt.addBatch();
    }

    public void executeBatch(Connection connection) throws SQLException {
        this.insertStmt.executeBatch();
    }
}
