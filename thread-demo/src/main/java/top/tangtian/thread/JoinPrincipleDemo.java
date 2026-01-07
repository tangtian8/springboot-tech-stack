package top.tangtian.thread;

/**
 * @author tangtian
 * @date 2025-12-22 09:46
 * join()原理深度解析
 *
 * 核心原理：join()底层使用wait()实现
 * 当线程结束时，JVM会自动调用notifyAll()唤醒等待的线程
 */
public class JoinPrincipleDemo {
	/* ========== join()原理核心总结 ==========

	1. join()本质 = synchronized + wait()
	   ----------------------------------------
	   等价代码：
	   synchronized(thread) {
		   while(thread.isAlive()) {
			   thread.wait(0);
		   }
	   }

	2. 为什么可以让调用线程等待？
	   ----------------------------------------
	   - wait()会让调用线程进入WAITING状态
	   - 释放锁，让出CPU
	   - 等待被notify/notifyAll唤醒

	3. 谁来唤醒等待的线程？
	   ----------------------------------------
	   - JVM底层实现
	   - 当线程的run()方法执行完毕时
	   - JVM自动调用该线程对象的notifyAll()
	   - 唤醒所有调用了join()的线程

	4. 为什么要用while循环检查isAlive()？
	   ----------------------------------------
	   - 防止虚假唤醒（spurious wakeup）
	   - wait()可能在没有notify时被唤醒
	   - 循环确保只有线程真正结束才退出

	5. 为什么多个线程可以join同一个线程？
	   ----------------------------------------
	   - 多个线程可以同时wait()同一个对象
	   - notifyAll()会唤醒所有等待的线程
	   - 每个线程都会收到通知并继续执行


	========== 图解join()执行流程 ==========

	主线程                     Worker线程
	  |                           |
	  | worker.start()            |
	  |-------------------------->|
	  |                          运行中...
	  | worker.join()             |
	  |   ↓                       |
	  | synchronized(worker)      |
	  |   ↓                       |
	  | while(isAlive()) {        |
	  |   worker.wait()           |
	  |   ↓                       |
	  | [WAITING状态]             |
	  |   ...                    ...
	  |                      run()结束
	  |                           |
	  |                     JVM: notifyAll()
	  |   ↑                       |
	  | 被唤醒 ←------------------┘
	  |   ↓
	  | 检查isAlive()
	  | 发现已死亡
	  |   ↓
	  | 退出while
	  |   ↓
	  | 释放锁，join()返回
	  |   ↓
	  | 继续执行


	========== 常见误解 ==========

	❌ 误解1：join()会"阻塞"线程
	✅ 正确：join()让线程进入WAITING状态，不是BLOCKED
			 WAITING可以被中断，BLOCKED不能

	❌ 误解2：join()是忙等待（busy-wait）
	✅ 正确：join()使用wait()，线程让出CPU，不消耗资源

	❌ 误解3：只有主线程可以join其他线程
	✅ 正确：任何线程都可以join任何线程

	❌ 误解4：被join的线程会知道有人在等它
	✅ 正确：被join的线程无感知，JVM自动处理通知


	========== 与其他等待机制对比 ==========

	方法              原理           释放锁    响应中断   用途
	----------------------------------------------------------------
	join()           wait()机制      是       是        等待线程结束
	Thread.sleep()   操作系统调度     否       是        延迟执行
	Object.wait()    监视器机制      是       是        线程间通信
	LockSupport.park() 许可机制      -        是        底层同步原语


	========== 面试高频问题 ==========

	Q1: join()的实现原理？
	A: 使用synchronized和wait()，线程结束时JVM调用notifyAll()唤醒

	Q2: 为什么join()要加synchronized？
	A: 因为要调用wait()，而wait()必须在同步块中调用

	Q3: 如果不调用join()会怎样？
	A: 主线程和子线程并发执行，无法保证执行顺序

	Q4: join()期间线程状态是什么？
	A: WAITING（无限等待）或TIMED_WAITING（join(timeout)）

	Q5: join()可以被中断吗？
	A: 可以，会抛出InterruptedException

	*/
	public static void main(String[] args) throws InterruptedException {
		// 演示1：join()的表面行为
		demonstrateJoinBehavior();

		Thread.sleep(2000);

		// 演示2：join()的底层原理（模拟实现）
		demonstrateJoinPrinciple();

		Thread.sleep(2000);

		// 演示3：多个线程join同一个线程
		demonstrateMultipleJoin();

		Thread.sleep(2000);

		// 演示4：查看join()源码逻辑
		explainJoinSourceCode();
	}

	// ========== 演示1：join()的表面行为 ==========
	static void demonstrateJoinBehavior() throws InterruptedException {
		System.out.println("=== 演示1：join()的表面行为 ===\n");

		Thread worker = new Thread(() -> {
			System.out.println("  [Worker] 开始工作...");
			sleep(2000);
			System.out.println("  [Worker] 工作完成");
		}, "WorkerThread");

		System.out.println("[Main] 启动工作线程");
		worker.start();

		System.out.println("[Main] 调用worker.join()，主线程等待...");
		worker.join(); // 主线程在这里等待

		System.out.println("[Main] worker结束，主线程继续执行");
		System.out.println();
	}

