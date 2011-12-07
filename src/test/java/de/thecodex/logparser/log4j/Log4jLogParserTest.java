package de.thecodex.logparser.log4j;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class Log4jLogParserTest {

    @Test
    public void thatLiveLogIsParseableWithoutError() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("oneException.log");

        Log4jLogEntry[] entryArray = collectIntoArray(new Log4jLogParser(inputStream));

        assertEquals(5, entryArray.length);
    }

    @Test
    public void thatLogLevelsAreCorrect() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("oneException.log");
        String[] expectedLogLevels = new String[]{"INFO", "ERROR", "INFO", "WARN", "INFO"};

        Log4jLogEntry[] entryArray = collectIntoArray(new Log4jLogParser(inputStream));

        assertEquals(expectedLogLevels.length, entryArray.length);
        for (int i = 0; i < expectedLogLevels.length; i++) {
            assertEquals("Comparing entry nr. " + 4, expectedLogLevels[i], entryArray[i].getLogLevel());
        }
    }

    @Test
    public void thatSecondLEntryIsException() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("oneException.log");

        Log4jLogEntry[] entryArray = collectIntoArray(new Log4jLogParser(inputStream));

        // second entry contains exception
        assertTrue(entryArray[1].isException());
        assertEquals("java.lang.RuntimeException", entryArray[1].getExceptionClass());
        assertEquals("de.thecodex.logparser.RawLogParserTest.generateStacktrace(RawLogParserTest.java:79)", entryArray[1].getOurFirstLine());

        // all other entries do not contain exception
        assertFalse(entryArray[0].isException());
        assertNull(entryArray[0].getExceptionClass());
        assertNull(entryArray[0].getOurFirstLine());
        assertFalse(entryArray[2].isException());
        assertNull(entryArray[2].getExceptionClass());
        assertNull(entryArray[2].getOurFirstLine());
        assertFalse(entryArray[3].isException());
        assertNull(entryArray[3].getExceptionClass());
        assertNull(entryArray[3].getOurFirstLine());
    }

    @Test
    public void thatUnicodeMatches () {
        String str =  "login=gussysauer…web.de";

        Pattern p = Pattern.compile(".*", Pattern.DOTALL);
        assertTrue(p.matcher(str).matches());
    }

    private Log4jLogEntry[] collectIntoArray(Log4jLogParser parser) {
        ArrayList<Log4jLogEntry> entries = new ArrayList<Log4jLogEntry>();

        for (Log4jLogEntry e : parser) {
            entries.add(e);
        }

        Log4jLogEntry[] array = new Log4jLogEntry[entries.size()];
        return entries.toArray(array);
    }
}
