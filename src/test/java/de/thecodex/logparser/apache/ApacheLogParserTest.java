package de.thecodex.logparser.apache;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ApacheLogParserTest {

    @Test
    public void thatLiveLogIsParseable() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("apache/apache.log");

        int count = 0;
        for (ApacheLogEntry e : new ApacheLogParser(inputStream)) {
            count++;
        }

        assertEquals(10, count);
    }

    @Test
    public void thatPropertiesAreParsed () throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("apache/apache.log");
        ApacheLogEntry entry =  new ApacheLogParser(inputStream).iterator().next();

        Calendar  cal = Calendar.getInstance();
        cal.set(2011, 10, 24, 17, 5, 34);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected = cal.getTime();


        assertEquals("127.0.0.1", entry.getIp());
        assertEquals(expected, entry.getDate());
        assertEquals("GET", entry.getMethod());
        assertEquals("/a/sample/resource.html", entry.getUri());
        assertEquals(200, entry.getStatusCode());
        assertEquals("-", entry.getReferer());
        assertEquals("Mozilla/5.0 (Windows NT 6.1; rv:8.0) Gecko/20100101 Firefox/8.0", entry.getBrowser());
    }
}
