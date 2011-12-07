package de.thecodex.logparser;

import com.googlecode.flyway.core.Flyway;
import de.thecodex.logparser.importer.DatabaseImporter;
import de.thecodex.logparser.log4j.Log4jLogEntry;
import de.thecodex.logparser.log4j.importer.Log4jLogEntryImporter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: folker
 * Date: 02.12.11
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class LogParserCli implements Runnable {

    public static void main(String[] args) {
        new LogParserCli(args).run();
    }

    private final String[] args;

    public LogParserCli(String[] args) {
        this.args = args;
    }

    private DatabaseImporter<Log4jLogEntry> createDBImporter(CommandLine cmdLine) {
        return new DatabaseImporter<Log4jLogEntry>(createConnection(cmdLine), new Log4jLogEntryImporter());
    }

    private Connection createConnection (CommandLine cmd) {
        String dbHost = cmd.getOptionValue("H");
        String dbUser = cmd.getOptionValue("U");
        String dbName = cmd.getOptionValue("D");
        String url = "jdbc:postgresql://" + dbHost +  "/" + dbName;

        Properties props = new Properties();
        props.setProperty("user", dbUser);
        try {
            return DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void migrateFlyway (CommandLine cmd) {
        String dbHost = cmd.getOptionValue("H");
        String dbUser = cmd.getOptionValue("U");
        String dbName = cmd.getOptionValue("D");

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName(dbHost);
        ds.setUser(dbUser);
        ds.setDatabaseName(dbName);

        Flyway flyway = new Flyway();

        flyway.setDataSource(ds);
        flyway.clean();
        flyway.migrate();
    }

    private static CommandLine parseCmdLine(String[] args, Options options) {
        PosixParser parser = new PosixParser();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Options initCliOptions() {
        Options options = new Options();
        options.addOption("h", false, "display current help");
        options.addOption("H", true, "db host");
        options.addOption("U", true, "db user");
        options.addOption("D", true, "db name");
        options.addOption("r", true, "local repository for logfiles");
        options.addOption("s", true, "server/host names");
        return options;
    }

    public void run() {
        Options options = initCliOptions();

        CommandLine cmd = parseCmdLine(args, options);
        if (cmd.hasOption("h")) {
            System.out.println("Show the help!");
            System.exit(1);
        }

        migrateFlyway(cmd);

        DatabaseImporter<Log4jLogEntry> importer = createDBImporter(cmd);
        String localFolder = cmd.getOptionValue("r");
        FetchAndImport fai = new FetchAndImport(importer, localFolder);

        for (String fileName :  cmd.getArgs()) {
            fai.fetchAndImport(fileName);
        }
    }
}
