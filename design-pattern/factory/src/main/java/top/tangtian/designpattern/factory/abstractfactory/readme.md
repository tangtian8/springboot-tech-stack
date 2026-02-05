``````
一、理论基础
什么是抽象工厂模式？
定义：提供一个创建一系列相关或相互依赖对象的接口，而无需指定它们具体的类。
核心概念
工厂方法 vs 抽象工厂：

工厂方法：创建一个产品
抽象工厂：创建一族相关产品

工厂方法：
MySQLFactory → MySQLDataSource

抽象工厂：
MySQLFactory → MySQLDataSource + MySQLConnection + MySQLTransaction
核心角色
抽象工厂 (AbstractFactory)      - 定义创建产品族的接口
具体工厂 (ConcreteFactory)      - 实现创建具体产品族
抽象产品 (AbstractProduct)      - 定义产品接口
具体产品 (ConcreteProduct)      - 实现具体产品

二、Spring中的抽象工厂模式
Spring ApplicationContext
java// ApplicationContext就是一个抽象工厂
public interface ApplicationContext extends BeanFactory {
    // 创建一族相关的Bean
    Object getBean(String name);
    <T> T getBean(Class<T> requiredType);
    Environment getEnvironment();
    ApplicationEventPublisher getApplicationEventPublisher();
    // ... 创建多个相关对象
}

// Web应用的工厂
public class AnnotationConfigWebApplicationContext 
        extends AbstractApplicationContext {
    // 创建Web相关的一族Bean：
    // - ServletContext
    // - WebApplicationContext
    // - DispatcherServlet
}

// 非Web应用的工厂
public class AnnotationConfigApplicationContext 
        extends AbstractApplicationContext {
    // 创建标准的一族Bean
}
```

---

## 三、实战练习：跨平台UI组件工厂

### 场景描述
开发一个跨平台应用，需要支持不同操作系统的UI组件：
- **Windows风格**：按钮、文本框、复选框
- **Mac风格**：按钮、文本框、复选框
- **Linux风格**：按钮、文本框、复选框

每个平台的组件风格不同，但功能相同。

---

## 项目结构
```
src/main/java/com/example/abstractfactory/
├── product/                    # 产品
│   ├── Button.java                 (抽象产品A)
│   ├── TextBox.java                (抽象产品B)
│   ├── CheckBox.java               (抽象产品C)
│   └── impl/
│       ├── windows/
│       │   ├── WindowsButton.java
│       │   ├── WindowsTextBox.java
│       │   └── WindowsCheckBox.java
│       ├── mac/
│       │   ├── MacButton.java
│       │   ├── MacTextBox.java
│       │   └── MacCheckBox.java
│       └── linux/
│           ├── LinuxButton.java
│           ├── LinuxTextBox.java
│           └── LinuxCheckBox.java
├── factory/                    # 工厂
│   ├── UIFactory.java              (抽象工厂)
│   └── impl/
│       ├── WindowsUIFactory.java   (具体工厂1)
│       ├── MacUIFactory.java       (具体工厂2)
│       └── LinuxUIFactory.java     (具体工厂3)
├── app/
│   └── Application.java            (客户端应用)
└── AbstractFactoryDemo.java        (测试)
``````