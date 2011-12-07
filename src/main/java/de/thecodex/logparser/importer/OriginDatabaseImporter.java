package de.thecodex.logparser.importer;

import de.thecodex.logparser.origin.OriginHost;
import de.thecodex.logparser.origin.OriginLogFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database abstraction to find ids of hosts and logfiles in database.
 */
public class OriginDatabaseImporter {

    private static final String QUERY_SELECT_HOST_BY_NAME = "SELECT id FROM hosts WHERE hostname = ?";
    private static final String QUERY_INSERT_HOST = "INSERT INTO hosts (hostname, fqdn, ip) VALUES(?, ?, ?)";
    private static final String QUERY_SELECT_LOG_FILE_BY_NAME_AND_HOST_ID = "SELECT id FROM logfiles WHERE host_id = ? AND file_name = ?";
    private static final String QUERY_INSERT_LOG_FILE = "INSERT INTO logfiles (host_id, file_name) VALUES(?, ?)";

    private final Connection connection;

    private PreparedStatement stmtFindHost;
    private PreparedStatement stmtInsertHost;
    private PreparedStatement stmtFindFile;
    private PreparedStatement stmtInsertFile;

    public OriginDatabaseImporter(Connection connection) {
        this.connection = connection;

        try {
            this.stmtFindHost = connection.prepareStatement(QUERY_SELECT_HOST_BY_NAME);
            this.stmtInsertHost = connection.prepareStatement(QUERY_INSERT_HOST);
            this.stmtFindFile = connection.prepareStatement(QUERY_SELECT_LOG_FILE_BY_NAME_AND_HOST_ID);
            this.stmtInsertFile = connection.prepareStatement(QUERY_INSERT_LOG_FILE);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Searches database for existing entry for host. If no entry is found a new entry is inserted and the newly created id gets returned.
     *
     * @param host The host
     * @return The host id
     */
    public int findHostIdOrInsertHost(OriginHost host) {
        try {
            Integer hostId = findHostIdByName(host);
            if (hostId != null) {
                return hostId.intValue();
            }

            return insertHost (host);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Searches databaase for existing entry for log file. If no entry is found, a new entry is inserted.
     *
     * @param file The log file
     * @return The log file db entry id
     */
    public int findLogFileIdOrInsertNew (OriginLogFile file) {
        int hostId = findHostIdOrInsertHost(file.getOriginHost());

        try {
            Integer fileId = findFileIdByNameAndHost(file.getFileName(), hostId);
            if (fileId != null) {
                return fileId;
            }

            return insertLogFile(file, hostId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int insertLogFile(OriginLogFile file, int hostId) throws SQLException {
        this.stmtInsertFile.setInt(1, hostId);
        this.stmtInsertFile.setString(2, file.getFileName());
        this.stmtInsertFile.execute();


        return findFileIdByNameAndHost(file.getFileName(), hostId);
    }

    private Integer findFileIdByNameAndHost(String fileName, int hostId) throws SQLException {
        this.stmtFindFile.setInt(1, hostId);
        this.stmtFindFile.setString(2, fileName);

        ResultSet resultSet = this.stmtFindFile.executeQuery();

        Integer fileId = null;
        if (resultSet.next()) {
            fileId = resultSet.getInt(1);
        }
        resultSet.close();

        return fileId;
    }

    private int insertHost(OriginHost host) throws SQLException {
        // first insert the host into the db
        this.stmtInsertHost.setString(1, host.getHostName());
        this.stmtInsertHost.setString(2, host.getHostName());
        this.stmtInsertHost.setString(3, host.getIp());
        this.stmtInsertHost.execute();

        // and then return the newly created id
        return findHostIdByName(host);
    }

    private Integer findHostIdByName(OriginHost host) throws SQLException {
        this.stmtFindHost.setString(1, host.getHostName());
        ResultSet resultSet = this.stmtFindHost.executeQuery();

        Integer hostId = null;
        if (resultSet.next()) {
            hostId = resultSet.getInt(1);
        }
        resultSet.close();

        return hostId;
    }
}
