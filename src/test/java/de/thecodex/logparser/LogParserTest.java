package de.thecodex.logparser;

import de.thecodex.logparser.parser.RawLogParser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the LogParser.
 */
public class LogParserTest {

    @Test
    public void thatSimpleLineParsingWorks() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("simple.log");

        assertNotNull(inputStream);

        List<RawLogEntry> result = parse(inputStream);

        assertNotNull(result);
        assertEquals(10, result.size());
    }

    @Test
    public void thatExceptionParsingWorks() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("oneException.log");

        assertNotNull(inputStream);

        List<RawLogEntry> result = parse(inputStream);

        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    public void thatLineBreaksSurvive () throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("oneException.log");
        List<RawLogEntry> result = parse(inputStream);

        // second entry is the exception
        RawLogEntry exceptionEntry = result.get(1);
        int length = exceptionEntry.getRawLogMessage().split("\n").length;
        assertEquals(29, length);
    }

    @Test
    public void thatLineContentsIsComplete() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("Some log with some spaces and the numbers 123".getBytes());
        List<RawLogEntry> result = parse(inputStream);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Some log with some spaces and the numbers 123", result.get(0).getRawLogMessage());
    }

    @Test
    public void thatSequenceHasCorrectEntries() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("simple.log");

        int count = 0;
        for (RawLogEntry e : new RawLogParser(inputStream)) {
            count++;
        }

        assertEquals(10, count);
    }

    @Test
    public void thatEntryStartRecognition() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("multiYear.log");
        List<RawLogEntry> result = parse(inputStream);
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    private List<RawLogEntry> parse(InputStream inputStream) throws IOException {
        ArrayList<RawLogEntry> result = new ArrayList<RawLogEntry>(200);

        for (RawLogEntry e : new RawLogParser(inputStream)) {
            result.add(e);
        }

        return result;
    }
}
