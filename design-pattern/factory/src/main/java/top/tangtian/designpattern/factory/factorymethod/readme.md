``````
使用场景决策树
```
开始
  ↓
产品种类会频繁增加？
  ├─ 是 → 工厂方法
  └─ 否 → 
      ↓
    产品创建逻辑复杂（需要不同的初始化流程）？
      ├─ 是 → 工厂方法
      └─ 否 →
          ↓
        产品种类 > 5个？
          ├─ 是 → 工厂方法
          └─ 否 → 简单工厂
``````

Spring使用工厂方法的例子

````
// FactoryBean - 工厂方法的应用
public interface FactoryBean<T> {
T getObject() throws Exception;  // 工厂方法
Class<?> getObjectType();
default boolean isSingleton() { return true; }
}

// ProxyFactoryBean - 创建AOP代理
public class ProxyFactoryBean implements FactoryBean<Object> {
@Override
public Object getObject() {
// 复杂的代理创建逻辑
return createProxy();
}
}

// SqlSessionFactoryBean - 创建MyBatis会话工厂
public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory> {
@Override
public SqlSessionFactory getObject() {
// 复杂的SqlSessionFactory创建逻辑
return buildSqlSessionFactory();
}
}
````

// 简单工厂 + 工厂方法
````
public class DataSourceFactoryProvider {
    private static Map<String, DataSourceFactory> factories = new HashMap<>();
    
    static {
        // 注册工厂
        factories.put("mysql", new MySQLDataSourceFactory());
        factories.put("postgres", new PostgreSQLDataSourceFactory());
        factories.put("oracle", new OracleDataSourceFactory());
    }
    
    // 简单工厂方法 - 获取工厂
    public static DataSourceFactory getFactory(String type) {
        DataSourceFactory factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown database type: " + type);
        }
        return factory;
    }
    
    // 便捷方法 - 直接创建数据源
    public static DataSource createDataSource(String type, DatabaseConfig config) {
        return getFactory(type).createDataSource(config);
    }
}

// 使用
DataSource ds = DataSourceFactoryProvider.createDataSource("mysql", config);
````