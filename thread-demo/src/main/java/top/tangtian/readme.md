### **第一阶段：线程基础** 🌱

1. **线程的创建与启动**
    - 继承Thread类
    - 实现Runnable接口
    - 实现Callable接口（带返回值）
2. **线程生命周期**
    - NEW（新建）
    - RUNNABLE（可运行）
    - BLOCKED（阻塞）
    - WAITING（等待）
    - TIMED_WAITING（计时等待）
    - TERMINATED（终止）
3. **基本线程操作**
    - start()、run()的区别
    - sleep()、yield()
    - join()
    - interrupt()中断机制

### **第二阶段：线程协作** 🤝

1. **线程同步**
    - synchronized关键字（方法、代码块）
    - volatile关键字
    - Lock接口（ReentrantLock）
2. **线程通信**
    - wait()、notify()、notifyAll()
    - Condition接口
    - 经典问题：生产者-消费者

### **第三阶段：并发工具类** 🛠️

1. **线程池**
    - Executor框架
    - ThreadPoolExecutor详解
    - 常见线程池类型
    - 拒绝策略
2. **并发容器**
    - ConcurrentHashMap
    - CopyOnWriteArrayList
    - BlockingQueue系列
3. **同步工具**
    - CountDownLatch
    - CyclicBarrier
    - Semaphore
    - Phaser

### **第四阶段：高级特性** 🚀

1. **原子类**
    - AtomicInteger等基本类型
    - AtomicReference
    - LongAdder
2. **ForkJoin框架**
3. **CompletableFuture异步编程**

## 线程状态可视化理解

让我用一个实际场景帮你理解：

### 🎬 **电影院排队买票的比喻**

- **NEW**：你刚进电影院大厅（线程对象创建了）
- **RUNNABLE**：你在售票窗口前，可能正在买票或排队等候（可运行/运行中）
- **BLOCKED**：你想进某个放映厅，但门被锁住了，等待工作人员开门（等待synchronized锁）
- **WAITING**：你坐在休息区，无限期等朋友叫你（wait()）
- **TIMED_WAITING**：你在休息区设置了闹钟，等10分钟（sleep(10000)）
- **TERMINATED**：电影看完，你离开了电影院（线程执行完毕）

------

## 🔑 核心知识点

### **1. sleep() vs wait() 的区别（面试高频！）**

| 特性     | sleep()        | wait()                 |
| -------- | -------------- | ---------------------- |
| 所属类   | Thread类       | Object类               |
| 释放锁   | ❌ 不释放       | ✅ 释放                 |
| 使用位置 | 任何地方       | 必须在synchronized中   |
| 唤醒方式 | 时间到自动唤醒 | notify()/notifyAll()   |
| 参数     | 必须指定时间   | 可以不指定（无限等待） |

### **2. BLOCKED vs WAITING vs TIMED_WAITING**

- **BLOCKED**：被动等待，等别人释放锁
- **WAITING**：主动等待，等别人唤醒，无时间限制
- **TIMED_WAITING**：主动等待，有超时时间，时间到了自动醒
## 第三部分：基本线程操作



## 🎯 核心知识点总结

### **1. start() vs run() - 最容易犯的错误！**

java

```java
// ❌ 错误：这样不会创建新线程
thread.run();  // 只是方法调用

// ✅ 正确：创建新线程
thread.start(); // 启动新线程执行run()
```

### **2. sleep() - 休眠但不释放锁**

java

```java
synchronized(lock) {
    Thread.sleep(1000); // 睡眠时仍持有lock
}
```

### **3. join() - 等待线程结束**

java

```java
thread1.start();
thread1.join();     // 等待thread1结束
// 继续执行后续代码
```

### **4. interrupt() - 中断的三个要点**

**要点1：中断只是设置标志，不是强制停止**

java

```java
thread.interrupt(); // 只是请求中断，不是杀死线程
```

**要点2：阻塞方法会抛异常**

java

```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // sleep被中断会抛出这个异常
}
```

**要点3：运行中的线程需主动检查**

java

```java
while (!Thread.currentThread().isInterrupted()) {
    // 工作代码
}
```

------

## 🔥 典型面试题解析

### **面试题1：如何停止一个线程？**

**❌ 错误答案：**

java

```java
thread.stop(); // 已废弃，危险！
```

**✅ 正确答案：**

java

```java
// 使用interrupt()机制
thread.interrupt();

// 线程内部检查并退出
while (!Thread.currentThread().isInterrupted()) {
    // 工作...
}
```

### **面试题2：为什么不能直接调用run()？**

