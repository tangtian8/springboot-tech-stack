package top.tangtian.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author tangtian
 * @date 2025-12-22 09:25
 */
public class ThreadCreationDemo {
	/* ========== 三种方式对比 ==========

		1. 继承Thread类
		   优点：代码简单，直接重写run()
		   缺点：Java单继承限制，无法继承其他类
		   使用场景：简单的线程任务

		2. 实现Runnable接口
		   优点：避免单继承限制，多个线程可共享同一个Runnable对象
		   缺点：无法获取返回值
		   使用场景：最常用的方式，推荐使用

		3. 实现Callable接口
		   优点：可以获取返回值，可以抛出异常
		   缺点：相对复杂，需要FutureTask包装
		   使用场景：需要获取线程执行结果时

		推荐：大多数情况使用Runnable，需要返回值时使用Callable
		*/
	public static void main(String[] args) throws Exception {
		System.out.println("=== 方式1：继承Thread类 ===");
		demonstrateThreadExtension();

		Thread.sleep(2000); // 等待上一组线程执行完

		System.out.println("\n=== 方式2：实现Runnable接口 ===");
		demonstrateRunnable();

		Thread.sleep(2000);

		System.out.println("\n=== 方式3：实现Callable接口（带返回值）===");
		demonstrateCallable();
	}

	// ============ 方式1：继承Thread类 ============
	static class DownloadThread extends Thread {
		private String fileName;

		public DownloadThread(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() +
					" 开始下载：" + fileName);
			try {
				// 模拟下载耗时
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() +
					" 下载完成：" + fileName);
		}
	}

	static void demonstrateThreadExtension() {
		DownloadThread t1 = new DownloadThread("文件A.zip");
		DownloadThread t2 = new DownloadThread("文件B.zip");

		t1.setName("下载线程-1");
		t2.setName("下载线程-2");

		t1.start(); // 启动线程
		t2.start();

		// 注意：不能调用run()方法，那样只是普通方法调用，不会创建新线程
	}

	// ============ 方式2：实现Runnable接口 ============
	static class UploadTask implements Runnable {
		private String fileName;

		public UploadTask(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() +
					" 开始上传：" + fileName);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() +
					" 上传完成：" + fileName);
		}
	}

	static void demonstrateRunnable() {
		// Runnable只是任务，需要包装成Thread才能执行
		Thread t1 = new Thread(new UploadTask("照片1.jpg"), "上传线程-1");
		Thread t2 = new Thread(new UploadTask("照片2.jpg"), "上传线程-2");

		t1.start();
		t2.start();

		// 使用Lambda表达式（更简洁）
		Thread t3 = new Thread(() -> {
			System.out.println(Thread.currentThread().getName() +
					" 使用Lambda上传：视频.mp4");
		}, "上传线程-3");
		t3.start();
	}

	// ============ 方式3：实现Callable接口（带返回值）============
	static class DataProcessTask implements Callable<String> {
		private String dataName;

		public DataProcessTask(String dataName) {
			this.dataName = dataName;
		}

		@Override
		public String call() throws Exception {
			System.out.println(Thread.currentThread().getName() +
					" 开始处理数据：" + dataName);
			Thread.sleep(1000);

			// 返回处理结果
			return "数据 " + dataName + " 处理完成，结果：SUCCESS";
		}
	}

	static void demonstrateCallable() throws Exception {
		// Callable需要通过FutureTask包装
		FutureTask<String> task1 = new FutureTask<>(
				new DataProcessTask("订单数据")
		);
		FutureTask<String> task2 = new FutureTask<>(
				new DataProcessTask("用户数据")
		);

		Thread t1 = new Thread(task1, "数据处理线程-1");
		Thread t2 = new Thread(task2, "数据处理线程-2");

		t1.start();
		t2.start();

		// 获取返回值（会阻塞直到任务完成）
		String result1 = task1.get();
		String result2 = task2.get();

		System.out.println("收到返回值：" + result1);
		System.out.println("收到返回值：" + result2);
	}
}
