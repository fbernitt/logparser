package de.thecodex.logparser.importer;

import de.thecodex.logparser.flyway.FlywayWrapper;
import de.thecodex.logparser.flyway.SupportedDatabase;
import de.thecodex.logparser.log4j.Log4jLogEntry;
import de.thecodex.logparser.log4j.Log4jLogParser;
import de.thecodex.logparser.log4j.importer.Log4jLogEntryImporter;
import de.thecodex.logparser.origin.OriginHost;
import de.thecodex.logparser.origin.OriginLogFile;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Tests a database import and migration using a h2 in-memory-db.
 *
 * @author TheCodEx
 */
public class InMemoryImportTest {

    private Connection connection;

    @Before
    public void setupInMemoryDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:testdb";

        this.connection = DriverManager.getConnection(url);
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb");

        initAndMigrateFlyway(ds);
    }

    private void initAndMigrateFlyway(JdbcDataSource ds) {
        new FlywayWrapper(SupportedDatabase.H2).initAndMigrate(ds);
    }

    @Test
    public void thatFoobar() {
        Log4jLogEntryImporter entryImporter = new Log4jLogEntryImporter();
        DatabaseImporter<Log4jLogEntry> dbImport = new DatabaseImporter<Log4jLogEntry>(this.connection, entryImporter);
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("oneException.log");
        dbImport.importLog(new OriginLogFile(OriginHost.LOCALHOST, "/tmp/foobar"), new Log4jLogParser(inputStream));
    }

    @After
    public void stopInMemoryDB() throws SQLException {
        this.connection.close();
    }
}
