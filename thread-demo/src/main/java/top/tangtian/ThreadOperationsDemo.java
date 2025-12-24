package top.tangtian;

/**
 * @author tangtian
 * @date 2025-12-22 09:37
 * 线程基本操作演示
 * 包括：start/run、sleep、join、yield、interrupt
 */
public class ThreadOperationsDemo {

	public static void main(String[] args) throws InterruptedException {
		// 演示1：start() vs run()
		demonstrateStartVsRun();

		Thread.sleep(2000);

		// 演示2：sleep() 休眠
		demonstrateSleep();

		Thread.sleep(3000);

		// 演示3：join() 等待线程结束
		demonstrateJoin();

		Thread.sleep(2000);

		// 演示4：yield() 让出CPU
		demonstrateYield();

		Thread.sleep(2000);

		// 演示5：interrupt() 中断机制
		demonstrateInterrupt();
	}

	// ========== 演示1：start() vs run() ==========
	static void demonstrateStartVsRun() {
		System.out.println("=== 演示1：start() vs run() ===");
		System.out.println("主线程：" + Thread.currentThread().getName());

		Thread thread1 = new Thread(() -> {
			System.out.println("  thread1执行线程：" + Thread.currentThread().getName());
		}, "MyThread-1");

		Thread thread2 = new Thread(() -> {
			System.out.println("  thread2执行线程：" + Thread.currentThread().getName());
		}, "MyThread-2");

		// 使用start() - 创建新线程
		System.out.println("\n调用start()：");
		thread1.start(); // 输出：MyThread-1

		// 使用run() - 只是普通方法调用
		System.out.println("调用run()：");
		thread2.run();   // 输出：main（在主线程执行！）

		System.out.println("\n结论：start()创建新线程，run()只是普通方法调用\n");
	}

	// ========== 演示2：sleep() 线程休眠 ==========
	static void demonstrateSleep() throws InterruptedException {
		System.out.println("=== 演示2：sleep() 线程休眠 ===");

		// 场景：模拟定时任务
		Thread timerThread = new Thread(() -> {
			for (int i = 1; i <= 5; i++) {
				System.out.println("  倒计时：" + i + " 秒");
				try {
					Thread.sleep(1000); // 休眠1秒
				} catch (InterruptedException e) {
					System.out.println("  睡眠被中断！");
					return;
				}
			}
			System.out.println("  倒计时结束！");
		}, "TimerThread");

		timerThread.start();

		System.out.println("主线程继续执行其他任务...");

		// 重要特性：sleep()不释放锁
		demonstrateSleepWithLock();
	}