	// ========== 演示2：join()的底层原理（手动实现）==========
	static void demonstrateJoinPrinciple() throws InterruptedException {
		System.out.println("=== 演示2：join()的底层原理（手动模拟）===\n");

		System.out.println("join()源码等价于：");
		System.out.println("synchronized(thread) {");
		System.out.println("    while(thread.isAlive()) {");
		System.out.println("        thread.wait(0); // 等待线程对象");
		System.out.println("    }");
		System.out.println("}");
		System.out.println();

		Thread worker = new Thread(() -> {
			System.out.println("  [Worker] 开始工作...");
			sleep(2000);
			System.out.println("  [Worker] 工作完成");
			// JVM在这里会自动调用 notifyAll() 唤醒等待的线程！
		}, "WorkerThread");

		worker.start();

		// 手动实现join()的逻辑
		System.out.println("[Main] 手动实现join()逻辑：");
		synchronized (worker) { // 锁住线程对象
			while (worker.isAlive()) { // 当线程还活着
				System.out.println("[Main] 线程还活着，调用wait()等待...");
				worker.wait(); // 等待（释放锁）
				System.out.println("[Main] 被唤醒了！检查线程是否结束...");
			}
		}

		System.out.println("[Main] 线程已结束，继续执行");
		System.out.println();
	}

	// ========== 演示3：多个线程可以同时join同一个线程 ==========
	static void demonstrateMultipleJoin() throws InterruptedException {
		System.out.println("=== 演示3：多个线程join同一个线程 ===\n");

		Thread worker = new Thread(() -> {
			System.out.println("  [Worker] 工作3秒...");
			sleep(3000);
			System.out.println("  [Worker] 完成！");
		}, "WorkerThread");

		// 创建3个观察者线程，都等待worker完成
		Thread observer1 = new Thread(() -> {
			try {
				System.out.println("  [Observer1] 等待worker完成...");
				worker.join();
				System.out.println("  [Observer1] worker完成了，我继续");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "Observer-1");

		Thread observer2 = new Thread(() -> {
			try {
				System.out.println("  [Observer2] 等待worker完成...");
				worker.join();
				System.out.println("  [Observer2] worker完成了，我继续");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "Observer-2");

		Thread observer3 = new Thread(() -> {
			try {
				System.out.println("  [Observer3] 等待worker完成...");
				worker.join();
				System.out.println("  [Observer3] worker完成了，我继续");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "Observer-3");

		// 启动所有线程
		worker.start();
		observer1.start();
		observer2.start();
		observer3.start();

		// 等待所有观察者完成
		observer1.join();
		observer2.join();
		observer3.join();

		System.out.println("\n[Main] 所有观察者都收到了worker完成的通知");
		System.out.println("原理：worker结束时JVM调用notifyAll()，唤醒所有等待的线程\n");
	}

	// ========== 演示4：详细解释join()源码 ==========
	static void explainJoinSourceCode() {
		System.out.println("=== 演示4：join()源码分析 ===\n");

		System.out.println("Thread.java中的join()源码：");
		System.out.println("--------------------------------------");
		System.out.println("public final synchronized void join(long millis)");
		System.out.println("    throws InterruptedException {");
		System.out.println("    long base = System.currentTimeMillis();");
		System.out.println("    long now = 0;");
		System.out.println();
		System.out.println("    if (millis < 0) {");
		System.out.println("        throw new IllegalArgumentException(\"timeout value is negative\");");
		System.out.println("    }");
		System.out.println();
		System.out.println("    if (millis == 0) { // join()等价于join(0)");
		System.out.println("        while (isAlive()) {");
		System.out.println("            wait(0); // 无限等待，直到被notify");
		System.out.println("        }");
		System.out.println("    } else {");
		System.out.println("        while (isAlive()) { // join(timeout)的情况");
		System.out.println("            long delay = millis - now;");
		System.out.println("            if (delay <= 0) {");
		System.out.println("                break; // 超时退出");
		System.out.println("            }");
		System.out.println("            wait(delay); // 等待指定时间");
		System.out.println("            now = System.currentTimeMillis() - base;");
		System.out.println("        }");
		System.out.println("    }");
		System.out.println("}");
		System.out.println("--------------------------------------");
		System.out.println();

		System.out.println("【关键点分析】");
		System.out.println("1. join()方法是synchronized的");
		System.out.println("   → 锁住的是线程对象本身（this）");
		System.out.println();

		System.out.println("2. 循环检查isAlive()");
		System.out.println("   → 防止虚假唤醒（spurious wakeup）");
		System.out.println("   → 确保线程真的结束了才退出");
		System.out.println();

		System.out.println("3. 调用wait(0)或wait(delay)");
		System.out.println("   → 等待时会释放线程对象的锁");
		System.out.println("   → 其他线程也可以join同一个线程");
		System.out.println();

		System.out.println("4. 谁来唤醒wait()？");
		System.out.println("   → JVM在线程结束时自动调用notifyAll()");
		System.out.println("   → 这是由JVM底层实现的，不在Java代码中");
		System.out.println();

		System.out.println("【完整流程】");
		System.out.println("① 主线程调用worker.join()");
		System.out.println("② 获取worker对象的锁");
		System.out.println("③ 检查worker.isAlive()，如果还活着");
		System.out.println("④ 调用worker.wait()，释放锁并等待");
		System.out.println("⑤ worker线程执行完run()方法");
		System.out.println("⑥ JVM调用worker的notifyAll()");
		System.out.println("⑦ 主线程被唤醒，重新获取锁");
		System.out.println("⑧ 再次检查isAlive()，发现已死亡");
		System.out.println("⑨ 退出while循环，join()返回");
		System.out.println("⑩ 主线程继续执行");
		System.out.println();
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