package de.thecodex.logparser.flyway;

import com.googlecode.flyway.core.Flyway;

import javax.sql.DataSource;
import java.io.File;

/**
 * Provides a wrapper around flyway to support different target sql dialects.
 */
public class FlywayWrapper {

    public static enum SupportedDatabase {
        H2("h2"),
        POSTGRES("postgres");

        private final String migrationFolder;

        private SupportedDatabase(String migrationFolder) {
            this.migrationFolder = migrationFolder;
        }
    }

    private final Flyway flyway = new Flyway();
    private final SupportedDatabase databaseType;

    public FlywayWrapper(SupportedDatabase database) {
        this.databaseType = database;
    }

    public void initAndMigrate(DataSource dataSource) {
        this.flyway.setDataSource(dataSource);
        this.flyway.setBaseDir(this.flyway.getBaseDir() + File.separatorChar + this.databaseType.migrationFolder);
        this.flyway.init();
        this.flyway.migrate();
    }

    public void cleanAndMigrate(DataSource dataSource) {
        this.flyway.setDataSource(dataSource);
        this.flyway.setBaseDir(this.flyway.getBaseDir() + File.separatorChar + this.databaseType.migrationFolder);
        this.flyway.clean();
        this.flyway.init();
        this.flyway.migrate();
    }

}
