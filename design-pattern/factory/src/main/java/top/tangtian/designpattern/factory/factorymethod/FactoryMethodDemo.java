package top.tangtian.designpattern.factory.factorymethod;

import top.tangtian.designpattern.factory.factorymethod.config.DatabaseConfig;
import top.tangtian.designpattern.factory.factorymethod.factory.DataSourceFactory;
import top.tangtian.designpattern.factory.factorymethod.factory.impl.MySQLDataSourceFactory;
import top.tangtian.designpattern.factory.factorymethod.factory.impl.OracleDataSourceFactory;
import top.tangtian.designpattern.factory.factorymethod.factory.impl.PostgreSQLDataSourceFactory;
import top.tangtian.designpattern.factory.factorymethod.product.DataSource;

/**
 * Hello world!
 *数据源工厂
 * 企业应用需要支持多种数据库（MySQL、PostgreSQL、Oracle），每种数据库的连接配置和优化参数都不同。使用工厂方法模式来创建不同的数据源。
 * src/main/java/com/example/factory/
 * ├── product/                    # 产品
 * │   ├── DataSource.java             (抽象产品)
 * │   └── impl/
 * │       ├── MySQLDataSource.java    (具体产品)
 * │       ├── PostgreSQLDataSource.java
 * │       └── OracleDataSource.java
 * ├── factory/                    # 工厂
 * │   ├── DataSourceFactory.java      (抽象工厂)
 * │   └── impl/
 * │       ├── MySQLDataSourceFactory.java   (具体工厂)
 * │       ├── PostgreSQLDataSourceFactory.java
 * │       └── OracleDataSourceFactory.java
 * ├── config/
 * │   └── DatabaseConfig.java         (数据库配置)
 * └── FactoryMethodDemo.java          (测试)
 */
public class FactoryMethodDemo
{
    public static void main(String[] args) {
        // 测试1: 创建MySQL数据源
        testMySQL();

        // 测试2: 创建PostgreSQL数据源
        testPostgreSQL();

        // 测试3: 创建Oracle数据源
        testOracle();

        // 测试4: 动态选择工厂
        testDynamicFactory();
    }

    private static void testMySQL() {
        System.out.println("\n### TEST 1: MySQL DataSource ###");

        DatabaseConfig config = new DatabaseConfig(
                "jdbc:mysql://localhost:3306/mydb",
                "root",
                "password"
        );
        config.setMaxConnections(15);

        DataSourceFactory factory = new MySQLDataSourceFactory();
        DataSource dataSource = factory.createAndTest(config);

        // 使用数据源
        dataSource.getConnection();

        // 关闭数据源
        dataSource.close();
    }

    private static void testPostgreSQL() {
        System.out.println("\n\n### TEST 2: PostgreSQL DataSource ###");

        DatabaseConfig config = new DatabaseConfig(
                "jdbc:postgresql://localhost:5432/mydb",
                "postgres",
                "password"
        );

        DataSourceFactory factory = new PostgreSQLDataSourceFactory();
        DataSource dataSource = factory.createAndTest(config);

        dataSource.getConnection();
        dataSource.close();
    }

    private static void testOracle() {
        System.out.println("\n\n### TEST 3: Oracle DataSource ###");

        DatabaseConfig config = new DatabaseConfig(
                "jdbc:oracle:thin:@localhost:1521:orcl",
                "system",
                "password"
        );

        DataSourceFactory factory = new OracleDataSourceFactory();
        DataSource dataSource = factory.createAndTest(config);

        dataSource.getConnection();
        dataSource.close();
    }

    private static void testDynamicFactory() {
        System.out.println("\n\n### TEST 4: Dynamic Factory Selection ###");

        // 模拟从配置文件读取数据库类型
        String dbType = "mysql"; // 可以是 "mysql", "postgresql", "oracle"

        DatabaseConfig config = new DatabaseConfig(
                "jdbc:mysql://localhost:3306/mydb",
                "root",
                "password"
        );

        DataSourceFactory factory = getFactory(dbType);
        DataSource dataSource = factory.createAndTest(config);

        System.out.println("\nDatabase Type: " + dataSource.getDatabaseType());

        dataSource.close();
    }

    /**
     * 根据数据库类型获取对应的工厂
     * 这是简单工厂模式的应用
     */
    private static DataSourceFactory getFactory(String dbType) {
        switch (dbType.toLowerCase()) {
            case "mysql":
                return new MySQLDataSourceFactory();
            case "postgresql":
            case "postgres":
                return new PostgreSQLDataSourceFactory();
            case "oracle":
                return new OracleDataSourceFactory();
            default:
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
    }
}
