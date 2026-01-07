package top.tangtian.thread;

/**
 * sleep() vs wait() 深度对比
 * 关键问题：让线程暂停用哪个？
 * 答案：取决于场景！
 */
public class SleepVsWaitComparison {
	/* ========== 完整对比表 ==========

	特性对比
	┌────────────────┬─────────────────┬─────────────────┐
	│    特性        │   sleep()       │   wait()        │
	├────────────────┼─────────────────┼─────────────────┤
	│ 所属类         │ Thread          │ Object          │
	│ 释放锁         │ ❌ 不释放       │ ✅ 释放         │
	│ 必须在同步块   │ ❌ 不需要       │ ✅ 必须         │
	│ 唤醒方式       │ 时间到          │ notify/notifyAll│
	│ CPU消耗        │ 无（让出CPU）   │ 无（让出CPU）   │
	│ 响应中断       │ ✅ 是           │ ✅ 是           │
	│ 用途           │ 延迟执行        │ 线程协作        │
	└────────────────┴─────────────────┴─────────────────┘


	========== 选择指南 ==========

	使用 sleep() 的场景：
	✅ 1. 定时任务（每隔X秒做某事）
	✅ 2. 延迟执行（X秒后再执行）
	✅ 3. 限流/节流（控制速率）
	✅ 4. 模拟耗时操作（测试用）
	✅ 5. 简单的重试机制

	示例：
	// 心跳检测
	while(running) {
		sendHeartbeat();
		Thread.sleep(5000); // 每5秒一次
	}

	// 重试机制
	for(int i = 0; i < 3; i++) {
		if(tryConnect()) break;
		Thread.sleep(1000); // 失败后等1秒重试
	}


	使用 wait() 的场景：
	✅ 1. 等待条件满足（如：数据准备好）
	✅ 2. 生产者-消费者模式
	✅ 3. 线程间通信和协作
	✅ 4. 资源池（等待资源可用）
	✅ 5. 任何需要"被通知"的场景

	示例：
	// 等待资源可用
	synchronized(resource) {
		while(!resource.isAvailable()) {
			resource.wait(); // 等待资源释放
		}
		resource.use();
	}

	// 生产者-消费者
	synchronized(queue) {
		while(queue.isEmpty()) {
			queue.wait(); // 等待生产者放入数据
		}
		return queue.take();
	}


	========== 核心原则 ==========

	原则1：需要「协作」用wait，需要「延迟」用sleep
	   - 协作：线程之间相互配合（需要通知机制）
	   - 延迟：单纯的时间等待（不需要其他线程）

	原则2：持有锁时尽量不用sleep
	   - sleep不释放锁，会阻塞其他线程
	   - 如果必须等待，考虑用wait或释放锁后再sleep

	原则3：不要用sleep做轮询
	   - 轮询浪费CPU时间片
	   - 有延迟且不精确
	   - 应该用wait/notify或其他通知机制

	原则4：时间敏感性
	   - sleep：精确度受系统调度影响（毫秒级）
	   - wait：被notify时立即唤醒（更快响应）


	========== 常见错误 ==========

	❌ 错误1：用sleep轮询等待条件
	while(!ready) {
		Thread.sleep(100); // 每100ms检查，浪费且有延迟
	}

	✅ 正确：用wait/notify
	synchronized(obj) {
		while(!ready) {
			obj.wait(); // 条件满足时立即唤醒
		}
	}


	❌ 错误2：在synchronized块内sleep太久
	synchronized(lock) {
		Thread.sleep(5000); // 阻塞其他线程5秒！
		doSomething();
	}

	✅ 正确：缩小同步范围
	Thread.sleep(5000); // 先sleep
	synchronized(lock) {
		doSomething(); // 再获取锁
	}


	❌ 错误3：认为sleep可以替代wait
	// 想等待数据准备好
	while(!dataReady) {
		Thread.sleep(100); // ❌ 不好
	}

	// 应该用wait
	synchronized(dataLock) {
		while(!dataReady) {
			dataLock.wait(); // ✅ 正确
		}
	}


	========== 面试题 ==========

	Q1: sleep和wait最本质的区别是什么？
	A: sleep是"暂停执行"，wait是"等待通知"。
	   sleep不释放锁，wait释放锁。

	Q2: 什么时候用sleep，什么时候用wait？
	A: 需要延迟执行用sleep，需要等待条件满足用wait。
	   判断标准：是否需要其他线程通知。

	Q3: 为什么wait必须在synchronized块中？
	A: wait会释放锁，必须先持有锁才能释放。
	   这是为了保证wait和notify的原子性。

	Q4: sleep(0)有什么用？
	A: 触发线程调度，让出当前时间片给其他线程。
	   但实际很少用，一般用Thread.yield()。

	Q5: wait()和wait(0)有什么区别？
	A: wait()无限等待，等同于wait(0)。
	   wait(time)是有超时的等待。

	*/
	public static void main(String[] args) throws InterruptedException {
		System.out.println("========== sleep vs wait 深度对比 ==========\n");

		// 场景1：定时任务 → 用sleep
		scenario1_TimedTask();
		Thread.sleep(3000);

		// 场景2：等待条件满足 → 用wait
		scenario2_WaitForCondition();
		Thread.sleep(3000);

		// 场景3：锁的持有情况
		scenario3_LockBehavior();
		Thread.sleep(4000);

		// 场景4：CPU资源消耗对比
		scenario4_CPUUsage();
		Thread.sleep(3000);

		// 场景5：错误使用示例
		scenario5_WrongUsage();
	}

