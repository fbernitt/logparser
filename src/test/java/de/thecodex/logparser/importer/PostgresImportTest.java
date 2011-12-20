package de.thecodex.logparser.importer;

import de.thecodex.logparser.flyway.FlywayWrapper;
import de.thecodex.logparser.log4j.Log4jLogEntry;
import de.thecodex.logparser.log4j.Log4jLogParser;
import de.thecodex.logparser.log4j.importer.Log4jLogEntryImporter;
import de.thecodex.logparser.origin.OriginHost;
import de.thecodex.logparser.origin.OriginLogFile;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Postgres import test.
 */
@Ignore
public class PostgresImportTest {

    private static final String DB_HOST = "foo";
    private static final String DB_USER = "bar";
    private static final String DB_NAME = "foobar";
    private static final String ORG_POSTGRESQL_DRIVER = "org.postgresql.Driver";
    private static final String JDBC_URL = "jdbc:postgresql://" + DB_HOST + "/" + DB_NAME;

    @Before
    public void initPostgresDatabase() throws ClassNotFoundException, SQLException {
        Class.forName(ORG_POSTGRESQL_DRIVER);

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName(DB_HOST);
        ds.setUser(DB_USER);
        ds.setDatabaseName(DB_NAME);

        cleanAndMigrateFlyway(ds);
    }

    private void cleanAndMigrateFlyway(DataSource ds) {
        new FlywayWrapper(FlywayWrapper.SupportedDatabase.POSTGRES).cleanAndMigrate(ds);
    }

    @Test
    public void thatLogIsImportedWithoutException() throws SQLException {

        Properties props = new Properties();
        props.setProperty("user", DB_USER);

        Connection conn = DriverManager.getConnection(JDBC_URL, props);

        Log4jLogEntryImporter entryImporter = new Log4jLogEntryImporter();
        DatabaseImporter<Log4jLogEntry> dbImport = new DatabaseImporter<Log4jLogEntry>(conn, entryImporter);
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("oneException.log");
        dbImport.importLog(new OriginLogFile(OriginHost.LOCALHOST, "/tmp/foobar"), new Log4jLogParser(inputStream));
    }

}
