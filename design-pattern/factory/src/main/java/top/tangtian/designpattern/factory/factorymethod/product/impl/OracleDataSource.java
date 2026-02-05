package top.tangtian.designpattern.factory.factorymethod.product.impl;

import top.tangtian.designpattern.factory.factorymethod.config.DatabaseConfig;
import top.tangtian.designpattern.factory.factorymethod.product.DataSource;

import java.sql.Connection;

public class OracleDataSource implements DataSource {
    private DatabaseConfig config;
    private boolean initialized = false;

    public OracleDataSource(DatabaseConfig config) {
        this.config = config;
        initialize();
    }

    private void initialize() {
        System.out.println("Initializing Oracle DataSource...");
        System.out.println("  - Configuring Oracle-specific parameters");
        System.out.println("  - Setting NLS_LANG and NLS_DATE_FORMAT");
        System.out.println("  - Configuring Oracle RAC failover");
        System.out.println("  - Enabling fast connection failover (FCF)");
        System.out.println("  - Max connections: " + config.getMaxConnections());
        this.initialized = true;
    }

    @Override
    public Connection getConnection() {
        if (!initialized) {
            throw new IllegalStateException("DataSource not initialized");
        }
        System.out.println("Getting Oracle connection from pool...");
        return null; // 简化示例
    }

    @Override
    public void close() {
        System.out.println("Closing Oracle DataSource...");
        System.out.println("  - Disconnecting from Oracle instances");
        System.out.println("  - Clearing connection cache");
        this.initialized = false;
    }

    @Override
    public boolean testConnection() {
        System.out.println("Testing Oracle connection...");
        System.out.println("  - Executing: SELECT 1 FROM DUAL");
        return initialized;
    }

    @Override
    public String getInfo() {
        return String.format("Oracle DataSource [%s] - Status: %s, Max Connections: %d",
                config.getUrl(),
                initialized ? "Active" : "Inactive",
                config.getMaxConnections());
    }

    @Override
    public String getDatabaseType() {
        return "Oracle";
    }
}