**答案**：调用run()只是普通方法调用，在当前线程执行，不会创建新线程。必须用start()才能创建新线程并在新线程中执行run()。

------

## 🏃 实战练习

### **练习1：下载管理器**

java

```java
// 创建一个下载管理器：
// 1. 启动3个下载线程
// 2. 主线程使用join()等待所有下载完成
// 3. 下载完成后显示"所有下载完成"
```

### **练习2：可取消的任务**

java

```java
// 创建一个可以取消的长时间任务：
// 1. 任务在循环中工作
// 2. 按下"取消"按钮时调用interrupt()
// 3. 任务检测到中断后清理资源并退出
```

### **练习3：超时控制**

java

```java
// 使用join(timeout)实现超时控制：
// 1. 启动一个任务线程
// 2. 最多等待3秒
// 3. 如果超时，中断任务并提示用户
```

------

## 📝 调试小技巧

java

```java
// 打印线程信息帮助调试
System.out.println("线程名称：" + Thread.currentThread().getName());
System.out.println("线程状态：" + thread.getState());
System.out.println("是否存活：" + thread.isAlive());
System.out.println("中断标志：" + thread.isInterrupted());
```

##  join() 原理核心解析

### **简单回答你的问题：**

**join() 能让调用线程等待，是因为它在底层使用了 `wait()` 机制！**

让我用一个比喻来解释：

## 🎭 形象比喻

想象你在餐厅等菜：

1. **你（主线程）**对服务员说："我要等这道菜做好"（调用 `chef.join()`）
2. **你进入等待区坐下**（进入 WAITING 状态，调用 `wait()`）
3. **厨师（worker线程）**在后厨做菜（执行 run() 方法）
4. **菜做好了**（run() 执行完毕）
5. **服务员喊你**（JVM 自动调用 `notifyAll()`）
6. **你被叫醒，去取菜**（线程被唤醒，join() 返回）

------

## 📊 关键原理图解

```
thread.join() 的内部实现：
┌──────────────────────────────┐
│ synchronized(thread) {       │  ← 锁住线程对象
│   while(thread.isAlive()) {  │  ← 循环检查
│     thread.wait();           │  ← 等待并释放锁
│   }                          │
│ }                            │
└──────────────────────────────┘
        ↓
    调用线程进入 WAITING 状态
        ↓
    thread 执行完 run()
        ↓
    JVM 自动调用 thread.notifyAll()
        ↓
    调用线程被唤醒，继续执行
```

------

## 💡 三个关键点

### **1. 为什么能等待？ → 因为 wait()**

java

```java
synchronized(worker) {
    worker.wait(); // 这里让调用线程等待
}
```

`wait()` 会：

- ✅ 让当前线程进入 WAITING 状态
- ✅ 释放锁
- ✅ 让出 CPU，不浪费资源

### **2. 谁来唤醒？ → JVM 自动 notify**

java

```java
// worker 的 run() 方法执行完后
// JVM 底层会自动执行：
synchronized(worker) {
    worker.notifyAll(); // 唤醒所有等待这个线程的其他线程
}
```

### **3. 为什么用 while 循环？ → 防止虚假唤醒**

java

```java
while(thread.isAlive()) { // 循环检查
    thread.wait();
}
// 唤醒后再次检查，确保线程真的结束了
```

------

## 🔬 源码级别的理解

**Thread.java 中的 join() 源码：**

java

~~~java
public final synchronized void join(long millis) 
    throws InterruptedException {
    
    if (millis == 0) {
        while (isAlive()) {    // 只要还活着
            wait(0);           // 就一直等待
        }
    }
}
```

**关键：**
1. `synchronized` → 锁住 thread 对象
2. `isAlive()` → 检查线程是否还活着
3. `wait(0)` → 无限等待，直到被 notify
4. JVM 在线程结束时自动调用 `notifyAll()`

---

## 🎯 完整执行时序
```
时间线：

T1: 主线程调用 worker.join()
    ↓
T2: 主线程获取 worker 对象的锁
    ↓
T3: 检查 worker.isAlive() → true
    ↓
T4: 调用 worker.wait()
    ↓
T5: 主线程进入 WAITING，释放锁
    ↓
    ... worker 继续执行 ...
    ↓
T6: worker 的 run() 执行完毕
    ↓
T7: JVM 自动调用 worker.notifyAll()
    ↓
T8: 主线程被唤醒，重新获取锁
    ↓
T9: 再次检查 worker.isAlive() → false
    ↓
T10: 退出 while 循环
     ↓
T11: join() 返回，主线程继续
~~~

------

## 🤔 深入思考

### **问题1：为什么不直接用 while + sleep？**

