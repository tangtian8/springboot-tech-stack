package top.tangtian;

/**
 * @author tangtian
 * @date 2025-12-22 09:50
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程协作完整教程
 * 包括：synchronized、volatile、wait/notify、Lock、生产者消费者
 */
public class ThreadCooperationDemo {


	/* ========== 知识点总结 ==========

	一、synchronized（同步锁）
	──────────────────────
	1. 作用：保证互斥访问，同一时刻只有一个线程执行
	2. 三种用法：
	   - 修饰实例方法：锁住对象实例（this）
	   - 修饰静态方法：锁住Class对象
	   - 修饰代码块：锁住指定对象

	3. 特点：
	   ✅ 自动加锁/解锁
	   ✅ 可重入（同一线程可多次获取）
	   ❌ 无法中断
	   ❌ 无法尝试获取锁
	   ❌ 非公平锁


	二、volatile（可见性）
	──────────────────────
	1. 作用：保证变量在多线程间的可见性
	2. 原理：
	   - 强制从主内存读取
	   - 修改后立即写回主内存
	   - 禁止指令重排序

	3. 使用场景：
	   ✅ 状态标志（flag）
	   ✅ 双重检查锁单例模式
	   ❌ 不适合count++（需要原子性）

	4. 经典应用：
	   private volatile boolean running = true;

	   public void stop() {
		   running = false; // 立即对其他线程可见
	   }

	   public void run() {
		   while(running) {
			   // 工作...
		   }
	   }


	三、wait/notify（线程通信）
	──────────────────────
	1. 基本用法：
	   synchronized(obj) {
		   while(条件不满足) {
			   obj.wait();     // 释放锁并等待
		   }
		   // 执行操作
		   obj.notify();       // 唤醒一个等待线程
	   }

	2. 要点：
	   - 必须在synchronized块中调用
	   - wait()会释放锁
	   - 使用while而不是if（防止虚假唤醒）
	   - notify()随机唤醒一个，notifyAll()唤醒全部

	3. 与sleep()的区别：
	   ┌──────────┬─────────┬──────────┐
	   │          │ wait()  │ sleep()  │
	   ├──────────┼─────────┼──────────┤
	   │ 释放锁   │ 是      │ 否       │
	   │ 唤醒方式 │ notify  │ 时间到   │
	   │ 所属类   │ Object  │ Thread   │
	   └──────────┴─────────┴──────────┘


	四、Lock接口（高级锁）
	──────────────────────
	1. 基本用法：
	   Lock lock = new ReentrantLock();
	   lock.lock();
	   try {
		   // 临界区代码
	   } finally {
		   lock.unlock(); // 必须在finally中！
	   }

	2. 高级功能：
	   - tryLock()：尝试获取锁，不阻塞
	   - lockInterruptibly()：可中断的锁
	   - 公平锁：new ReentrantLock(true)
	   - Condition：多个等待队列

	3. Condition vs wait/notify：
	   Condition notEmpty = lock.newCondition();
	   Condition notFull = lock.newCondition();

	   // 可以针对不同条件等待/唤醒
	   notEmpty.await();
	   notFull.signal();


	五、生产者-消费者模式
	──────────────────────
	1. 问题场景：
	   - 生产者生产数据
	   - 消费者消费数据
	   - 缓冲区有限

	2. 核心逻辑：
	   生产：
	   while(缓冲区满) wait();
	   放入数据;
	   notify消费者;

	   消费：
	   while(缓冲区空) wait();
	   取出数据;
	   notify生产者;

	3. 实现方式：
	   - synchronized + wait/notify
	   - Lock + Condition（推荐）
	   - BlockingQueue（最简单）


	========== 选择指南 ==========

	何时用synchronized？
	  ✅ 简单的互斥场景
	  ✅ 代码量小
	  ✅ 不需要高级特性

	何时用volatile？
	  ✅ 简单的状态标志
	  ✅ 只读操作
	  ✅ 不涉及复合操作

	何时用Lock？
	  ✅ 需要可中断
	  ✅ 需要超时获取
	  ✅ 需要公平锁
	  ✅ 需要多个条件变量

	何时用wait/notify？
	  ✅ 线程间需要协作
	  ✅ 条件满足时再执行
	  ✅ 生产者-消费者模式

	*/
	public static void main(String[] args) throws InterruptedException {
		System.out.println("========== 第二阶段：线程协作 ==========\n");

		// 第一部分：synchronized 同步机制
		part1_Synchronized();
		Thread.sleep(2000);

		// 第二部分：volatile 可见性
		part2_Volatile();
		Thread.sleep(2000);

		// 第三部分：wait/notify 线程通信
		part3_WaitNotify();
		Thread.sleep(2000);

		// 第四部分：Lock接口（更灵活的锁）
		part4_Lock();
		Thread.sleep(2000);

		// 第五部分：经典案例 - 生产者消费者模式
		part5_ProducerConsumer();
	}

