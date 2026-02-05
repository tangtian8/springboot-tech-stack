package top.tangtian.designpattern.factory.factorymethod.factory;

import top.tangtian.designpattern.factory.factorymethod.config.DatabaseConfig;
import top.tangtian.designpattern.factory.factorymethod.product.DataSource;

/**
 * 抽象工厂 - 定义创建数据源的工厂方法
 */
public abstract class DataSourceFactory {

    /**
     * 工厂方法 - 子类实现具体的创建逻辑
     */
    public abstract DataSource createDataSource(DatabaseConfig config);

    /**
     * 模板方法 - 定义创建数据源的标准流程
     */
    public final DataSource createAndTest(DatabaseConfig config) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Creating DataSource using " + getFactoryName());
        System.out.println("=".repeat(60));

        // 1. 验证配置
        validateConfig(config);

        // 2. 创建数据源（由子类实现）
        DataSource dataSource = createDataSource(config);

        // 3. 测试连接
        if (dataSource.testConnection()) {
            System.out.println("✓ DataSource created and tested successfully");
        } else {
            System.out.println("✗ DataSource creation failed");
        }

        // 4. 返回数据源
        System.out.println(dataSource.getInfo());
        System.out.println("=".repeat(60));

        return dataSource;
    }

    /**
     * 钩子方法 - 验证配置
     */
    protected void validateConfig(DatabaseConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Database config cannot be null");
        }
        if (config.getUrl() == null || config.getUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Database URL cannot be empty");
        }
        System.out.println("✓ Configuration validated");
    }

    /**
     * 获取工厂名称
     */
    protected abstract String getFactoryName();
}