	static void demonstrateSleepWithLock() throws InterruptedException {
		Object lock = new Object();

		Thread t = new Thread(() -> {
			synchronized (lock) {
				System.out.println("  线程获得锁，准备sleep...");
				try {
					Thread.sleep(2000); // sleep期间仍持有锁！
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("  线程sleep结束，释放锁");
			}
		});

		t.start();
		Thread.sleep(500); // 确保t先获取锁

		// 主线程尝试获取锁（会被阻塞）
		new Thread(() -> {
			System.out.println("  另一个线程尝试获取锁...");
			synchronized (lock) {
				System.out.println("  另一个线程获得锁！");
			}
		}).start();

		System.out.println("结论：sleep()期间不释放锁\n");
	}

	// ========== 演示3：join() 等待线程结束 ==========
	static void demonstrateJoin() throws InterruptedException {
		System.out.println("=== 演示3：join() 等待线程结束 ===");

		// 场景1：顺序执行多个任务
		System.out.println("\n场景1：数据处理流程（必须按顺序）");

		Thread downloadThread = new Thread(() -> {
			System.out.println("  步骤1：下载数据...");
			sleep(1000);
			System.out.println("  步骤1：下载完成");
		}, "DownloadThread");

		Thread processThread = new Thread(() -> {
			System.out.println("  步骤2：处理数据...");
			sleep(1000);
			System.out.println("  步骤2：处理完成");
		}, "ProcessThread");

		Thread uploadThread = new Thread(() -> {
			System.out.println("  步骤3：上传结果...");
			sleep(1000);
			System.out.println("  步骤3：上传完成");
		}, "UploadThread");

		// 使用join()保证顺序执行
		downloadThread.start();
		downloadThread.join(); // 等待下载完成

		processThread.start();
		processThread.join();  // 等待处理完成

		uploadThread.start();
		uploadThread.join();   // 等待上传完成

		System.out.println("所有步骤完成！\n");

		// 场景2：join(timeout) 限时等待
		System.out.println("场景2：join(timeout) 限时等待");

		Thread longTask = new Thread(() -> {
			System.out.println("  长任务开始（需要5秒）...");
			sleep(5000);
			System.out.println("  长任务完成");
		}, "LongTask");

		longTask.start();

		System.out.println("主线程最多等待2秒...");
		longTask.join(2000); // 最多等2秒

		if (longTask.isAlive()) {
			System.out.println("任务还没完成，主线程不再等待！");
		} else {
			System.out.println("任务已完成");
		}
		System.out.println();
	}

	// ========== 演示4：yield() 让出CPU ==========
	static void demonstrateYield() {
		System.out.println("=== 演示4：yield() 让出CPU时间片 ===");

		// yield()只是一个提示，不保证一定让出
		Thread t1 = new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				System.out.println("  线程1 - " + i);
				if (i % 2 == 0) {
					Thread.yield(); // 偶数时让出CPU
					System.out.println("    (线程1让出CPU)");
				}
			}
		}, "YieldThread-1");

		Thread t2 = new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				System.out.println("  线程2 - " + i);
			}
		}, "YieldThread-2");

		t1.start();
		t2.start();

		sleep(500);
		System.out.println("注意：yield()只是提示，实际效果取决于操作系统调度\n");
	}

	// ========== 演示5：interrupt() 中断机制 ==========
	static void demonstrateInterrupt() throws InterruptedException {
		System.out.println("=== 演示5：interrupt() 中断机制 ===");

		// 场景1：中断阻塞状态的线程（sleep/wait/join）
		System.out.println("\n场景1：中断sleep中的线程");
		Thread sleepingThread = new Thread(() -> {
			try {
				System.out.println("  线程开始sleep 5秒...");
				Thread.sleep(5000);
				System.out.println("  线程sleep完成（这行不会执行）");
			} catch (InterruptedException e) {
				System.out.println("  线程被中断，抛出InterruptedException");
				System.out.println("  中断标志位：" + Thread.currentThread().isInterrupted());
			}
		}, "SleepingThread");

		sleepingThread.start();
		Thread.sleep(1000); // 等1秒后中断
		System.out.println("主线程发送中断信号...");
		sleepingThread.interrupt(); // 中断！
		sleepingThread.join();

		// 场景2：中断运行中的线程（需要主动检查）
		System.out.println("\n场景2：中断运行中的线程（正确方式）");
		Thread runningThread = new Thread(() -> {
			int count = 0;
			// 方式1：检查中断标志
			while (!Thread.currentThread().isInterrupted()) {
				count++;
				if (count % 100000000 == 0) {
					System.out.println("  计算中... " + count);
				}
			}
			System.out.println("  检测到中断标志，线程退出");
		}, "RunningThread");

		runningThread.start();
		Thread.sleep(500);
		System.out.println("主线程发送中断信号...");
		runningThread.interrupt();
		runningThread.join();

		// 场景3：错误的中断处理
		System.out.println("\n场景3：错误示范 - 忽略中断");
		Thread badThread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(100);
					// 做一些工作...
				} catch (InterruptedException e) {
					// ❌ 错误：捕获后不处理，线程无法停止！
					System.out.println("  捕获到中断，但继续执行...");
				}
			}
		}, "BadThread");

		badThread.start();
		Thread.sleep(500);
		badThread.interrupt();
		Thread.sleep(500);
		System.out.println("BadThread状态：" + badThread.getState() +
				"（仍在运行！这是错误的）\n");

		// 场景4：正确的中断处理
		System.out.println("场景4：正确的中断处理");
		Thread goodThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(100);
					// 做一些工作...
				} catch (InterruptedException e) {
					// ✅ 正确：恢复中断状态并退出
					System.out.println("  捕获到中断，恢复中断状态并退出");
					Thread.currentThread().interrupt(); // 恢复中断状态
					break;
				}
			}
			System.out.println("  线程正常退出");
		}, "GoodThread");

		goodThread.start();
		Thread.sleep(500);
		goodThread.interrupt();
		goodThread.join();
		System.out.println("GoodThread状态：" + goodThread.getState() + "（已终止）\n");
	}

	// 辅助方法：简化sleep调用
	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}