	// ==================== 第一部分：synchronized ====================
	static void part1_Synchronized() throws InterruptedException {
		System.out.println("=== 第一部分：synchronized 同步机制 ===\n");

		// 问题演示：不加锁的线程安全问题
		demonstrateProblem();
		Thread.sleep(1000);

		// 解决方案1：同步方法
		demonstrateSynchronizedMethod();
		Thread.sleep(1000);

		// 解决方案2：同步代码块
		demonstrateSynchronizedBlock();
		Thread.sleep(1000);

		// 解决方案3：类锁 vs 对象锁
		demonstrateClassLockVsObjectLock();
	}

	// 问题演示：多线程访问共享数据
	static void demonstrateProblem() throws InterruptedException {
		System.out.println("【问题演示：不加锁的后果】");

		class UnsafeCounter {
			private int count = 0;

			public void increment() {
				count++; // 非原子操作！实际分三步：读取、加1、写入
			}

			public int getCount() {
				return count;
			}
		}

		UnsafeCounter counter = new UnsafeCounter();

		// 创建10个线程，每个线程累加1000次
		Thread[] threads = new Thread[10];
		for (int i = 0; i < 10; i++) {
			threads[i] = new Thread(() -> {
				for (int j = 0; j < 1000; j++) {
					counter.increment();
				}
			});
			threads[i].start();
		}

		// 等待所有线程完成
		for (Thread t : threads) {
			t.join();
		}

		System.out.println("期望结果：10000");
		System.out.println("实际结果：" + counter.getCount() + " ❌ 数据不一致！");
		System.out.println("原因：count++ 不是原子操作，多线程同时修改导致数据丢失\n");
	}

	// 解决方案1：同步方法
	static void demonstrateSynchronizedMethod() throws InterruptedException {
		System.out.println("【解决方案1：synchronized 修饰方法】");

		class SafeCounter {
			private int count = 0;

			// synchronized 保证同一时刻只有一个线程执行
			public synchronized void increment() {
				count++;
			}

			public synchronized int getCount() {
				return count;
			}
		}

		SafeCounter counter = new SafeCounter();

		Thread[] threads = new Thread[10];
		for (int i = 0; i < 10; i++) {
			threads[i] = new Thread(() -> {
				for (int j = 0; j < 1000; j++) {
					counter.increment();
				}
			});
			threads[i].start();
		}

		for (Thread t : threads) {
			t.join();
		}

		System.out.println("期望结果：10000");
		System.out.println("实际结果：" + counter.getCount() + " ✅ 数据正确！");
		System.out.println("原理：synchronized 锁住对象，保证互斥访问\n");
	}