java

```java
// ❌ 这样不好
while(thread.isAlive()) {
    Thread.sleep(100); // 忙等待，浪费CPU
}
```

因为：

- 浪费 CPU 资源（每 100ms 检查一次）
- 不能精确知道线程何时结束
- 有延迟（最多 100ms）

### **问题2：多个线程可以 join 同一个线程吗？**

java

```java
// ✅ 可以！
Thread worker = new Thread(...);
worker.start();

// 三个线程都等待 worker
thread1.join(worker); // 线程1等待
thread2.join(worker); // 线程2等待
thread3.join(worker); // 线程3等待

// worker 结束时，notifyAll() 会唤醒所有三个线程
```

------

## 📝 总结

**join() 让调用线程等待的原理：**

1. **锁机制**：`synchronized(thread)` 锁住线程对象
2. **等待机制**：`thread.wait()` 让调用线程进入 WAITING
3. **通知机制**：JVM 在线程结束时自动 `notifyAll()`
4. **循环检查**：`while(isAlive())` 确保线程真正结束

**本质：join() = synchronized + wait() + JVM的自动notify**



## 第二阶段：线程协作

## 🎯 第二阶段核心知识图谱

让我用一张思维导图帮你理解这些概念的关系：

```
线程协作
├── 互斥（同一时刻只有一个线程）
│   ├── synchronized（自动）
│   │   ├── 同步方法
│   │   ├── 同步代码块
│   │   └── 对象锁 vs 类锁
│   └── Lock接口（手动）
│       ├── ReentrantLock
│       ├── tryLock()
│       └── lockInterruptibly()
│
├── 可见性（修改立即对其他线程可见）
│   └── volatile
│       ├── 禁止缓存
│       ├── 禁止重排序
│       └── ⚠️ 不保证原子性
│
└── 通信（线程间传递信息）
    ├── wait/notify（配合synchronized）
    │   ├── wait() - 释放锁并等待
    │   ├── notify() - 唤醒一个
    │   └── notifyAll() - 唤醒全部
    └── Condition（配合Lock）
        ├── await()
        ├── signal()
        └── signalAll()
```

------

## 💡 核心对比表

### **synchronized vs Lock**

| 特性         | synchronized | ReentrantLock         |
| ------------ | ------------ | --------------------- |
| **锁获取**   | 自动         | 手动lock()            |
| **锁释放**   | 自动         | 手动unlock()          |
| **中断**     | ❌ 不可中断   | ✅ lockInterruptibly() |
| **超时**     | ❌ 不支持     | ✅ tryLock(timeout)    |
| **公平性**   | ❌ 非公平     | ✅ 可选公平锁          |
| **条件变量** | ❌ 只有一个   | ✅ 多个Condition       |
| **性能**     | 好           | 更好                  |
| **使用难度** | 简单         | 需要注意释放          |

**选择建议：**

- 简单场景用 `synchronized`
- 需要高级功能用 `Lock`

------

### **sleep() vs wait()**

| 特性         | sleep()  | wait()           |
| ------------ | -------- | ---------------- |
| **所属类**   | Thread   | Object           |
| **释放锁**   | ❌ 不释放 | ✅ 释放           |
| **使用位置** | 任何地方 | synchronized块内 |
| **唤醒方式** | 时间到   | notify/notifyAll |
| **用途**     | 延迟执行 | 线程通信         |

------

### **volatile vs synchronized**

| 特性       | volatile   | synchronized |
| ---------- | ---------- | ------------ |
| **原子性** | ❌ 不保证   | ✅ 保证       |
| **可见性** | ✅ 保证     | ✅ 保证       |
| **有序性** | ✅ 禁止重排 | ✅ 保证       |
| **开销**   | 小         | 大           |
| **适用**   | 状态标志   | 复合操作     |

java

~~~java
// volatile 适用场景
private volatile boolean running = true;

// synchronized 适用场景
private int count = 0;
public synchronized void increment() {
    count++; // 需要原子性
}
```

---

## 🔥 经典面试题详解

### **Q1: synchronized 的实现原理？**

**答案：**
- 基于 **Monitor（监视器）** 机制
- 每个对象都有一个监视器锁（Monitor Lock）
- 同步代码块使用 `monitorenter` 和 `monitorexit` 指令
- 同步方法使用 `ACC_SYNCHRONIZED` 标记
```
对象头
├── Mark Word（存储锁信息）
│   ├── 无锁
│   ├── 偏向锁
│   ├── 轻量级锁
│   └── 重量级锁
└── Class Pointer
~~~

------

### **Q2: 什么是虚假唤醒？为什么用while不用if？**

