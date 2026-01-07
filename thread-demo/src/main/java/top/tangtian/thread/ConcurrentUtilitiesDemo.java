package top.tangtian.thread;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author tangtian
 * @date 2025-12-22 10:10
 * 第三阶段：并发工具类完整教程
 * 包括：
 * 1. 线程池 (ThreadPoolExecutor)
 * 2. CountDownLatch（倒计时门栓）
 * 3. CyclicBarrier（循环栅栏）
 * 4. Semaphore（信号量）
 * 5. 并发容器
 * 6. 原子类
 */
public class ConcurrentUtilitiesDemo {
	/**
	 *
	 /* ========== 知识点总结 ==========

	 一、线程池（ThreadPoolExecutor）
	 ────────────────────────────────
	 核心参数：
	 • corePoolSize - 核心线程数
	 • maximumPoolSize - 最大线程数
	 • keepAliveTime - 空闲线程存活时间
	 • workQueue - 任务队列
	 • handler - 拒绝策略

	 任务提交流程：
	 1. 线程数 < core → 创建新线程
	 2. 线程数 >= core → 放入队列
	 3. 队列满 & 线程 < max → 创建新线程
	 4. 队列满 & 线程 >= max → 拒绝策略

	 拒绝策略：
	 • AbortPolicy - 抛异常（默认）
	 • CallerRunsPolicy - 调用者执行
	 • DiscardPolicy - 丢弃
	 • DiscardOldestPolicy - 丢弃最老


	 二、CountDownLatch（倒计时门栓）
	 ────────────────────────────────
	 场景：一个线程等待N个线程完成

	 用法：
	 CountDownLatch latch = new CountDownLatch(N);
	 latch.countDown(); // 每个线程完成时调用
	 latch.await();     // 主线程等待

	 特点：
	 • 计数器递减
	 • 一次性，不可重置
	 • 主要用于"等待所有任务完成"


	 三、CyclicBarrier（循环栅栏）
	 ────────────────────────────────
	 场景：N个线程互相等待，都到达后一起执行

	 用法：
	 CyclicBarrier barrier = new CyclicBarrier(N, action);
	 barrier.await(); // 每个线程调用，等待其他线程

	 特点：
	 • 计数器递增
	 • 可重用（循环）
	 • 可设置回调动作
	 • 主要用于"分阶段协作"


	 四、Semaphore（信号量）
	 ────────────────────────────────
	 场景：限制同时访问资源的线程数

	 用法：
	 Semaphore sem = new Semaphore(N); // N个许可证
	 sem.acquire();  // 获取许可（可能阻塞）
	 sem.release();  // 释放许可

	 应用：
	 • 限流（QPS控制）
	 • 资源池（连接池）
	 • 控制并发数


	 五、并发容器
	 ────────────────────────────────
	 ConcurrentHashMap：
	 • 线程安全的HashMap
	 • 分段锁，高性能
	 • 适合通用场景

	 CopyOnWriteArrayList：
	 • 写时复制
	 • 读多写少场景
	 • 迭代时可修改

	 BlockingQueue：
	 • 阻塞队列
	 • 自动实现生产者-消费者
	 • put()阻塞，take()阻塞


	 六、原子类（Atomic）
	 ────────────────────────────────
	 原理：CAS（Compare-And-Swap）无锁算法

	 常用类：
	 • AtomicInteger/Long - 基本类型
	 • AtomicReference - 对象引用
	 • LongAdder - 高性能累加器

	 优势：
	 • 无锁，性能高
	 • 避免synchronized开销
	 • 适合简单的原子操作


	 ========== 选择指南 ==========

	 场景1：多线程累加计数
	 ✅ AtomicInteger（简单）
	 ✅ LongAdder（高并发）
	 ❌ synchronized（开销大）

	 场景2：控制并发数量
	 ✅ Semaphore（限流、资源池）
	 ✅ 线程池（任务执行）

	 场景3：等待多个任务完成
	 ✅ CountDownLatch（一次性）
	 ✅ CyclicBarrier（循环使用）

	 场景4：生产者-消费者
	 ✅ BlockingQueue（最简单）
	 ⭕ wait/notify（手动实现）
	 ⭕ Lock+Condition（灵活）

	 场景5：线程安全的Map
	 ✅ ConcurrentHashMap（通用）
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("========== 第三阶段：并发工具类 ==========\n");

		// 第一部分：线程池
		part1_ThreadPool();
		Thread.sleep(3000);

		// 第二部分：CountDownLatch
		part2_CountDownLatch();
		Thread.sleep(2000);

		// 第三部分：CyclicBarrier
		part3_CyclicBarrier();
		Thread.sleep(2000);

		// 第四部分：Semaphore
		part4_Semaphore();
		Thread.sleep(2000);

		// 第五部分：并发容器
		part5_ConcurrentCollections();
		Thread.sleep(2000);

		// 第六部分：原子类
		part6_AtomicClasses();
	}

	// ==================== 第一部分：线程池 ====================
	static void part1_ThreadPool() throws Exception {
		System.out.println("=== 第一部分：线程池 ThreadPool ===\n");

		// 1.1 为什么需要线程池
		explainWhyThreadPool();
		Thread.sleep(1000);

		// 1.2 线程池的创建
		demonstrateThreadPoolCreation();
		Thread.sleep(1000);

		// 1.3 线程池参数详解
		explainThreadPoolParameters();
		Thread.sleep(1000);

		// 1.4 拒绝策略
		demonstrateRejectionPolicy();
	}

	static void explainWhyThreadPool() {
		System.out.println("【1.1 为什么需要线程池？】\n");

		System.out.println("问题1：频繁创建销毁线程的开销");
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			new Thread(() -> {
				// 简单任务
			}).start();
		}
		long end = System.currentTimeMillis();
		System.out.println("  创建1000个线程耗时：" + (end - start) + "ms");

		System.out.println("\n问题2：无限制创建线程可能导致OOM");
		System.out.println("  每个线程占用约1MB内存");
		System.out.println("  创建10000个线程 = 10GB内存！");

		System.out.println("\n线程池的优势：");
		System.out.println("  ✅ 复用线程，减少创建销毁开销");
		System.out.println("  ✅ 控制并发数量，避免资源耗尽");
		System.out.println("  ✅ 统一管理，方便监控和调优");
		System.out.println("  ✅ 提供任务队列，平滑处理突发流量\n");
	}

	static void demonstrateThreadPoolCreation() throws Exception {
		System.out.println("【1.2 线程池的创建】\n");

		// 方式1：Executors工厂方法（不推荐生产环境）
		System.out.println("方式1：Executors工厂方法");

		ExecutorService fixedPool = Executors.newFixedThreadPool(3);
		System.out.println("  newFixedThreadPool(3) - 固定3个线程");

		ExecutorService cachedPool = Executors.newCachedThreadPool();
		System.out.println("  newCachedThreadPool() - 动态创建线程");

		ExecutorService singlePool = Executors.newSingleThreadExecutor();
		System.out.println("  newSingleThreadExecutor() - 单线程");

		ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);
		System.out.println("  newScheduledThreadPool(2) - 定时任务\n");

		// 方式2：手动创建ThreadPoolExecutor（推荐）
		System.out.println("方式2：手动创建ThreadPoolExecutor（推荐）");
		ThreadPoolExecutor customPool = new ThreadPoolExecutor(
				2,                          // 核心线程数
				5,                          // 最大线程数
				60L,                        // 空闲线程存活时间
				TimeUnit.SECONDS,           // 时间单位
				new ArrayBlockingQueue<>(10), // 任务队列
				Executors.defaultThreadFactory(), // 线程工厂
				new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
		);

		System.out.println("  核心线程数：2");
		System.out.println("  最大线程数：5");
		System.out.println("  队列容量：10");
		System.out.println("  拒绝策略：AbortPolicy\n");

		// 演示使用
		System.out.println("提交10个任务：");
		for (int i = 1; i <= 10; i++) {
			final int taskId = i;
			customPool.execute(() -> {
				System.out.println("  任务" + taskId + " 由 " +
						Thread.currentThread().getName() + " 执行");
				sleep(500);
			});
		}

		customPool.shutdown();
		customPool.awaitTermination(10, TimeUnit.SECONDS);

		// 关闭其他线程池
		fixedPool.shutdown();
		cachedPool.shutdown();
		singlePool.shutdown();
		scheduledPool.shutdown();

		System.out.println();
	}

	static void explainThreadPoolParameters() {
		System.out.println("【1.3 线程池参数详解】\n");

		System.out.println("核心参数说明：");
		System.out.println("┌─────────────────┬────────────────────────┐");
		System.out.println("│ 参数            │ 说明                   │");
		System.out.println("├─────────────────┼────────────────────────┤");
		System.out.println("│ corePoolSize    │ 核心线程数（常驻）     │");
		System.out.println("│ maximumPoolSize │ 最大线程数             │");
		System.out.println("│ keepAliveTime   │ 空闲线程存活时间       │");
		System.out.println("│ workQueue       │ 任务队列               │");
		System.out.println("│ threadFactory   │ 线程工厂               │");
		System.out.println("│ handler         │ 拒绝策略               │");
		System.out.println("└─────────────────┴────────────────────────┘");

		System.out.println("\n任务提交流程：");
		System.out.println("  1. 线程数 < corePoolSize");
		System.out.println("     → 创建新线程执行任务");
		System.out.println("  2. 线程数 >= corePoolSize");
		System.out.println("     → 任务放入队列");
		System.out.println("  3. 队列满 && 线程数 < maximumPoolSize");
		System.out.println("     → 创建新线程执行任务");
		System.out.println("  4. 队列满 && 线程数 >= maximumPoolSize");
		System.out.println("     → 执行拒绝策略");

		System.out.println("\n常见队列类型：");
		System.out.println("  • ArrayBlockingQueue   - 有界队列（推荐）");
		System.out.println("  • LinkedBlockingQueue  - 无界队列（谨慎）");
		System.out.println("  • SynchronousQueue     - 不存储任务，直接交给线程");
		System.out.println("  • PriorityBlockingQueue- 优先级队列\n");
	}

	static void demonstrateRejectionPolicy() throws Exception {
		System.out.println("【1.4 拒绝策略】\n");

		System.out.println("四种拒绝策略：");
		System.out.println("  1. AbortPolicy（默认）- 抛出异常");
		System.out.println("  2. CallerRunsPolicy - 调用者执行");
		System.out.println("  3. DiscardPolicy - 直接丢弃");
		System.out.println("  4. DiscardOldestPolicy - 丢弃最老任务\n");

		// 演示 CallerRunsPolicy
		System.out.println("演示 CallerRunsPolicy：");
		ThreadPoolExecutor pool = new ThreadPoolExecutor(
				1, 1,
				0L, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<>(1),
				new ThreadPoolExecutor.CallerRunsPolicy()
		);

		for (int i = 1; i <= 3; i++) {
			final int taskId = i;
			pool.execute(() -> {
				System.out.println("  任务" + taskId + " 由 " +
						Thread.currentThread().getName() + " 执行");
				sleep(1000);
			});
		}

		pool.shutdown();
		pool.awaitTermination(5, TimeUnit.SECONDS);
		System.out.println("  → 任务3由main线程执行（队列满时调用者执行）\n");
	}

	// ==================== 第二部分：CountDownLatch ====================
	static void part2_CountDownLatch() throws Exception {
		System.out.println("=== 第二部分：CountDownLatch（倒计时门栓）===\n");

		System.out.println("【场景：等待所有线程完成后再继续】");
		System.out.println("类比：赛跑 - 所有选手准备好才能发令枪响\n");

		// 案例：数据分片处理
		System.out.println("案例：并行处理数据，等待所有分片完成\n");

		int shardCount = 5;
		CountDownLatch latch = new CountDownLatch(shardCount);

		System.out.println("开始处理" + shardCount + "个数据分片...");

		for (int i = 1; i <= shardCount; i++) {
			final int shardId = i;
			new Thread(() -> {
				System.out.println("  [分片" + shardId + "] 开始处理");
				sleep(1000 + new Random().nextInt(1000));
				System.out.println("  [分片" + shardId + "] 处理完成");
				latch.countDown(); // 计数器减1
			}).start();
		}

		System.out.println("\n主线程等待所有分片完成...");
		latch.await(); // 等待计数器归零
		System.out.println("✅ 所有分片处理完成，开始汇总结果\n");

		System.out.println("CountDownLatch关键点：");
		System.out.println("  • new CountDownLatch(n) - 设置计数器");
		System.out.println("  • countDown() - 计数减1");
		System.out.println("  • await() - 等待计数归零");
		System.out.println("  • 一次性使用，不能重置\n");
	}

	// ==================== 第三部分：CyclicBarrier ====================
	static void part3_CyclicBarrier() throws Exception {
		System.out.println("=== 第三部分：CyclicBarrier（循环栅栏）===\n");

		System.out.println("【场景：多个线程互相等待，到齐后一起执行】");
		System.out.println("类比：旅游团 - 所有人到齐才能上车出发\n");

		// 案例：多线程计算，分阶段汇总
		System.out.println("案例：3个线程分别计算，每轮计算完一起汇总\n");

		final int threadCount = 3;
		CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
			System.out.println("    → 所有线程到达屏障，执行汇总操作\n");
		});

		for (int i = 1; i <= threadCount; i++) {
			final int workerId = i;
			new Thread(() -> {
				try {
					for (int round = 1; round <= 2; round++) {
						System.out.println("  [工作者" + workerId + "] 第" + round + "轮计算中...");
						sleep(1000);
						System.out.println("  [工作者" + workerId + "] 第" + round + "轮完成，等待其他人");
						barrier.await(); // 等待其他线程
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}

		Thread.sleep(6000);

		System.out.println("CyclicBarrier vs CountDownLatch：");
		System.out.println("┌──────────────┬──────────────┬──────────────┐");
		System.out.println("│ 特性         │ CountDownLatch│ CyclicBarrier│");
		System.out.println("├──────────────┼──────────────┼──────────────┤");
		System.out.println("│ 计数方向     │ 递减到0      │ 递增到N      │");
		System.out.println("│ 是否可重用   │ 否           │ 是（循环）   │");
		System.out.println("│ 回调         │ 无           │ 有           │");
		System.out.println("│ 使用场景     │ 一个等多个   │ 多个互等     │");
		System.out.println("└──────────────┴──────────────┴──────────────┘\n");
	}

	// ==================== 第四部分：Semaphore ====================
	static void part4_Semaphore() throws Exception {
		System.out.println("=== 第四部分：Semaphore（信号量）===\n");

		System.out.println("【场景：限制同时访问资源的线程数量】");
		System.out.println("类比：停车场 - 只有3个车位，满了就等待\n");

		// 案例：数据库连接池（限制并发连接数）
		System.out.println("案例：模拟数据库连接池（最多3个并发连接）\n");

		Semaphore semaphore = new Semaphore(3); // 3个许可证

		for (int i = 1; i <= 8; i++) {
			final int userId = i;
			new Thread(() -> {
				try {
					System.out.println("  [用户" + userId + "] 请求连接...");
					semaphore.acquire(); // 获取许可证（可能阻塞）

					System.out.println("  [用户" + userId + "] 获得连接，执行查询");
					sleep(1000);
					System.out.println("  [用户" + userId + "] 释放连接");

					semaphore.release(); // 释放许可证
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();

			Thread.sleep(200);
		}

		Thread.sleep(5000);

		System.out.println("\nSemaphore关键API：");
		System.out.println("  • new Semaphore(n) - 初始化n个许可证");
		System.out.println("  • acquire() - 获取许可证（阻塞）");
		System.out.println("  • tryAcquire() - 尝试获取（不阻塞）");
		System.out.println("  • release() - 释放许可证");
		System.out.println("\n应用场景：");
		System.out.println("  • 限流（QPS控制）");
		System.out.println("  • 资源池（数据库连接、线程池）");
		System.out.println("  • 控制并发访问数量\n");
	}

	// ==================== 第五部分：并发容器 ====================
	static void part5_ConcurrentCollections() throws Exception {
		System.out.println("=== 第五部分：并发容器 ===\n");

		// 5.1 ConcurrentHashMap
		System.out.println("【5.1 ConcurrentHashMap】");
		System.out.println("优势：线程安全且高性能（分段锁）\n");

		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

		CountDownLatch latch = new CountDownLatch(5);
		for (int i = 0; i < 5; i++) {
			new Thread(() -> {
				for (int j = 0; j < 1000; j++) {
					map.merge("count", 1, Integer::sum);
				}
				latch.countDown();
			}).start();
		}

		latch.await();
		System.out.println("  5个线程各累加1000次");
		System.out.println("  期望：5000，实际：" + map.get("count") + " ✅\n");

		// 5.2 CopyOnWriteArrayList
		System.out.println("【5.2 CopyOnWriteArrayList】");
		System.out.println("特点：写时复制，读多写少场景\n");

		CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
		list.add("A");
		list.add("B");
		list.add("C");

		// 迭代时可以修改，不会抛ConcurrentModificationException
		for (String s : list) {
			System.out.println("  读取：" + s);
			if (s.equals("B")) {
				list.add("D"); // 写操作
			}
		}
		System.out.println("  最终列表：" + list + "\n");

		// 5.3 BlockingQueue
		System.out.println("【5.3 BlockingQueue（阻塞队列）】");
		System.out.println("自动实现生产者-消费者模式\n");

		BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

		// 生产者
		new Thread(() -> {
			try {
				for (int i = 1; i <= 10; i++) {
					queue.put(i); // 队列满时阻塞
					System.out.println("  [生产者] 生产：" + i);
					Thread.sleep(200);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		// 消费者
		new Thread(() -> {
			try {
				for (int i = 1; i <= 10; i++) {
					Integer item = queue.take(); // 队列空时阻塞
					System.out.println("  [消费者] 消费：" + item);
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		Thread.sleep(6000);

		System.out.println("\n并发容器对比：");
		System.out.println("┌─────────────────────┬──────────────┬─────────┐");
		System.out.println("│ 容器                │ 适用场景     │ 性能    │");
		System.out.println("├─────────────────────┼──────────────┼─────────┤");
		System.out.println("│ ConcurrentHashMap   │ 通用Map      │ 高      │");
		System.out.println("│ CopyOnWriteArrayList│ 读多写少     │ 读快写慢│");
		System.out.println("│ BlockingQueue       │ 生产者消费者 │ 中      │");
		System.out.println("└─────────────────────┴──────────────┴─────────┘\n");
	}

	// ==================== 第六部分：原子类 ====================
	static void part6_AtomicClasses() throws Exception {
		System.out.println("=== 第六部分：原子类（Atomic）===\n");

		System.out.println("【原理：CAS（Compare-And-Swap）无锁算法】\n");

		// 6.1 AtomicInteger
		System.out.println("【6.1 AtomicInteger】");
		AtomicInteger counter = new AtomicInteger(0);

		CountDownLatch latch = new CountDownLatch(5);
		for (int i = 0; i < 5; i++) {
			new Thread(() -> {
				for (int j = 0; j < 1000; j++) {
					counter.incrementAndGet(); // 原子操作
				}
				latch.countDown();
			}).start();
		}

		latch.await();
		System.out.println("  5个线程各累加1000次");
		System.out.println("  结果：" + counter.get() + " ✅ 无需synchronized\n");

		// 6.2 AtomicReference
		System.out.println("【6.2 AtomicReference（对象原子更新）】");

		class User {
			String name;
			int age;
			User(String name, int age) {
				this.name = name;
				this.age = age;
			}
			@Override
			public String toString() {
				return name + "(" + age + ")";
			}
		}

		AtomicReference<User> userRef = new AtomicReference<>(new User("Alice", 20));
		System.out.println("  初始：" + userRef.get());

		// CAS更新
		User oldUser = userRef.get();
		User newUser = new User("Bob", 25);
		boolean success = userRef.compareAndSet(oldUser, newUser);

		System.out.println("  更新成功：" + success);
		System.out.println("  当前：" + userRef.get() + "\n");

		// 6.3 LongAdder（高性能计数器）
		System.out.println("【6.3 LongAdder（比AtomicLong更快）】");
		System.out.println("原理：分段累加，减少CAS竞争\n");

		LongAdder adder = new LongAdder();

		long start = System.currentTimeMillis();
		CountDownLatch latch2 = new CountDownLatch(10);

		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				for (int j = 0; j < 100000; j++) {
					adder.increment();
				}
				latch2.countDown();
			}).start();
		}

		latch2.await();
		long end = System.currentTimeMillis();

		System.out.println("  10个线程各累加100000次");
		System.out.println("  结果：" + adder.sum());
		System.out.println("  耗时：" + (end - start) + "ms\n");

		System.out.println("原子类总结：");
		System.out.println("  • AtomicInteger/Long - 基本类型原子操作");
		System.out.println("  • AtomicReference - 对象引用原子操作");
		System.out.println("  • LongAdder - 高并发累加（性能最好）");
		System.out.println("  • 优势：无锁、高性能、简单易用");
	}

	// 辅助方法
	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}



