package de.thecodex.logparser.importer;

import de.thecodex.logparser.origin.OriginHost;
import de.thecodex.logparser.origin.OriginLogFile;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;

public class DatabaseImporterTest {

    private final LogEntryImporter<Object> logEntryImporter = Mockito.mock(LogEntryImporter.class);
    private final Connection connection = Mockito.mock(Connection.class);
    private final List<Object> entries = new ArrayList<Object>();

    private final OriginLogFile logfile = new OriginLogFile(OriginHost.LOCALHOST, "/tmp/test.log");

    @Test
    @Ignore
    public void thatTransactionsAreUsed() throws SQLException {
        new DatabaseImporter<Object>(this.connection, this.logEntryImporter).importLog(this.logfile, this.entries);

        Mockito.verify(this.connection).setAutoCommit(false);
        Mockito.verify(this.connection).commit();
    }

    @Test
    public void thatTransactionGetsRolledBackOnError() throws SQLException {

        this.entries.add(new Object());
        Mockito.doThrow(new RuntimeException("Test exception")).when(this.logEntryImporter).importEntry(eq(this.connection),anyInt(), any());

        try {
            new DatabaseImporter<Object>(this.connection, this.logEntryImporter).importLog(this.logfile, this.entries);
            fail("Exception expected");
        } catch (RuntimeException e) {
            // expected
        }

        Mockito.verify(this.connection, Mockito.never()).commit();
        Mockito.verify(this.connection).rollback();
    }

    @Test
    @Ignore
    public void thatEntriesArePassedToImporter() throws SQLException {
        Object testEntry = new Object();
        this.entries.add(testEntry);
        new DatabaseImporter<Object>(this.connection, this.logEntryImporter).importLog(this.logfile, this.entries);

        Mockito.verify(this.logEntryImporter).importEntry(this.connection, 1, testEntry);
        Mockito.verifyNoMoreInteractions(this.logEntryImporter);
    }
}