	// ========== 场景1：定时任务 → 用sleep ==========
	static void scenario1_TimedTask() {
		System.out.println("=== 场景1：定时任务（推荐sleep）===\n");

		System.out.println("需求：每隔1秒打印当前时间");
		System.out.println("分析：这是纯粹的「延迟执行」，不涉及线程协作\n");

		Thread timerThread = new Thread(() -> {
			for (int i = 1; i <= 3; i++) {
				System.out.println("  [Timer] 第" + i + "秒 - " +
						System.currentTimeMillis());

				try {
					Thread.sleep(1000); // ✅ 正确：定时延迟用sleep
				} catch (InterruptedException e) {
					break;
				}
			}
		}, "TimerThread");

		timerThread.start();

		try {
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("\n结论：定时、延迟、周期性任务 → 用 sleep\n");
	}

	// ========== 场景2：等待条件满足 → 用wait ==========
	static void scenario2_WaitForCondition() throws InterruptedException {
		System.out.println("=== 场景2：等待条件满足（推荐wait）===\n");

		System.out.println("需求：等待数据准备完成后再处理");
		System.out.println("分析：这是「线程协作」，需要其他线程通知\n");

		class DataPreparer {
			private boolean dataReady = false;
			private String data;

			// 准备数据
			public synchronized void prepareData() {
				System.out.println("  [准备线程] 开始准备数据...");
				sleep(1000); // 模拟耗时操作

				data = "重要数据";
				dataReady = true;

				System.out.println("  [准备线程] 数据准备完成，通知等待线程");
				notify(); // ✅ 唤醒等待的线程
			}

			// 处理数据
			public synchronized void processData() {
				while (!dataReady) {
					try {
						System.out.println("  [处理线程] 数据未就绪，等待...");
						wait(); // ✅ 正确：等待条件满足用wait
					} catch (InterruptedException e) {
						return;
					}
				}

				System.out.println("  [处理线程] 收到通知，处理数据：" + data);
			}
		}

		DataPreparer preparer = new DataPreparer();

		// 处理线程（先启动）
		Thread processor = new Thread(() -> preparer.processData(), "Processor");
		processor.start();

		Thread.sleep(500); // 确保processor先等待

		// 准备线程
		Thread prepThread = new Thread(() -> preparer.prepareData(), "Preparer");
		prepThread.start();

		processor.join();
		prepThread.join();

		System.out.println("\n结论：等待条件、线程协作 → 用 wait\n");
	}

	// ========== 场景3：锁的持有情况 ==========
	static void scenario3_LockBehavior() throws InterruptedException {
		System.out.println("=== 场景3：锁的持有情况（核心区别）===\n");

		Object lock = new Object();

		// 演示sleep：不释放锁
		System.out.println("【演示1：sleep()不释放锁】");
		Thread sleepThread = new Thread(() -> {
			synchronized (lock) {
				System.out.println("  [线程A] 获得锁");
				System.out.println("  [线程A] 开始sleep 2秒（仍持有锁）");
				sleep(2000);
				System.out.println("  [线程A] sleep结束，释放锁");
			}
		}, "Thread-A");

		Thread blockedThread = new Thread(() -> {
			sleep(100); // 确保线程A先获取锁
			System.out.println("  [线程B] 尝试获取锁...");
			synchronized (lock) {
				System.out.println("  [线程B] 终于获得锁！");
			}
		}, "Thread-B");

		sleepThread.start();
		blockedThread.start();
		sleepThread.join();
		blockedThread.join();

		System.out.println("\n【演示2：wait()释放锁】");
		Thread waitThread = new Thread(() -> {
			synchronized (lock) {
				System.out.println("  [线程C] 获得锁");
				try {
					System.out.println("  [线程C] 调用wait()（释放锁并等待）");
					lock.wait(2000);
					System.out.println("  [线程C] 被唤醒/超时，重新获得锁");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "Thread-C");

		Thread opportunityThread = new Thread(() -> {
			sleep(100);
			System.out.println("  [线程D] 尝试获取锁...");
			synchronized (lock) {
				System.out.println("  [线程D] 立即获得锁！（因为线程C释放了）");
			}
		}, "Thread-D");

		waitThread.start();
		opportunityThread.start();
		waitThread.join();
		opportunityThread.join();

		System.out.println("\n关键区别：");
		System.out.println("  sleep() → 不释放锁，其他线程只能等待");
		System.out.println("  wait()  → 释放锁，其他线程可以获取\n");
	}

	// ========== 场景4：CPU资源消耗对比 ==========
	static void scenario4_CPUUsage() {
		System.out.println("=== 场景4：错误的忙等待 vs 正确的等待 ===\n");

		class TaskQueue {
			private boolean hasTask = false;

			// ❌ 错误方式1：忙等待（疯狂消耗CPU）
			public void busyWaitWrong() {
				System.out.println("  [错误方式] 忙等待（while + 不断检查）");
				long start = System.currentTimeMillis();
				int checks = 0;

				while (!hasTask && System.currentTimeMillis() - start < 1000) {
					checks++; // 疯狂循环检查
				}

				System.out.println("  [错误方式] 1秒内检查了 " + checks + " 次！❌ CPU爆炸");
			}

			// ❌ 错误方式2：sleep轮询（仍然不好）
			public void sleepPollingWrong() {
				System.out.println("  [不推荐] sleep轮询");
				int checks = 0;

				while (!hasTask && checks < 10) {
					sleep(100); // 每100ms检查一次
					checks++;
				}

				System.out.println("  [不推荐] 总共检查了 " + checks +
						" 次（有延迟且浪费资源）");
			}

			// ✅ 正确方式：wait/notify
			public synchronized void waitCorrect() {
				try {
					System.out.println("  [正确方式] wait等待通知");
					while (!hasTask) {
						wait(); // 不消耗CPU，等待被唤醒
					}
					System.out.println("  [正确方式] 被唤醒，立即响应！✅ 高效");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			public synchronized void notifyTask() {
				hasTask = true;
				notify();
			}
		}

		TaskQueue queue = new TaskQueue();

		// 演示错误方式
		new Thread(() -> queue.busyWaitWrong()).start();
		sleep(1500);

		new Thread(() -> queue.sleepPollingWrong()).start();
		sleep(1500);

		// 演示正确方式
		Thread correctThread = new Thread(() -> queue.waitCorrect());
		correctThread.start();
		sleep(1000);
		queue.notifyTask();

		sleep(500);

		System.out.println("\n结论：");
		System.out.println("  忙等待 → CPU 100%，非常糟糕");
		System.out.println("  sleep轮询 → 有延迟，仍浪费资源");
		System.out.println("  wait/notify → 不消耗CPU，立即响应 ✅\n");
	}

	// ========== 场景5：错误使用示例 ==========
	static void scenario5_WrongUsage() {
		System.out.println("=== 场景5：常见错误使用 ===\n");

		Object lock = new Object();

		System.out.println("【错误1：在非同步块中调用wait()】");
		Thread wrongThread1 = new Thread(() -> {
			try {
				lock.wait(); // ❌ IllegalMonitorStateException
			} catch (Exception e) {
				System.out.println("  错误：" + e.getClass().getSimpleName() +
						" - wait()必须在synchronized块中");
			}
		});
		wrongThread1.start();

		try {
			wrongThread1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("\n【错误2：在需要协作时使用sleep轮询】");
		System.out.println("❌ 错误示例：");
		System.out.println("  while(!condition) {");
		System.out.println("      Thread.sleep(100); // 轮询，不好");
		System.out.println("  }");
		System.out.println("\n✅ 正确示例：");
		System.out.println("  synchronized(lock) {");
		System.out.println("      while(!condition) {");
		System.out.println("          lock.wait(); // 等待通知，高效");
		System.out.println("      }");
		System.out.println("  }");

		System.out.println("\n【错误3：在持有锁时sleep太久】");
		System.out.println("❌ 问题代码：");
		System.out.println("  synchronized(lock) {");
		System.out.println("      Thread.sleep(5000); // 持有锁睡5秒！");
		System.out.println("  }");
		System.out.println("影响：其他线程被阻塞5秒，并发性能极差");
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