**虚假唤醒**：线程在没有被 notify 的情况下被唤醒。

java

```java
// ❌ 错误：用if
synchronized(obj) {
    if(!condition) {
        obj.wait();
    }
    // 如果虚假唤醒，条件仍不满足，会错误执行
}

// ✅ 正确：用while
synchronized(obj) {
    while(!condition) { // 醒来后重新检查
        obj.wait();
    }
    // 确保条件满足才执行
}
```

------

### **Q3: volatile 能保证原子性吗？**

**不能！**

java

```java
private volatile int count = 0;

// ❌ count++ 不是原子操作
public void increment() {
    count++; // 分三步：读取、加1、写入
}

// ✅ 需要用synchronized或AtomicInteger
public synchronized void increment() {
    count++;
}
```

------

### **Q4: 生产者-消费者的三种实现方式？**

**方式1：synchronized + wait/notify**

java

```java
synchronized(buffer) {
    while(buffer.isFull()) {
        buffer.wait();
    }
    buffer.add(item);
    buffer.notify();
}
```

**方式2：Lock + Condition**

java

```java
lock.lock();
try {
    while(buffer.isFull()) {
        notFull.await();
    }
    buffer.add(item);
    notEmpty.signal();
} finally {
    lock.unlock();
}
```

**方式3：BlockingQueue（最简单）**

java

```java
BlockingQueue<Item> queue = new ArrayBlockingQueue<>(10);
queue.put(item); // 自动阻塞
Item item = queue.take(); // 自动阻塞
```

------

## 🏃 实战练习

### **练习1：账户转账（synchronized）**

java

```java
// 实现安全的转账方法，避免死锁
class Bank {
    public void transfer(Account from, Account to, int amount) {
        // 你的代码...
    }
}
```

### **练习2：停止标志（volatile）**

java

```java
// 实现可以被外部停止的任务
class Task implements Runnable {
    // 你的代码...
}
```

### **练习3：阻塞队列（wait/notify）**

java

```java
// 手动实现一个固定大小的阻塞队列
class MyBlockingQueue<T> {
    public void put(T item) throws InterruptedException {
        // 你的代码...
    }
    
    public T take() throws InterruptedException {
        // 你的代码...
    }
}
```

------

## 📚 学习建议

1. **先理解概念**：搞清楚每个机制解决什么问题
2. **运行代码**：观察输出，理解执行流程
3. **修改代码**：故意制造问题，观察现象
4. **做练习**：自己动手实现案例



## 🎯 第三阶段知识图谱

```
并发工具类
│
├── 线程池（复用线程，管理任务）
│   ├── ThreadPoolExecutor（核心）
│   ├── 参数配置（核心数、最大数、队列）
│   └── 拒绝策略（4种）
│
├── 同步工具（协调线程执行）
│   ├── CountDownLatch（一个等多个）
│   ├── CyclicBarrier（多个互等，可重用）
│   └── Semaphore（限制并发数）
│
├── 并发容器（线程安全集合）
│   ├── ConcurrentHashMap（高性能Map）
│   ├── CopyOnWriteArrayList（读多写少）
│   └── BlockingQueue（生产者消费者）
│
└── 原子类（无锁操作）
    ├── AtomicInteger/Long（基本类型）
    ├── AtomicReference（对象引用）
    └── LongAdder（高性能计数）
```

------

## 🔥 核心对比

### **三大同步工具对比**

| 工具               | 场景      | 计数方向 | 是否重用 | 典型用法         |
| ------------------ | --------- | -------- | -------- | ---------------- |
| **CountDownLatch** | 一个等N个 | 递减到0  | ❌ 一次性 | 等待所有任务完成 |
| **CyclicBarrier**  | N个互等   | 递增到N  | ✅ 可循环 | 分阶段计算       |
| **Semaphore**      | 限制并发  | 许可证   | ✅ 可重用 | 限流、资源池     |

### **使用示例对比**

java

~~~java
// CountDownLatch - 主线程等待3个工作线程
CountDownLatch latch = new CountDownLatch(3);
// 工作线程：
latch.countDown();
// 主线程：
latch.await();

// CyclicBarrier - 3个线程互相等待
CyclicBarrier barrier = new CyclicBarrier(3);
// 每个线程：
barrier.await(); // 等待其他2个线程

// Semaphore - 限制最多3个并发
Semaphore semaphore = new Semaphore(3);
semaphore.acquire(); // 获取许可
// 执行任务
semaphore.release(); // 释放许可
```

---

## 💡 线程池核心原理图
```
任务提交流程：
                  
提交任务
  ↓