	// 解决方案2：同步代码块（更灵活）
	static void demonstrateSynchronizedBlock() throws InterruptedException {
		System.out.println("【解决方案2：synchronized 代码块】");

		class BankAccount {
			private int balance = 1000;
			private final Object lock = new Object(); // 显式锁对象

			public void withdraw(int amount) {
				// 只锁住关键代码，而不是整个方法
				System.out.println("  准备取款（不需要锁）");

				synchronized (lock) { // 锁住关键区
					if (balance >= amount) {
						System.out.println("  余额充足，执行扣款：" + amount);
						balance -= amount;
					} else {
						System.out.println("  余额不足");
					}
				}

				System.out.println("  记录日志（不需要锁）");
			}

			public int getBalance() {
				synchronized (lock) {
					return balance;
				}
			}
		}

		BankAccount account = new BankAccount();

		Thread t1 = new Thread(() -> account.withdraw(600), "线程1");
		Thread t2 = new Thread(() -> account.withdraw(600), "线程2");

		t1.start();
		t2.start();
		t1.join();
		t2.join();

		System.out.println("最终余额：" + account.getBalance());
		System.out.println("优势：只锁住必要的代码，提高并发性能\n");
	}

	// 类锁 vs 对象锁
	static void demonstrateClassLockVsObjectLock() throws InterruptedException {
		System.out.println("【类锁 vs 对象锁】");

		class LockDemo {
			// 对象锁：锁住的是实例对象
			public synchronized void instanceMethod() {
				System.out.println("  " + Thread.currentThread().getName() +
						" - 对象锁方法");
				sleep(500);
			}

			// 类锁：锁住的是Class对象（所有实例共享）
			public static synchronized void staticMethod() {
				System.out.println("  " + Thread.currentThread().getName() +
						" - 类锁方法");
				sleep(500);
			}
		}

		LockDemo obj1 = new LockDemo();
		LockDemo obj2 = new LockDemo();

		System.out.println("情况1：两个线程访问同一对象的同步方法（会互斥）");
		new Thread(() -> obj1.instanceMethod(), "T1").start();
		new Thread(() -> obj1.instanceMethod(), "T2").start();
		Thread.sleep(1200);

		System.out.println("\n情况2：两个线程访问不同对象的同步方法（不互斥）");
		new Thread(() -> obj1.instanceMethod(), "T3").start();
		new Thread(() -> obj2.instanceMethod(), "T4").start();
		Thread.sleep(1200);

		System.out.println("\n情况3：两个线程访问静态同步方法（会互斥）");
		new Thread(() -> LockDemo.staticMethod(), "T5").start();
		new Thread(() -> LockDemo.staticMethod(), "T6").start();
		Thread.sleep(1200);

		System.out.println();
	}

	// ==================== 第二部分：volatile ====================
	static void part2_Volatile() throws InterruptedException {
		System.out.println("=== 第二部分：volatile 可见性保证 ===\n");

		// 问题演示：可见性问题
		demonstrateVisibilityProblem();
		Thread.sleep(2000);

		// 解决方案：volatile
		demonstrateVolatileSolution();
	}

	static void demonstrateVisibilityProblem() throws InterruptedException {
		System.out.println("【问题演示：可见性问题】");

		class VisibilityDemo {
			private boolean flag = false; // 没有volatile

			public void setFlag() {
				System.out.println("  设置flag = true");
				flag = true;
			}

			public void checkFlag() {
				System.out.println("  等待flag变为true...");
				int count = 0;
				// 可能永远看不到flag的变化（CPU缓存导致）
				while (!flag && count < 100000000) {
					count++;
				}
				if (flag) {
					System.out.println("  检测到flag = true");
				} else {
					System.out.println("  ❌ 循环结束仍未检测到变化（可见性问题）");
				}
			}
		}

		VisibilityDemo demo = new VisibilityDemo();

		Thread reader = new Thread(() -> demo.checkFlag(), "读线程");
		Thread writer = new Thread(() -> {
			sleep(100);
			demo.setFlag();
		}, "写线程");

		reader.start();
		writer.start();
		reader.join();
		writer.join();

		System.out.println("原因：每个线程有自己的CPU缓存，修改可能不会立即同步\n");
	}

