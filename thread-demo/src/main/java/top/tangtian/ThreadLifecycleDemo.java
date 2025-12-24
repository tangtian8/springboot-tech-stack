package top.tangtian;

/**
 * @author tangtian
 * @date 2025-12-22 09:29
 */
public class ThreadLifecycleDemo {
	private static final Object lock = new Object();

	/* ========== 线程状态转换图 ==========

    NEW (新建)
     |
     | start()
     ↓
    RUNNABLE (可运行/运行中)
     |
     |------------------------→ BLOCKED (阻塞，等待锁)
     |                              ↓
     |                         获得锁后返回
     |                              ↓
     |←---------------------------
     |
     |------------------------→ WAITING (等待)
     |                         - wait()
     |                         - join()
     |                         - LockSupport.park()
     |                              ↓
     |                         notify()/notifyAll()
     |                         或其他线程结束
     |                              ↓
     |←---------------------------
     |
     |------------------------→ TIMED_WAITING (计时等待)
     |                         - sleep(time)
     |                         - wait(time)
     |                         - join(time)
     |                         - LockSupport.parkNanos()
     |                              ↓
     |                         时间到或被中断
     |                              ↓
     |←---------------------------
     |
     | run()执行完毕
     ↓
    TERMINATED (终止)


	========== 各状态的触发条件 ==========

	进入BLOCKED：
	- 尝试获取synchronized锁，但锁被其他线程持有

	进入WAITING：
	- Object.wait() - 需要在synchronized块中调用
	- Thread.join() - 等待目标线程结束
	- LockSupport.park() - 暂停当前线程

	进入TIMED_WAITING：
	- Thread.sleep(time)
	- Object.wait(time)
	- Thread.join(time)
	- LockSupport.parkNanos()
	- LockSupport.parkUntil()

	从WAITING/TIMED_WAITING返回RUNNABLE：
	- notify()/notifyAll() 唤醒
	- 目标线程执行完毕（join的情况）
	- 时间到期（TIMED_WAITING）
	- 被中断（抛出InterruptedException）

	进入TERMINATED：
	- run()方法正常执行完毕
	- run()方法抛出未捕获的异常

	========== 重要提示 ==========

	1. RUNNABLE状态包含两种情况：
	   - Ready：等待CPU时间片
	   - Running：正在CPU上执行

	2. sleep() vs wait()：
	   - sleep()不释放锁，wait()释放锁
	   - sleep()是Thread的方法，wait()是Object的方法
	   - sleep()必须指定时间，wait()可以不指定

	3. 线程中断：
	   - 中断WAITING/TIMED_WAITING状态会抛出InterruptedException
	   - 中断RUNNABLE状态不会立即停止，需要检查中断标志

	*/
	public static void main(String[] args) throws InterruptedException {
		System.out.println("=== 线程生命周期演示 ===\n");

		// 演示1：NEW -> RUNNABLE -> TERMINATED
		demonstrateBasicLifecycle();

		Thread.sleep(2000);

		// 演示2：BLOCKED状态
		demonstrateBlockedState();

		Thread.sleep(3000);

		// 演示3：WAITING状态
		demonstrateWaitingState();

		Thread.sleep(3000);

		// 演示4：TIMED_WAITING状态
		demonstrateTimedWaitingState();
	}

	// ========== 演示1：基本生命周期 NEW -> RUNNABLE -> TERMINATED ==========
	static void demonstrateBasicLifecycle() throws InterruptedException {
		System.out.println("【演示1：基本生命周期】");

		Thread thread = new Thread(() -> {
			System.out.println("  线程开始执行任务...");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("  线程任务执行完成");
		}, "BasicThread");

		// 状态1：NEW（新建）
		System.out.println("1. 创建后状态：" + thread.getState()); // NEW

		// 启动线程
		thread.start();

		// 状态2：RUNNABLE（可运行）
		System.out.println("2. 启动后状态：" + thread.getState()); // RUNNABLE

		// 等待线程执行完毕
		thread.join();

		// 状态3：TERMINATED（终止）
		System.out.println("3. 结束后状态：" + thread.getState()); // TERMINATED
		System.out.println();
	}

	// ========== 演示2：BLOCKED状态（等待获取锁）==========
	static void demonstrateBlockedState() throws InterruptedException {
		System.out.println("【演示2：BLOCKED状态 - 等待锁】");

		Thread thread1 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("  线程1获得锁，持有3秒...");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("  线程1释放锁");
			}
		}, "Thread-1");

		Thread thread2 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("  线程2获得锁");
			}
		}, "Thread-2");

		thread1.start();
		Thread.sleep(100); // 确保thread1先获取锁

		thread2.start();
		Thread.sleep(100); // 确保thread2进入等待锁的状态

		// 此时thread2应该是BLOCKED状态，因为在等待thread1释放锁
		System.out.println("线程1状态：" + thread1.getState()); // TIMED_WAITING (在sleep)
		System.out.println("线程2状态：" + thread2.getState()); // BLOCKED (等待锁)
		System.out.println();
	}

	// ========== 演示3：WAITING状态（无限期等待）==========
	static void demonstrateWaitingState() throws InterruptedException {
		System.out.println("【演示3：WAITING状态 - 无限期等待】");

		Thread waitingThread = new Thread(() -> {
			synchronized (lock) {
				try {
					System.out.println("  线程进入等待状态（调用wait()）...");
					lock.wait(); // 释放锁并等待，直到被notify/notifyAll唤醒
					System.out.println("  线程被唤醒！");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "WaitingThread");

		waitingThread.start();
		Thread.sleep(500); // 等待线程进入wait状态

		// 检查状态
		System.out.println("等待线程状态：" + waitingThread.getState()); // WAITING

		// 唤醒等待的线程
		synchronized (lock) {
			System.out.println("  主线程唤醒等待线程（调用notify()）");
			lock.notify();
		}

		waitingThread.join();
		System.out.println();
	}

	// ========== 演示4：TIMED_WAITING状态（计时等待）==========
	static void demonstrateTimedWaitingState() throws InterruptedException {
		System.out.println("【演示4：TIMED_WAITING状态 - 计时等待】");

		// 方式1：通过sleep()进入TIMED_WAITING
		Thread sleepThread = new Thread(() -> {
			try {
				System.out.println("  线程sleep 2秒...");
				Thread.sleep(2000);
				System.out.println("  线程sleep结束");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "SleepThread");

		sleepThread.start();
		Thread.sleep(100);
		System.out.println("Sleep线程状态：" + sleepThread.getState()); // TIMED_WAITING

		// 方式2：通过join(timeout)进入TIMED_WAITING
		Thread joinThread = new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "JoinThread");

		Thread mainWaiter = new Thread(() -> {
			try {
				joinThread.start();
				System.out.println("  等待JoinThread完成（最多等1秒）...");
				joinThread.join(1000); // 最多等待1秒
				System.out.println("  等待结束");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "MainWaiter");

		mainWaiter.start();
		Thread.sleep(100);
		System.out.println("MainWaiter状态：" + mainWaiter.getState()); // TIMED_WAITING
		System.out.println();
	}
}
