package top.tangtian.designpattern.factory.factorymethod.factory.impl;

import top.tangtian.designpattern.factory.factorymethod.config.DatabaseConfig;
import top.tangtian.designpattern.factory.factorymethod.factory.DataSourceFactory;
import top.tangtian.designpattern.factory.factorymethod.product.DataSource;
import top.tangtian.designpattern.factory.factorymethod.product.impl.PostgreSQLDataSource;

public class PostgreSQLDataSourceFactory extends DataSourceFactory {

    @Override
    public DataSource createDataSource(DatabaseConfig config) {
        System.out.println("Creating PostgreSQL DataSource...");

        // PostgreSQL特定的配置优化
        optimizeForPostgreSQL(config);

        return new PostgreSQLDataSource(config);
    }

    @Override
    protected String getFactoryName() {
        return "PostgreSQL DataSource Factory";
    }

    /**
     * PostgreSQL特定的优化配置
     */
    private void optimizeForPostgreSQL(DatabaseConfig config) {
        if (config.getMaxConnections() < 10) {
            config.setMaxConnections(20);
            System.out.println("  - Adjusted max connections to 20 for PostgreSQL");
        }

        config.setMinConnections(5);
        System.out.println("  - Set min connections to 5 for PostgreSQL");
    }
}