	static void demonstrateVolatileSolution() throws InterruptedException {
		System.out.println("【解决方案：volatile 关键字】");

		class VolatileDemo {
			private volatile boolean flag = false; // 加上volatile

			public void setFlag() {
				System.out.println("  设置flag = true");
				flag = true;
			}

			public void checkFlag() {
				System.out.println("  等待flag变为true...");
				while (!flag) {
					// volatile保证立即看到变化
				}
				System.out.println("  ✅ 检测到flag = true");
			}
		}

		VolatileDemo demo = new VolatileDemo();

		Thread reader = new Thread(() -> demo.checkFlag(), "读线程");
		Thread writer = new Thread(() -> {
			sleep(100);
			demo.setFlag();
		}, "写线程");

		reader.start();
		writer.start();
		reader.join();
		writer.join();

		System.out.println("\nvolatile的作用：");
		System.out.println("1. 保证可见性：修改立即对其他线程可见");
		System.out.println("2. 禁止指令重排序");
		System.out.println("3. ⚠️ 不保证原子性（count++仍需要synchronized）\n");
	}

	// ==================== 第三部分：wait/notify ====================
	static void part3_WaitNotify() throws InterruptedException {
		System.out.println("=== 第三部分：wait/notify 线程通信 ===\n");

		demonstrateWaitNotify();
	}

