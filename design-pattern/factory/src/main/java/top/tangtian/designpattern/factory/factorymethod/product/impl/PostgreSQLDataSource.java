package top.tangtian.designpattern.factory.factorymethod.product.impl;

import top.tangtian.designpattern.factory.factorymethod.config.DatabaseConfig;
import top.tangtian.designpattern.factory.factorymethod.product.DataSource;

import java.sql.Connection;

public class PostgreSQLDataSource implements DataSource {
    private DatabaseConfig config;
    private boolean initialized = false;

    public PostgreSQLDataSource(DatabaseConfig config) {
        this.config = config;
        initialize();
    }

    private void initialize() {
        System.out.println("Initializing PostgreSQL DataSource...");
        System.out.println("  - Configuring PostgreSQL-specific parameters");
        System.out.println("  - Setting default_transaction_isolation");
        System.out.println("  - Configuring statement timeout");
        System.out.println("  - Enabling prepared statement caching");
        System.out.println("  - Max connections: " + config.getMaxConnections());
        this.initialized = true;
    }

    @Override
    public Connection getConnection() {
        if (!initialized) {
            throw new IllegalStateException("DataSource not initialized");
        }
        System.out.println("Getting PostgreSQL connection from pool...");
        return null; // 简化示例
    }

    @Override
    public void close() {
        System.out.println("Closing PostgreSQL DataSource...");
        System.out.println("  - Closing all active connections");
        System.out.println("  - Releasing connection pool");
        this.initialized = false;
    }

    @Override
    public boolean testConnection() {
        System.out.println("Testing PostgreSQL connection...");
        System.out.println("  - Executing: SELECT version()");
        return initialized;
    }

    @Override
    public String getInfo() {
        return String.format("PostgreSQL DataSource [%s] - Status: %s, Max Connections: %d",
                config.getUrl(),
                initialized ? "Active" : "Inactive",
                config.getMaxConnections());
    }

    @Override
    public String getDatabaseType() {
        return "PostgreSQL";
    }
}
