package de.thecodex.logparser;

import de.thecodex.logparser.flyway.DBAccessFactory;
import de.thecodex.logparser.flyway.FlywayWrapper;
import de.thecodex.logparser.flyway.PostgresDBAccessFactory;
import de.thecodex.logparser.flyway.SupportedDatabase;
import de.thecodex.logparser.importer.DatabaseImporter;
import de.thecodex.logparser.log4j.Log4jLogEntry;
import de.thecodex.logparser.log4j.importer.Log4jLogEntryImporter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class LogParserCli implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(LogParserCli.class);

    public static void main(String[] args) {
        new LogParserCli(args).run();
    }

    private final String[] args;
    private String dbHost;
    private String dbUser;
    private String dbName;
    private DBAccessFactory dbAccessFactory;
    private SupportedDatabase dbType = SupportedDatabase.POSTGRES;

    public LogParserCli(String[] args) {
        this.args = args;
    }

    private DatabaseImporter<Log4jLogEntry> createDBImporter() {
        return new DatabaseImporter<Log4jLogEntry>(this.dbAccessFactory.createConnection(), new Log4jLogEntryImporter());
    }

    private void initDBFields(CommandLine cmd) {
        this.dbHost = cmd.getOptionValue("H");
        this.dbUser = cmd.getOptionValue("U");
        this.dbName = cmd.getOptionValue("D");
        String dbTypeId = cmd.getOptionValue("t");

        SupportedDatabase db = SupportedDatabase.valueOf(dbTypeId);
        switch (db) {
            case POSTGRES:
                this.dbType = SupportedDatabase.POSTGRES;
                this.dbAccessFactory = new PostgresDBAccessFactory(this.dbUser, this.dbHost, this.dbName);
                break;

            default:
                throw new UnsupportedOperationException("Database type " + dbTypeId + " is not supported!");
        }
    }

    private void migrateFlyway(CommandLine cmd) {
        FlywayWrapper flyway = new FlywayWrapper(this.dbType);
        flyway.cleanAndMigrate(this.dbAccessFactory.createDataSource());
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
        options.addOption("p", true, "first line pattern");
        options.addOption("r", true, "local repository for logfiles");
        options.addOption("s", true, "server/host names");
        options.addOption("t", true, "db type (postgres, h2)");
        return options;
    }

    public void run() {
        Options options = initCliOptions();

        CommandLine cmd = parseCmdLine(this.args, options);
        if (cmd.hasOption("h")) {
            System.out.println("Show the help!");
            System.exit(1);
        }
        initDBFields(cmd);

        LOGGER.info("migrating flyway...");
        migrateFlyway(cmd);

        FetchAndImport fai = createFetchAndImport(cmd);

        for (String fileName : cmd.getArgs()) {
            LOGGER.info("Importing file " + fileName);
            fai.fetchAndImport(fileName);
        }
    }

    private FetchAndImport createFetchAndImport(CommandLine cmd) {
        DatabaseImporter<Log4jLogEntry> importer = createDBImporter();
        String localFolder = cmd.getOptionValue("r");
        Pattern firstLinePattern = null;
        if (cmd.hasOption("p")) {
            firstLinePattern = Pattern.compile(cmd.getOptionValue("p"));
            LOGGER.info("Using first line detection-pattern: " + cmd.getOptionValue("p"));
        }

        if (cmd.hasOption("s")) {
            List<String> hostNames = Arrays.asList(cmd.getOptionValue("s").split(","));
            return new FetchAndImport(importer, localFolder, hostNames, firstLinePattern);
        } else {
            return new FetchAndImport(importer, localFolder, firstLinePattern);
        }
    }
}
