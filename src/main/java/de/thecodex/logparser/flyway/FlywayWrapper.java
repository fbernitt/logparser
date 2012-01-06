package de.thecodex.logparser.flyway;

import com.googlecode.flyway.core.Flyway;

import javax.sql.DataSource;
import java.io.File;

/**
 * Provides a wrapper around flyway to support different target sql dialects.
 */
public class FlywayWrapper {

    private final Flyway flyway = new Flyway();
    private final SupportedDatabase databaseType;

    public FlywayWrapper(SupportedDatabase database) {
        this.databaseType = database;
    }

    public void initAndMigrate(DataSource dataSource) {
        this.flyway.setDataSource(dataSource);
        this.flyway.setBaseDir(baseDirForDatabaseType());
        this.flyway.init();
        this.flyway.migrate();
    }

    public void cleanAndMigrate(DataSource dataSource) {
        this.flyway.setDataSource(dataSource);
        this.flyway.setBaseDir(baseDirForDatabaseType());
        this.flyway.clean();
        this.flyway.init();
        this.flyway.migrate();
    }

    private String baseDirForDatabaseType() {
        return this.flyway.getBaseDir() + File.separatorChar + this.databaseType.getMigrationFolder();
    }

}
