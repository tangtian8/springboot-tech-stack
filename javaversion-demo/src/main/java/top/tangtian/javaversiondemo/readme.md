# Java JDK 11 到 JDK 21 特性详细列表

## 📌 Java 11 (LTS - 2018年9月)

### 语言特性
- **Lambda 参数的局部变量语法**: 可以在 lambda 表达式中使用 `var`
- **Nest-Based Access Control**: 简化嵌套类之间的访问控制

### API 增强
- **新的字符串方法**:
    - `isBlank()`: 检查字符串是否为空或只包含空白
    - `lines()`: 返回行的 Stream
    - `strip()`, `stripLeading()`, `stripTrailing()`: 去除空白
    - `repeat(int)`: 重复字符串
- **新的 HTTP Client API**: 替代旧的 `HttpURLConnection`
- **文件读写新方法**:
    - `Files.readString(Path)`
    - `Files.writeString(Path, String)`
- **Collection.toArray()**: 新的重载方法

### JVM 改进
- **动态类文件常量**: 支持 `CONSTANT_Dynamic`
- **Epsilon GC**: 无操作垃圾收集器
- **ZGC**: 可扩展低延迟垃圾收集器(实验性)

### 移除的功能
- Java EE 和 CORBA 模块被移除
- JavaFX 从 JDK 中移除
- Java Web Start 被移除

---

## 📌 Java 12 (2019年3月)

### 语言特性
- **Switch 表达式(预览)**: switch 可以作为表达式使用，引入箭头语法

### API 增强
- **String 新方法**:
    - `indent(int)`: 调整缩进
    - `transform(Function)`: 转换字符串
- **Files.mismatch()**: 比较两个文件
- **Collectors.teeing()**: 组合两个收集器

### JVM 改进
- **Shenandoah GC**: 低暂停时间垃圾收集器(实验性)
- **默认 CDS 归档**: 改善启动时间

---

## 📌 Java 13 (2019年9月)

### 语言特性
- **文本块(预览)**: 多行字符串字面量，使用 `"""`
- **Switch 表达式(第二次预览)**: 引入 `yield` 关键字

### API 增强
- **String 文本块相关方法**:
    - `formatted()`: 格式化字符串
    - `stripIndent()`: 移除附带的空白
    - `translateEscapes()`: 转义序列转换

---

## 📌 Java 14 (2020年3月)

### 语言特性
- **Switch 表达式(正式)**: 成为标准特性
- **instanceof 模式匹配(预览)**: 简化类型检查和转换
- **Records(预览)**: 不可变数据类

### JVM 改进
- **有用的 NullPointerException**: 精确指出哪个变量为 null
- **JFR 事件流**: 持续监控 JVM

---

## 📌 Java 15 (2020年9月)

### 语言特性
- **文本块(正式)**: 成为标准特性
- **Sealed Classes(预览)**: 限制类的继承
- **Records(第二次预览)**: 改进

### API 增强
- **Hidden Classes**: 框架使用的隐藏类
- **CharSequence.isEmpty()**: 新增默认方法

### JVM 改进
- **ZGC 和 Shenandoah**: 转为产品特性
- **移除 Nashorn JavaScript 引擎**

---

## 📌 Java 16 (2021年3月)

### 语言特性
- **Records(正式)**: 成为标准特性
- **instanceof 模式匹配(正式)**: 成为标准特性
- **Sealed Classes(第二次预览)**

### API 增强
- **Stream.toList()**: 简化流转换为列表
- **Stream.mapMulti()**: 一对多转换
- **Day Period Support**: 时间 API 增强
- **Vector API(孵化器)**: SIMD 计算

### JVM 改进
- **Unix-Domain Socket Channels**: 进程间通信
- **Foreign Linker API(孵化器)**: 调用本地代码
- **Foreign-Memory Access API(第三次孵化器)**

---

## 📌 Java 17 (LTS - 2021年9月)

### 语言特性
- **Sealed Classes(正式)**: 成为标准特性
- **Pattern Matching for switch(预览)**: switch 支持模式匹配
- **恢复永久性废弃强封装**: 模块系统增强

### API 增强
- **增强的伪随机数生成器**: 新的 `RandomGenerator` 接口
- **Context-Specific Deserialization Filters**: 序列化过滤器

