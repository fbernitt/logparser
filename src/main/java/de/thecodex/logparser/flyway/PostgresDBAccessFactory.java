package de.thecodex.logparser.flyway;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresDBAccessFactory implements DBAccessFactory {

    private final String user;
    private final String host;
    private final String dbName;

    public PostgresDBAccessFactory(String user, String host, String dbName) {
        this.user = user;
        this.host = host;
        this.dbName = dbName;
    }

    @Override
    public DataSource createDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName(this.host);
        ds.setUser(this.user);
        ds.setDatabaseName(this.dbName);

        return ds;
    }

    @Override
    public Connection createConnection() {
        Properties props = new Properties();
        props.setProperty("user", this.user);

        try {
            return DriverManager.getConnection(buildUrl(), props);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildUrl() {
        return "jdbc:postgresql://" + this.host + "/" + this.dbName;
    }
}
