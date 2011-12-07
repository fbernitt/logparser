package de.thecodex.logparser.importer;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: folker
 * Date: 25.11.11
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public interface LogEntryImporter<T> {

    void prepare (Connection connection) throws SQLException;

    void importEntry (Connection connection, int logFileId, T entry) throws SQLException;

    void batchImportEntry (Connection connection, int logFileId,  T entry) throws SQLException;

    void executeBatch (Connection connection) throws SQLException;
}
