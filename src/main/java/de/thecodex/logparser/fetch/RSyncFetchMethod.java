package de.thecodex.logparser.fetch;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Fetches files using rsync. Calls the actual rsync executable.
 */
public class RSyncFetchMethod implements FetchMethod {

    private static final Logger LOGGER = Logger.getLogger(RSyncFetchMethod.class);

    public void fetch(FileInfo fileInfo) {
        String cmd = "rsync -avz " + fileInfo.getHostName() + ":" + fileInfo.getRemoteFile() + " " + fileInfo.getLocalFile();

        LOGGER.debug(cmd);

        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            int procResult = proc.waitFor();


            if (procResult != 0) {
                dumpStdoutAndStdErr(proc);

                throw new RuntimeException("Call of rsync failed with " + procResult + " for host " + fileInfo.getHostName() + " and file " + fileInfo.getRemoteFile());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void dumpStdoutAndStdErr(Process proc) {
        try {
            String line;

            BufferedReader stdin = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while ((line = stdin.readLine()) != null) {
                LOGGER.error(line);
            }

            BufferedReader stderr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((line = stderr.readLine()) != null) {
                LOGGER.error(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
