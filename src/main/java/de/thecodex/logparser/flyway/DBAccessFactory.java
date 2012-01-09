package de.thecodex.logparser.flyway;

import javax.sql.DataSource;
import java.sql.Connection;

public interface DBAccessFactory {
    DataSource createDataSource();

    Connection createConnection();
}