	static void demonstrateWaitNotify() throws InterruptedException {
		System.out.println("【wait/notify 基本使用】");

		class Message {
			private String content;
			private boolean hasMessage = false;

			// 发送消息
			public synchronized void send(String msg) {
				while (hasMessage) {
					try {
						System.out.println("  [发送者] 邮箱满了，等待...");
						wait(); // 等待消息被取走
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}

				this.content = msg;
				hasMessage = true;
				System.out.println("  [发送者] 发送消息：" + msg);
				notify(); // 通知接收者
			}

			// 接收消息
			public synchronized String receive() {
				while (!hasMessage) {
					try {
						System.out.println("  [接收者] 邮箱空的，等待...");
						wait(); // 等待新消息
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}

				hasMessage = false;
				System.out.println("  [接收者] 收到消息：" + content);
				notify(); // 通知发送者
				return content;
			}
		}

		Message message = new Message();

		// 发送者线程
		Thread sender = new Thread(() -> {
			String[] messages = {"Hello", "World", "Bye"};
			for (String msg : messages) {
				message.send(msg);
				sleep(500);
			}
		}, "Sender");

		// 接收者线程
		Thread receiver = new Thread(() -> {
			for (int i = 0; i < 3; i++) {
				message.receive();
				sleep(1000);
			}
		}, "Receiver");

		sender.start();
		receiver.start();
		sender.join();
		receiver.join();

		System.out.println("\nwait/notify 要点：");
		System.out.println("1. 必须在synchronized块中使用");
		System.out.println("2. wait()会释放锁并进入等待");
		System.out.println("3. notify()唤醒一个等待线程");
		System.out.println("4. notifyAll()唤醒所有等待线程");
		System.out.println("5. 使用while循环判断条件（防止虚假唤醒）\n");
	}

	// ==================== 第四部分：Lock接口 ====================
	static void part4_Lock() throws InterruptedException {
		System.out.println("=== 第四部分：Lock接口（更灵活的锁）===\n");

		demonstrateLock();
	}

	static void demonstrateLock() throws InterruptedException {
		System.out.println("【ReentrantLock vs synchronized】");

		class TicketOffice {
			private int tickets = 10;
			private final Lock lock = new ReentrantLock();
			private final Condition condition = lock.newCondition();

			// 使用Lock的售票方法
			public void sellTicket(String customer) {
				lock.lock(); // 获取锁
				try {
					while (tickets <= 0) {
						System.out.println("  " + customer + " 等待补票...");
						condition.await(); // 等待（类似wait）
					}

					tickets--;
					System.out.println("  " + customer + " 购票成功，剩余：" + tickets);

					if (tickets > 0) {
						condition.signal(); // 唤醒一个（类似notify）
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					lock.unlock(); // 必须在finally中释放锁！
				}
			}

			public void addTickets(int count) {
				lock.lock();
				try {
					tickets += count;
					System.out.println("  补充了" + count + "张票，总共：" + tickets);
					condition.signalAll(); // 唤醒所有等待的客户
				} finally {
					lock.unlock();
				}
			}
		}

		TicketOffice office = new TicketOffice();

		// 创建5个客户
		for (int i = 1; i <= 5; i++) {
			final int customerId = i;
			new Thread(() -> {
				for (int j = 0; j < 3; j++) {
					office.sellTicket("客户" + customerId);
					sleep(100);
				}
			}).start();
		}

		// 1秒后补票
		Thread.sleep(1000);
		office.addTickets(10);

		Thread.sleep(2000);

		System.out.println("\nLock vs synchronized：");
		System.out.println("┌─────────────┬──────────────┬──────────────┐");
		System.out.println("│  特性       │ synchronized │ Lock         │");
		System.out.println("├─────────────┼──────────────┼──────────────┤");
		System.out.println("│  锁释放     │ 自动         │ 手动unlock() │");
		System.out.println("│  可中断     │ 否           │ 是           │");
		System.out.println("│  公平锁     │ 否           │ 可选         │");
		System.out.println("│  多条件变量 │ 否           │ 是           │");
		System.out.println("│  性能       │ 好           │ 更好         │");
		System.out.println("└─────────────┴──────────────┴──────────────┘\n");
	}

	// ==================== 第五部分：生产者消费者 ====================
	static void part5_ProducerConsumer() throws InterruptedException {
		System.out.println("=== 第五部分：经典案例 - 生产者消费者模式 ===\n");

		demonstrateProducerConsumer();
	}

	static void demonstrateProducerConsumer() throws InterruptedException {
		System.out.println("【完整的生产者-消费者实现】");

		class Buffer {
			private final Queue<Integer> queue = new LinkedList<>();
			private final int capacity = 5;
			private final Lock lock = new ReentrantLock();
			private final Condition notFull = lock.newCondition();
			private final Condition notEmpty = lock.newCondition();

			// 生产
			public void produce(int item) throws InterruptedException {
				lock.lock();
				try {
					while (queue.size() == capacity) {
						System.out.println("    [生产者] 缓冲区满，等待...");
						notFull.await(); // 等待缓冲区不满
					}

					queue.offer(item);
					System.out.println("  [生产者] 生产：" + item +
							"，缓冲区大小：" + queue.size());

					notEmpty.signal(); // 通知消费者
				} finally {
					lock.unlock();
				}
			}

			// 消费
			public int consume() throws InterruptedException {
				lock.lock();
				try {
					while (queue.isEmpty()) {
						System.out.println("    [消费者] 缓冲区空，等待...");
						notEmpty.await(); // 等待缓冲区不空
					}

					int item = queue.poll();
					System.out.println("  [消费者] 消费：" + item +
							"，缓冲区大小：" + queue.size());

					notFull.signal(); // 通知生产者
					return item;
				} finally {
					lock.unlock();
				}
			}
		}

		Buffer buffer = new Buffer();

		// 创建2个生产者
		for (int i = 1; i <= 2; i++) {
			final int producerId = i;
			new Thread(() -> {
				try {
					for (int j = 0; j < 5; j++) {
						int item = producerId * 100 + j;
						buffer.produce(item);
						Thread.sleep(200);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}, "生产者-" + i).start();
		}

		// 创建3个消费者
		for (int i = 1; i <= 3; i++) {
			new Thread(() -> {
				try {
					for (int j = 0; j < 3; j++) {
						buffer.consume();
						Thread.sleep(300);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}, "消费者-" + i).start();
		}

		Thread.sleep(5000);

		System.out.println("\n生产者-消费者模式要点：");
		System.out.println("1. 共享缓冲区：生产者放入，消费者取出");
		System.out.println("2. 缓冲区满：生产者等待");
		System.out.println("3. 缓冲区空：消费者等待");
		System.out.println("4. 使用两个条件变量：notFull 和 notEmpty");
		System.out.println("5. 这是线程协作的经典应用场景");
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