### JVM 改进
- **移除 RMI Activation**
- **移除实验性 AOT 和 JIT 编译器**
- **废弃 Applet API**

### 其他
- **macOS/AArch64 移植**: 支持 Apple Silicon

---

## 📌 Java 18 (2022年3月)

### 语言特性
- **UTF-8 默认字符集**: 所有 API 默认使用 UTF-8
- **简单 Web 服务器**: `jwebserver` 命令行工具
- **代码片段 API**: `@snippet` 标签用于 JavaDoc

### API 增强
- **Vector API(第三次孵化器)**
- **Foreign Function & Memory API(第二次孵化器)**
- **Pattern Matching for switch(第二次预览)**

### JVM 改进
- **Internet-Address Resolution SPI**: 可插拔的地址解析

---

## 📌 Java 19 (2022年9月)

### 语言特性
- **Record Patterns(预览)**: 解构 record
- **Pattern Matching for switch(第三次预览)**
- **Virtual Threads(预览)**: 轻量级线程
- **Structured Concurrency(孵化器)**: 结构化并发

### API 增强
- **Foreign Function & Memory API(预览)**
- **Vector API(第四次孵化器)**

---

## 📌 Java 20 (2023年3月)

### 语言特性
- **Record Patterns(第二次预览)**
- **Pattern Matching for switch(第四次预览)**
- **Virtual Threads(第二次预览)**
- **Structured Concurrency(第二次孵化器)**

### API 增强
- **Scoped Values(孵化器)**: 线程间共享不可变数据
- **Foreign Function & Memory API(第二次预览)**
- **Vector API(第五次孵化器)**

---

## 📌 Java 21 (LTS - 2023年9月)

### 语言特性
- **Record Patterns(正式)**: 成为标准特性
- **Pattern Matching for switch(正式)**: 成为标准特性
- **Virtual Threads(正式)**: 成为标准特性
- **Sequenced Collections**: 新的集合接口
- **String Templates(预览)**: 字符串插值

### API 增强
- **Sequenced Collections 接口**:
    - `SequencedCollection`
    - `SequencedSet`
    - `SequencedMap`
- **新的集合方法**:
    - `getFirst()`, `getLast()`
    - `addFirst()`, `addLast()`
    - `removeFirst()`, `removeLast()`
    - `reversed()`

### 并发增强
- **Structured Concurrency(预览)**: 简化多线程编程
- **Scoped Values(预览)**: 替代 ThreadLocal

### API 改进
- **Foreign Function & Memory API(正式)**: 安全高效地访问本地代码和内存
- **Vector API(第六次孵化器)**: SIMD 计算

### JVM 改进
- **Generational ZGC**: ZGC 支持分代
- **废弃 Windows 32-bit x86 移植**
- **准备禁止动态加载代理**

---

## 🎯 如何运行示例代码

### 1. 确保安装 JDK 21
```bash
java -version
# 应该显示 java version "21" 或更高
```

### 2. 编译代码
```bash
javac --enable-preview --release 21 JavaFeatures.java
```

### 3. 运行代码
```bash
java --enable-preview JavaFeatures
```

### 注意事项
- 某些预览特性需要使用 `--enable-preview` 标志
- 确保使用 JDK 21 或更高版本
- 虚拟线程特性在 Java 21 中已正式发布
- Record 和 Sealed Classes 在 Java 17 中已正式发布

---

## 📊 LTS 版本对比

| 特性 | Java 11 | Java 17 | Java 21 |
|-----|---------|---------|---------|
| Records | ❌ | ✅ | ✅ |
| Sealed Classes | ❌ | ✅ | ✅ |
| Pattern Matching for instanceof | ❌ | ✅ | ✅ |
| Pattern Matching for switch | ❌ | 预览 | ✅ |
| Text Blocks | ❌ | ✅ | ✅ |
| Virtual Threads | ❌ | ❌ | ✅ |
| Sequenced Collections | ❌ | ❌ | ✅ |
| HTTP Client | ✅ | ✅ | ✅ |
| Switch Expressions | ❌ | ✅ | ✅ |

---

## 🔗 相关资源
- [OpenJDK 官方网站](https://openjdk.org/)
- [JEP Index](https://openjdk.org/jeps/0)
- [Oracle Java 文档](https://docs.oracle.com/en/java/)