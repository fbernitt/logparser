package de.thecodex.logparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

/**
 * Allows to peek the next line. Required by log parser to decide whether the next message starts.
 */
public class PeekableLineReader {
    private final Stack<String> stack = new Stack<String>();
    private final BufferedReader reader;


    public PeekableLineReader(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    public String readLine() throws IOException {
        if (!this.stack.isEmpty()) {
            return this.stack.pop();
        } else {
            return lineFromReader();
        }
    }

    private String lineFromReader() throws IOException {
        return this.reader.readLine();
    }

    public String peekLine() throws IOException {
        if (!this.stack.isEmpty()) {
            return this.stack.peek();
        } else {
            String line = lineFromReader();
            if (line != null) {
                this.stack.push(line);
            }
            return line;
        }
    }
}