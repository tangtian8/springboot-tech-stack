package top.tangtian;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author tangtian
 * @date 2025-12-23 09:55
/**
 * 第四阶段：高级并发特性
 *
 * 包括：
 * 1. CompletableFuture（异步编程）
 * 2. ForkJoin框架（分治并行）
 * 3. 实战案例
 */
public class AdvancedConcurrencyDemo {
	/* ========== 知识点总结 ==========

	一、CompletableFuture（异步编程）
	────────────────────────────────

	1. 创建方式：
	   • supplyAsync(() -> T) - 有返回值
	   • runAsync(() -> void) - 无返回值

	2. 链式调用：
	   • thenApply(T -> U) - 转换结果
	   • thenAccept(T -> void) - 消费结果
	   • thenRun(() -> void) - 不关心结果
	   • thenCompose(T -> CF<U>) - 扁平化异步

	3. 组合操作：
	   • thenCombine(CF, BiFunction) - 组合两个结果
	   • allOf(CF...) - 等待所有完成
	   • anyOf(CF...) - 任意一个完成

	4. 异常处理：
	   • exceptionally(ex -> T) - 捕获异常
	   • handle((T, ex) -> U) - 处理成功和失败
	   • whenComplete((T, ex) -> void) - 完成回调


	二、ForkJoin框架（分治并行）
	────────────────────────────────

	1. 核心概念：
	   • Fork：分解任务
	   • Join：合并结果
	   • 工作窃取：负载均衡

	2. 任务类型：
	   • RecursiveTask<V> - 有返回值
	   • RecursiveAction - 无返回值

	3. 关键方法：
	   • fork() - 异步执行
	   • join() - 获取结果
	   • invoke() - 同步执行
	   • invokeAll() - 批量执行

	4. 适用场景：
	   • 计算密集型任务
	   • 可分解的大任务
	   • 数组/集合的并行操作


	========== CompletableFuture方法速查 ==========

	创建：
	  supplyAsync(Supplier)
	  runAsync(Runnable)
	  completedFuture(value)

	转换：
	  thenApply(Function)          - 同步转换
	  thenApplyAsync(Function)     - 异步转换

	消费：
	  thenAccept(Consumer)         - 同步消费
	  thenAcceptAsync(Consumer)    - 异步消费

	执行：
	  thenRun(Runnable)            - 同步执行
	  thenRunAsync(Runnable)       - 异步执行

	组合：
	  thenCompose(Function)        - 扁平化
	  thenCombine(CF, BiFunction)  - 组合两个
	  allOf(CF...)                 - 等待全部
	  anyOf(CF...)                 - 任意一个

	异常：
	  exceptionally(Function)      - 异常处理
	  handle(BiFunction)           - 统一处理
	  whenComplete(BiConsumer)     - 完成回调


	========== ForkJoin使用模板 ==========

	RecursiveTask模板（有返回值）：
	────────────────────────────
	class MyTask extends RecursiveTask<Result> {
		@Override
		protected Result compute() {
			if (任务足够小) {
				直接计算并返回结果;
			} else {
				分解成子任务;
				fork子任务;
				join子任务;
				合并结果并返回;
			}
		}
	}

	RecursiveAction模板（无返回值）：
	────────────────────────────
	class MyTask extends RecursiveAction {
		@Override
		protected void compute() {
			if (任务足够小) {
				直接执行;
			} else {
				分解成子任务;
				invokeAll(子任务们);
			}
		}
	}


	========== 性能对比 ==========

	场景1：多个独立IO操作
	  • 串行：T1 + T2 + T3 = 1500ms
	  • CompletableFuture并行：max(T1, T2, T3) = 400ms
	  • 提升：3.75倍

	场景2：计算密集型任务
	  • 单线程：N × T
	  • ForkJoin：N × T / CPU核心数
	  • 提升：接近CPU核心数倍

	场景3：批量数据处理
	  • 串行：n × t
	  • CompletableFuture：n × t / 线程数
	  • 提升：取决于线程数和任务特性


	========== 最佳实践 ==========

	CompletableFuture：
	✅ IO密集型任务（网络请求、数据库查询）
	✅ 需要组合多个异步结果
	✅ 需要异常处理和回调
	❌ 简单的CPU计算（开销大于收益）

	ForkJoin：
	✅ CPU密集型任务
	✅ 可递归分解的大任务
	✅ 数组/集合的并行计算
	❌ IO密集型（线程会阻塞）
	❌ 任务粒度难以把握时

	*/

