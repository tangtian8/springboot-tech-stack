package top.tangtian.designpattern.factory.factorymethod.factory.impl;

import top.tangtian.designpattern.factory.factorymethod.config.DatabaseConfig;
import top.tangtian.designpattern.factory.factorymethod.factory.DataSourceFactory;
import top.tangtian.designpattern.factory.factorymethod.product.DataSource;
import top.tangtian.designpattern.factory.factorymethod.product.impl.OracleDataSource;

public class OracleDataSourceFactory extends DataSourceFactory {

    @Override
    public DataSource createDataSource(DatabaseConfig config) {
        System.out.println("Creating Oracle DataSource...");

        // Oracle特定的配置优化
        optimizeForOracle(config);

        return new OracleDataSource(config);
    }

    @Override
    protected String getFactoryName() {
        return "Oracle DataSource Factory";
    }

    /**
     * Oracle特定的优化配置
     */
    private void optimizeForOracle(DatabaseConfig config) {
        if (config.getMaxConnections() < 15) {
            config.setMaxConnections(30);
            System.out.println("  - Adjusted max connections to 30 for Oracle");
        }

        if (config.getConnectionTimeout() < 20000) {
            config.setConnectionTimeout(60000);
            System.out.println("  - Adjusted connection timeout to 60s for Oracle");
        }

        config.setMinConnections(10);
        System.out.println("  - Set min connections to 10 for Oracle");
    }
}