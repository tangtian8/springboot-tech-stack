package top.tangtian.designpattern.factory.factorymethod.factory.impl;

import top.tangtian.designpattern.factory.factorymethod.config.DatabaseConfig;
import top.tangtian.designpattern.factory.factorymethod.factory.DataSourceFactory;
import top.tangtian.designpattern.factory.factorymethod.product.DataSource;
import top.tangtian.designpattern.factory.factorymethod.product.impl.MySQLDataSource;

public class MySQLDataSourceFactory extends DataSourceFactory {

    @Override
    public DataSource createDataSource(DatabaseConfig config) {
        System.out.println("Creating MySQL DataSource...");

        // MySQL特定的配置优化
        optimizeForMySQL(config);

        return new MySQLDataSource(config);
    }

    @Override
    protected String getFactoryName() {
        return "MySQL DataSource Factory";
    }

    /**
     * MySQL特定的优化配置
     */
    private void optimizeForMySQL(DatabaseConfig config) {
        // 为MySQL设置合理的默认值
        if (config.getMaxConnections() < 5) {
            config.setMaxConnections(10);
            System.out.println("  - Adjusted max connections to 10 for MySQL");
        }

        if (config.getConnectionTimeout() < 10000) {
            config.setConnectionTimeout(30000);
            System.out.println("  - Adjusted connection timeout to 30s for MySQL");
        }
    }
}