	public static void main(String[] args) throws Exception {
		System.out.println("========== 第四阶段：高级并发特性 ==========\n");

		// 第一部分：CompletableFuture基础
		part1_CompletableFutureBasics();
		Thread.sleep(2000);

		// 第二部分：CompletableFuture组合操作
		part2_CompletableFutureCombination();
		Thread.sleep(2000);

		// 第三部分：CompletableFuture异常处理
		part3_CompletableFutureException();
		Thread.sleep(2000);

		// 第四部分：ForkJoin框架
		part4_ForkJoinFramework();
		Thread.sleep(2000);

		// 第五部分：实战案例
		part5_RealWorldExamples();
	}

	// ==================== 第一部分：CompletableFuture基础 ====================
	static void part1_CompletableFutureBasics() throws Exception {
		System.out.println("=== 第一部分：CompletableFuture 基础 ===\n");

		System.out.println("【什么是CompletableFuture？】");
		System.out.println("Java 8引入的异步编程工具，解决Future的不足：");
		System.out.println("  ❌ Future.get()会阻塞");
		System.out.println("  ❌ 不能链式调用");
		System.out.println("  ❌ 不能组合多个异步任务");
		System.out.println("  ✅ CompletableFuture支持回调、组合、异常处理\n");

		// 1.1 创建CompletableFuture
		System.out.println("【1.1 创建CompletableFuture】\n");

		// 方式1：supplyAsync（有返回值）
		CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
			System.out.println("  [异步任务1] 执行中... 线程：" +
					Thread.currentThread().getName());
			sleep(1000);
			return "结果1";
		});

		System.out.println("主线程不阻塞，继续执行...");
		String result1 = future1.get(); // 获取结果（会阻塞）
		System.out.println("  得到结果：" + result1 + "\n");

		// 方式2：runAsync（无返回值）
		CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
			System.out.println("  [异步任务2] 执行中...");
			sleep(500);
			System.out.println("  [异步任务2] 完成");
		});

		future2.get();
		System.out.println();

		// 1.2 thenApply（链式调用）
		System.out.println("【1.2 thenApply - 链式处理结果】\n");

		CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
			System.out.println("  步骤1：获取用户ID");
			return "user123";
		}).thenApply(userId -> {
			System.out.println("  步骤2：根据ID查询用户名：" + userId);
			sleep(500);
			return "张三";
		}).thenApply(userName -> {
			System.out.println("  步骤3：格式化输出：" + userName);
			return "用户：" + userName;
		});

		System.out.println("最终结果：" + future3.get() + "\n");

		// 1.3 thenAccept（消费结果）
		System.out.println("【1.3 thenAccept - 消费结果（无返回值）】\n");

		CompletableFuture.supplyAsync(() -> {
			return "订单已创建";
		}).thenAccept(message -> {
			System.out.println("  发送通知：" + message);
		}).get();

		System.out.println();

		// 1.4 thenRun（不关心结果）
		System.out.println("【1.4 thenRun - 不关心前一步结果】\n");

		CompletableFuture.supplyAsync(() -> {
			System.out.println("  任务执行中...");
			return "完成";
		}).thenRun(() -> {
			System.out.println("  清理资源");
		}).get();

		System.out.println();
	}

	// ==================== 第二部分：CompletableFuture组合操作 ====================
	static void part2_CompletableFutureCombination() throws Exception {
		System.out.println("=== 第二部分：CompletableFuture 组合操作 ===\n");

		// 2.1 thenCombine（组合两个异步任务）
		System.out.println("【2.1 thenCombine - 组合两个任务的结果】\n");
		System.out.println("场景：并行查询用户信息和订单信息\n");

		CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() -> {
			System.out.println("  [任务1] 查询用户信息...");
			sleep(1000);
			return "用户：张三";
		});

		CompletableFuture<String> orderFuture = CompletableFuture.supplyAsync(() -> {
			System.out.println("  [任务2] 查询订单信息...");
			sleep(800);
			return "订单：3笔";
		});

		CompletableFuture<String> combinedFuture = userFuture.thenCombine(
				orderFuture,
				(user, order) -> user + ", " + order
		);

		System.out.println("合并结果：" + combinedFuture.get() + "\n");

		// 2.2 allOf（等待所有任务完成）
		System.out.println("【2.2 allOf - 等待所有任务完成】\n");
		System.out.println("场景：并行下载多个文件\n");

		CompletableFuture<String> download1 = CompletableFuture.supplyAsync(() -> {
			System.out.println("  下载文件1...");
			sleep(1000);
			return "文件1";
		});

		CompletableFuture<String> download2 = CompletableFuture.supplyAsync(() -> {
			System.out.println("  下载文件2...");
			sleep(1200);
			return "文件2";
		});

		CompletableFuture<String> download3 = CompletableFuture.supplyAsync(() -> {
			System.out.println("  下载文件3...");
			sleep(800);
			return "文件3";
		});

		CompletableFuture<Void> allDownloads = CompletableFuture.allOf(
				download1, download2, download3
		);

		allDownloads.get(); // 等待所有完成
		System.out.println("✅ 所有文件下载完成\n");

		// 收集结果
		List<String> results = Stream.of(download1, download2, download3)
				.map(CompletableFuture::join)
				.collect(Collectors.toList());
		System.out.println("下载结果：" + results + "\n");

		// 2.3 anyOf（任意一个完成即可）
		System.out.println("【2.3 anyOf - 任意一个完成即可】\n");
		System.out.println("场景：从多个数据源查询，谁先返回用谁\n");

		CompletableFuture<String> source1 = CompletableFuture.supplyAsync(() -> {
			sleep(1000);
			return "来自数据源1";
		});

		CompletableFuture<String> source2 = CompletableFuture.supplyAsync(() -> {
			sleep(500);
			return "来自数据源2";
		});

		CompletableFuture<String> source3 = CompletableFuture.supplyAsync(() -> {
			sleep(1500);
			return "来自数据源3";
		});

		CompletableFuture<Object> fastestResult = CompletableFuture.anyOf(
				source1, source2, source3
		);

		System.out.println("最快的结果：" + fastestResult.get() + "\n");

		// 2.4 thenCompose（扁平化异步操作）
		System.out.println("【2.4 thenCompose - 扁平化嵌套异步】\n");
		System.out.println("场景：先查用户ID，再根据ID查详情\n");

		CompletableFuture<String> userDetailFuture = CompletableFuture.supplyAsync(() -> {
			System.out.println("  步骤1：查询用户ID");
			return "123";
		}).thenCompose(userId -> CompletableFuture.supplyAsync(() -> {
			System.out.println("  步骤2：根据ID=" + userId + "查询详情");
			sleep(500);
			return "张三，25岁，北京";
		}));

		System.out.println("用户详情：" + userDetailFuture.get() + "\n");
	}

	// ==================== 第三部分：异常处理 ====================
	static void part3_CompletableFutureException() throws Exception {
		System.out.println("=== 第三部分：CompletableFuture 异常处理 ===\n");

		// 3.1 exceptionally（捕获异常）
		System.out.println("【3.1 exceptionally - 捕获异常并提供默认值】\n");

		CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
			System.out.println("  执行任务...");
			if (new Random().nextBoolean()) {
				throw new RuntimeException("模拟异常");
			}
			return "成功结果";
		}).exceptionally(ex -> {
			System.out.println("  ❌ 捕获异常：" + ex.getMessage());
			return "默认值";
		});

		System.out.println("结果：" + future1.get() + "\n");

		// 3.2 handle（无论成功失败都处理）
		System.out.println("【3.2 handle - 同时处理成功和失败】\n");

		CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
			if (System.currentTimeMillis() % 2 == 0) {
				throw new RuntimeException("任务失败");
			}
			return "任务成功";
		}).handle((result, ex) -> {
			if (ex != null) {
				System.out.println("  处理异常：" + ex.getMessage());
				return "异常处理后的结果";
			} else {
				System.out.println("  处理成功：" + result);
				return result + " - 已处理";
			}
		});

		System.out.println("最终结果：" + future2.get() + "\n");

		// 3.3 whenComplete（回调处理）
		System.out.println("【3.3 whenComplete - 任务完成回调】\n");

		CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
			System.out.println("  计算中...");
			return 10 / 2;
		}).whenComplete((result, ex) -> {
			if (ex != null) {
				System.out.println("  任务失败：" + ex.getMessage());
			} else {
				System.out.println("  任务成功，结果：" + result);
			}
		});

		System.out.println("返回值：" + future3.get() + "\n");
	}

	// ==================== 第四部分：ForkJoin框架 ====================
	static void part4_ForkJoinFramework() throws Exception {
		System.out.println("=== 第四部分：ForkJoin 框架 ===\n");

		System.out.println("【什么是ForkJoin？】");
		System.out.println("分治算法的并行实现框架：");
		System.out.println("  • Fork：将大任务分解成小任务");
		System.out.println("  • Join：合并小任务的结果");
		System.out.println("  • 工作窃取：空闲线程偷取其他线程的任务\n");

		// 4.1 RecursiveTask示例：求和
		System.out.println("【4.1 示例：并行计算数组求和】\n");

		class SumTask extends RecursiveTask<Long> {
			private static final int THRESHOLD = 10000; // 阈值
			private long[] array;
			private int start;
			private int end;

			public SumTask(long[] array, int start, int end) {
				this.array = array;
				this.start = start;
				this.end = end;
			}

			@Override
			protected Long compute() {
				int length = end - start;

				// 任务足够小，直接计算
				if (length <= THRESHOLD) {
					long sum = 0;
					for (int i = start; i < end; i++) {
						sum += array[i];
					}
					return sum;
				}

				// 任务太大，分解成两个子任务
				int middle = start + length / 2;
				SumTask leftTask = new SumTask(array, start, middle);
				SumTask rightTask = new SumTask(array, middle, end);

				// Fork：异步执行左右任务
				leftTask.fork();
				rightTask.fork();

				// Join：等待并获取结果
				long leftResult = leftTask.join();
				long rightResult = rightTask.join();

				return leftResult + rightResult;
			}
		}

		// 创建大数组
		int size = 100_000;
		long[] array = new long[size];
		for (int i = 0; i < size; i++) {
			array[i] = i + 1;
		}

		// 传统方式求和
		long start1 = System.currentTimeMillis();
		long sum1 = 0;
		for (long num : array) {
			sum1 += num;
		}
		long end1 = System.currentTimeMillis();
		System.out.println("传统方式：");
		System.out.println("  结果：" + sum1);
		System.out.println("  耗时：" + (end1 - start1) + "ms\n");

		// ForkJoin方式
		ForkJoinPool pool = new ForkJoinPool();
		long start2 = System.currentTimeMillis();
		long sum2 = pool.invoke(new SumTask(array, 0, array.length));
		long end2 = System.currentTimeMillis();
		System.out.println("ForkJoin方式：");
		System.out.println("  结果：" + sum2);
		System.out.println("  耗时：" + (end2 - start2) + "ms\n");

		// 4.2 RecursiveAction示例：并行排序
		System.out.println("【4.2 示例：并行快速排序】\n");

		class QuickSortTask extends RecursiveAction {
			private int[] array;
			private int left;
			private int right;

			public QuickSortTask(int[] array, int left, int right) {
				this.array = array;
				this.left = left;
				this.right = right;
			}

			@Override
			protected void compute() {
				if (left >= right) return;

				int pivot = partition(array, left, right);

				// 分解成两个子任务并行执行
				QuickSortTask leftTask = new QuickSortTask(array, left, pivot - 1);
				QuickSortTask rightTask = new QuickSortTask(array, pivot + 1, right);

				invokeAll(leftTask, rightTask);
			}

			private int partition(int[] arr, int left, int right) {
				int pivot = arr[right];
				int i = left - 1;

				for (int j = left; j < right; j++) {
					if (arr[j] <= pivot) {
						i++;
						int temp = arr[i];
						arr[i] = arr[j];
						arr[j] = temp;
					}
				}

				int temp = arr[i + 1];
				arr[i + 1] = arr[right];
				arr[right] = temp;

				return i + 1;
			}
		}

		int[] arr = {5, 2, 8, 1, 9, 3, 7, 4, 6};
		System.out.println("排序前：" + Arrays.toString(arr));

		pool.invoke(new QuickSortTask(arr, 0, arr.length - 1));
		System.out.println("排序后：" + Arrays.toString(arr) + "\n");

		System.out.println("ForkJoin关键点：");
		System.out.println("  • RecursiveTask<V> - 有返回值");
		System.out.println("  • RecursiveAction - 无返回值");
		System.out.println("  • fork() - 异步执行");
		System.out.println("  • join() - 获取结果");
		System.out.println("  • invokeAll() - 批量执行\n");
	}

	// ==================== 第五部分：实战案例 ====================
	static void part5_RealWorldExamples() throws Exception {
		System.out.println("=== 第五部分：实战案例 ===\n");

		// 案例1：电商系统商品详情页
		System.out.println("【案例1：电商商品详情页并行加载】\n");

		long caseStart = System.currentTimeMillis();

		// 并行查询多个服务
		CompletableFuture<String> productInfo = CompletableFuture.supplyAsync(() -> {
			System.out.println("  查询商品基本信息...");
			sleep(300);
			return "iPhone 15 Pro";
		});

		CompletableFuture<Double> priceInfo = CompletableFuture.supplyAsync(() -> {
			System.out.println("  查询价格信息...");
			sleep(200);
			return 7999.0;
		});

		CompletableFuture<Integer> stockInfo = CompletableFuture.supplyAsync(() -> {
			System.out.println("  查询库存信息...");
			sleep(250);
			return 100;
		});

		CompletableFuture<List<String>> reviewsInfo = CompletableFuture.supplyAsync(() -> {
			System.out.println("  查询评论信息...");
			sleep(400);
			return Arrays.asList("很好", "不错", "推荐");
		});

		CompletableFuture<String> recommendInfo = CompletableFuture.supplyAsync(() -> {
			System.out.println("  查询推荐商品...");
			sleep(350);
			return "相关推荐：iPhone 15";
		});

		// 等待所有查询完成
		CompletableFuture.allOf(productInfo, priceInfo, stockInfo,
				reviewsInfo, recommendInfo).join();

		long caseEnd = System.currentTimeMillis();

		System.out.println("\n商品详情：");
		System.out.println("  商品：" + productInfo.get());
		System.out.println("  价格：¥" + priceInfo.get());
		System.out.println("  库存：" + stockInfo.get());
		System.out.println("  评论：" + reviewsInfo.get());
		System.out.println("  推荐：" + recommendInfo.get());
		System.out.println("\n总耗时：" + (caseEnd - caseStart) + "ms");
		System.out.println("（如果串行执行需要：300+200+250+400+350=1500ms）\n");

		// 案例2：批量数据处理
		System.out.println("【案例2：批量处理用户数据】\n");

		List<Integer> userIds = IntStream.range(1, 21)
				.boxed()
				.collect(Collectors.toList());

		System.out.println("处理20个用户数据...\n");

		long processStart = System.currentTimeMillis();

		List<CompletableFuture<String>> futures = userIds.stream()
				.map(userId -> CompletableFuture.supplyAsync(() -> {
					// 模拟处理每个用户
					sleep(100);
					return "用户" + userId + "已处理";
				}))
				.collect(Collectors.toList());

		// 等待所有处理完成
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		List<String> processResults = futures.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList());

		long processEnd = System.currentTimeMillis();

		System.out.println("处理结果：" + processResults.size() + "个用户");
		System.out.println("总耗时：" + (processEnd - processStart) + "ms");
		System.out.println("（串行需要：20 × 100 = 2000ms）\n");
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