package top.tangtian.designpattern.factory.factorymethod.product.impl;

import top.tangtian.designpattern.factory.factorymethod.config.DatabaseConfig;
import top.tangtian.designpattern.factory.factorymethod.product.DataSource;

import java.sql.Connection;

public class MySQLDataSource implements DataSource {
    private DatabaseConfig config;
    private boolean initialized = false;

    public MySQLDataSource(DatabaseConfig config) {
        this.config = config;
        initialize();
    }

    private void initialize() {
        System.out.println("Initializing MySQL DataSource...");
        System.out.println("  - Configuring MySQL-specific parameters");
        System.out.println("  - Setting character encoding to UTF-8");
        System.out.println("  - Enabling connection pooling");
        System.out.println("  - Setting wait_timeout and interactive_timeout");
        System.out.println("  - Max connections: " + config.getMaxConnections());
        this.initialized = true;
    }

    @Override
    public Connection getConnection() {
        if (!initialized) {
            throw new IllegalStateException("DataSource not initialized");
        }
        // 实际应用中这里会返回真实的MySQL连接
        System.out.println("Getting MySQL connection from pool...");
        return null; // 简化示例
    }

    @Override
    public void close() {
        System.out.println("Closing MySQL DataSource...");
        System.out.println("  - Draining connection pool");
        System.out.println("  - Releasing resources");
        this.initialized = false;
    }

    @Override
    public boolean testConnection() {
        System.out.println("Testing MySQL connection...");
        System.out.println("  - Executing: SELECT 1");
        // 模拟连接测试
        return initialized;
    }

    @Override
    public String getInfo() {
        return String.format("MySQL DataSource [%s] - Status: %s, Max Connections: %d",
                config.getUrl(),
                initialized ? "Active" : "Inactive",
                config.getMaxConnections());
    }

    @Override
    public String getDatabaseType() {
        return "MySQL";
    }
}