线程数 < corePoolSize？
  ├─ 是 → 创建核心线程
  └─ 否 ↓
      
队列未满？
  ├─ 是 → 放入队列
  └─ 否 ↓
      
线程数 < maximumPoolSize？
  ├─ 是 → 创建临时线程
  └─ 否 → 执行拒绝策略
            ├─ AbortPolicy（抛异常）
            ├─ CallerRunsPolicy（调用者执行）
            ├─ DiscardPolicy（丢弃）
            └─ DiscardOldestPolicy（丢弃最老）
~~~

------

## 🎓 关键参数配置建议

### **CPU密集型任务**

java

```java
int coreSize = Runtime.getRuntime().availableProcessors() + 1;
// 核心数+1，充分利用CPU
```

### **IO密集型任务**

java

```java
int coreSize = Runtime.getRuntime().availableProcessors() * 2;
// 核心数*2，因为线程经常阻塞
```

### **混合型任务**

java

```java
// 需要根据实际测试调整
// 一般设置为 CPU核心数 到 CPU核心数*2 之间
```

------

## 🔥 高频面试题

### **Q1: 为什么不建议用Executors创建线程池？**

**答案：**

java

```java
// ❌ 问题代码
ExecutorService pool = Executors.newFixedThreadPool(5);
// 内部使用 LinkedBlockingQueue，默认容量Integer.MAX_VALUE
// 可能导致内存溢出（OOM）

// ✅ 推荐方式
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    5, 10,
    60L, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100), // 明确队列大小
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

**原因：**

- `newFixedThreadPool` 和 `newSingleThreadExecutor` 队列无界，可能OOM
- `newCachedThreadPool` 最大线程数无限，可能创建过多线程

------

### **Q2: CountDownLatch和CyclicBarrier的区别？**

| 特性     | CountDownLatch | CyclicBarrier        |
| -------- | -------------- | -------------------- |
| **用途** | 一个等多个     | 多个互相等           |
| **计数** | 递减           | 递增                 |
| **重用** | 不可重用       | 可重用               |
| **回调** | 无             | 有（所有到达时执行） |

**场景举例：**

- CountDownLatch: 主线程等待所有下载任务完成
- CyclicBarrier: 多个线程分阶段计算，每阶段都要等齐

------

### **Q3: 什么是CAS？原子类如何保证线程安全？**

**CAS（Compare-And-Swap）：**

java

```java
// 伪代码
boolean compareAndSet(expected, newValue) {
    if (currentValue == expected) {
        currentValue = newValue;
        return true;
    }
    return false;
}
```

**特点：**

- 无锁算法，硬件支持
- 乐观锁思想
- 适合冲突较少的场景

**问题：**

- ABA问题（可用版本号解决）
- 只能保证单个变量
- 自旋开销（冲突多时性能差）

------

### **Q4: BlockingQueue的put和offer有什么区别？**

java

```java
BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

// put() - 队列满时阻塞等待
queue.put(item);

// offer() - 队列满时返回false
boolean success = queue.offer(item);

// offer(timeout) - 队列满时等待指定时间
boolean success = queue.offer(item, 3, TimeUnit.SECONDS);
```

**选择：**

- 必须放入 → `put()`
- 可以失败 → `offer()`
- 超时控制 → `offer(timeout)`

------

## 🏃 实战练习

### **练习1：实现一个固定速率的任务执行器**

java

```java
// 需求：每秒最多执行10个任务
// 提示：使用Semaphore + ScheduledExecutorService
```

### **练习2：实现并行下载文件**

java

```java
// 需求：
// 1. 下载10个文件
// 2. 使用线程池（最多5个并发）
// 3. 所有文件下载完成后打印"全部完成"
// 提示：使用ThreadPoolExecutor + CountDownLatch
```

### **练习3：实现简单的缓存系统**

java

```java
// 需求：
// 1. 线程安全的缓存
// 2. 支持过期时间
// 3. 高并发读写
// 提示：使用ConcurrentHashMap
```

------

## 📚 学习路径建议

1. **先理解单个工具**：每个工具解决什么问题
2. **对比学习**：相似工具的区别（如CountDownLatch vs CyclicBarrier）
3. **实际应用**：结合真实场景练习
4. **性能对比**：不同方案的性能差异
5. **源码阅读**：理解底层实现原理

------

## 🎉 第三阶段总结

你现在已经掌握了：

✅ **线程池**：复用线程，管理任务执行 ✅ **同步工具**：协调多线程协作 ✅ **并发容器**：线程安全的集合 ✅ **原子类**：无锁的高性能操作

这些是Java并发编程的精华工具！

