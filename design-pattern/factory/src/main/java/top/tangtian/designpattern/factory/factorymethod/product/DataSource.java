package top.tangtian.designpattern.factory.factorymethod.product;

import java.sql.Connection;

public interface DataSource {
    /**
     * 获取数据库连接
     */
    Connection getConnection();

    /**
     * 关闭数据源
     */
    void close();

    /**
     * 测试连接
     */
    boolean testConnection();

    /**
     * 获取数据源信息
     */
    String getInfo();

    /**
     * 获取数据库类型
     */
    String getDatabaseType();
}
