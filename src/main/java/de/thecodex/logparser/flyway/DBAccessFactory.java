package de.thecodex.logparser.flyway;

import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: folker
 * Date: 06.01.12
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public interface DBAccessFactory {
    DataSource createDataSource();
}
