package de.thecodex.logparser.flyway;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class PostgresDBAccessFactory implements DBAccessFactory {

    private final SupportedDatabase databaseType;

    public PostgresDBAccessFactory(SupportedDatabase databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public DataSource createDataSource() {
        return null;
    }

    private PGSimpleDataSource createPostresDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName(DB_HOST);
        ds.setUser(DB_USER);
        ds.setDatabaseName(DB_NAME);

        return ds;
    }
}
