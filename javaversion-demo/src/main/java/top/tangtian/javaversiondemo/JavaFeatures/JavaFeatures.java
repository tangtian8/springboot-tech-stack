package top.tangtian.javaversiondemo.JavaFeatures;

/**
 * @author tangtian
 * @date 2026-01-07 12:43
 */
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * Java 11-21 特性综合演示
 * 编译命令: javac --enable-preview --release 21 JavaFeatures.java
 * 运行命令: java --enable-preview JavaFeatures
 */
public class JavaFeatures {

	public static void main(String[] args) {
		System.out.println("=== Java 11-21 特性演示 ===\n");

		// Java 11 特性
		demonstrateJava11Features();

		// Java 12-13 特性
		demonstrateJava12_13Features();

		// Java 14 特性
		demonstrateJava14Features();

		// Java 15 特性
		demonstrateJava15Features();

		// Java 16 特性
		demonstrateJava16Features();

		// Java 17 特性
		demonstrateJava17Features();

		// Java 21 特性
		demonstrateJava21Features();
	}

	// ========== Java 11 特性 ==========
	static void demonstrateJava11Features() {
		System.out.println("【Java 11 特性】");

		// 1. 新的字符串方法
		System.out.println("1. 新的字符串方法:");
		String text = "  Hello World  ";
		System.out.println("  isBlank(): " + "   ".isBlank());
		System.out.println("  lines(): " + "line1\nline2\nline3".lines().count());
		System.out.println("  strip(): '" + text.strip() + "'");
		System.out.println("  repeat(3): " + "AB".repeat(3));

		// 2. Lambda 参数的局部变量语法 (var)
		System.out.println("\n2. Lambda 中使用 var:");
		List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
		names.forEach((var name) -> System.out.println("  " + name));

		// 3. 新的 HTTP Client API
		System.out.println("\n3. HTTP Client API (同步):");
		try {
			HttpClient client = HttpClient.newBuilder()
					.connectTimeout(Duration.ofSeconds(5))
					.build();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://httpbin.org/get"))
					.GET()
					.build();
			HttpResponse<String> response = client.send(request,
					HttpResponse.BodyHandlers.ofString());
			System.out.println("  状态码: " + response.statusCode());
		} catch (Exception e) {
			System.out.println("  请求失败: " + e.getMessage());
		}

		// 4. 文件读写新方法
		System.out.println("\n4. 文件读写新方法:");
		try {
			java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test", ".txt");
			java.nio.file.Files.writeString(tempFile, "Hello from Java 11!");
			String content = java.nio.file.Files.readString(tempFile);
			System.out.println("  读取内容: " + content);
			java.nio.file.Files.delete(tempFile);
		} catch (IOException e) {
			System.out.println("  文件操作失败");
		}

		System.out.println();
	}

	// ========== Java 12-13 特性 ==========
	static void demonstrateJava12_13Features() {
		System.out.println("【Java 12-13 特性】");

		// 1. Switch 表达式 (Java 12 预览, Java 14 正式)
		System.out.println("1. Switch 表达式:");
		String day = "MONDAY";
		int numLetters = switch (day) {
			case "MONDAY", "FRIDAY", "SUNDAY" -> 6;
			case "TUESDAY" -> 7;
			case "THURSDAY", "SATURDAY" -> 8;
			case "WEDNESDAY" -> 9;
			default -> throw new IllegalStateException("无效的日期: " + day);
		};
		System.out.println("  " + day + " 有 " + numLetters + " 个字母");

		// 2. 文本块 (Java 13 预览, Java 15 正式)
		System.out.println("\n2. 文本块 (Text Blocks):");
		String json = """
                {
                    "name": "张三",
                    "age": 25,
                    "city": "北京"
                }
                """;
		System.out.println(json);

		System.out.println();
	}

	// ========== Java 14 特性 ==========
	static void demonstrateJava14Features() {
		System.out.println("【Java 14 特性】");

		// 1. instanceof 模式匹配 (预览)
		System.out.println("1. instanceof 模式匹配:");
		Object obj = "Hello Java 14";
		if (obj instanceof String str) {
			System.out.println("  字符串长度: " + str.length());
			System.out.println("  转大写: " + str.toUpperCase());
		}

		// 2. Records (预览, Java 16 正式)
		System.out.println("\n2. Records (在 Java 16 中正式)");
		System.out.println("  详见 Java 16 演示");

		// 3. Helpful NullPointerExceptions
		System.out.println("\n3. 更有帮助的 NPE 信息:");
		System.out.println("  Java 14+ 会准确指出哪个变量为 null");

		System.out.println();
	}

	// ========== Java 15 特性 ==========
	static void demonstrateJava15Features() {
		System.out.println("【Java 15 特性】");

		// 1. 文本块正式版
		System.out.println("1. 文本块 (正式版):");
		String html = """
                <html>
                    <body>
                        <h1>Java 15</h1>
                    </body>
                </html>
                """;
		System.out.println(html);

		// 2. Sealed Classes (预览, Java 17 正式)
		System.out.println("2. 密封类 (预览, Java 17 正式):");
		System.out.println("  允许限制哪些类可以继承/实现");

		System.out.println();
	}

	// ========== Java 16 特性 ==========
	static void demonstrateJava16Features() {
		System.out.println("【Java 16 特性】");

		// 1. Records (正式版)
		System.out.println("1. Records (正式版):");
		Person person = new Person("李四", 30);
		System.out.println("  Person: " + person);
		System.out.println("  姓名: " + person.name());
		System.out.println("  年龄: " + person.age());

		// 2. Pattern Matching for instanceof (正式版)
		System.out.println("\n2. instanceof 模式匹配 (正式版):");
		Object value = 42;
		if (value instanceof Integer num) {
			System.out.println("  整数值: " + num);
			System.out.println("  平方: " + (num * num));
		}

		// 3. Stream.toList()
		System.out.println("\n3. Stream.toList() 简化:");
		List<Integer> numbers = List.of(1, 2, 3, 4, 5);
		List<Integer> evenNumbers = numbers.stream()
				.filter(n -> n % 2 == 0)
				.toList();  // 比 .collect(Collectors.toList()) 更简洁
		System.out.println("  偶数: " + evenNumbers);

		System.out.println();
	}

	// ========== Java 17 特性 ==========
	static void demonstrateJava17Features() {
		System.out.println("【Java 17 (LTS) 特性】");

		// 1. Sealed Classes (正式版)
		System.out.println("1. 密封类 (正式版):");
		Shape circle = new Circle(5.0);
		double area = calculateArea(circle);
		System.out.println("  圆形面积: " + area);

		// 2. Pattern Matching for switch (预览)
		System.out.println("\n2. switch 的模式匹配 (预览):");
		Object testObj = "Hello";
		String result = formatObject(testObj);
		System.out.println("  结果: " + result);

		// 3. Enhanced Pseudo-Random Number Generators
		System.out.println("\n3. 增强的随机数生成器:");
		Random random = new Random();
		System.out.println("  随机整数: " + random.nextInt(100));

		System.out.println();
	}

	// ========== Java 21 特性 ==========
	static void demonstrateJava21Features() {
		System.out.println("【Java 21 (LTS) 特性】");

		// 1. Record Patterns (正式版)
		System.out.println("1. Record 模式 (正式版):");
		Point point = new Point(10, 20);
		if (point instanceof Point(int x, int y)) {
			System.out.println("  x = " + x + ", y = " + y);
		}

		// 2. Pattern Matching for switch (正式版)
		System.out.println("\n2. switch 模式匹配 (正式版):");
		System.out.println(describeObject("Hello"));
		System.out.println(describeObject(42));
		System.out.println(describeObject(3.14));
		System.out.println(describeObject(null));

		// 3. Sequenced Collections
		System.out.println("\n3. 有序集合 (Sequenced Collections):");
		List<String> list = new ArrayList<>(List.of("A", "B", "C"));
		System.out.println("  第一个: " + list.getFirst());
		System.out.println("  最后一个: " + list.getLast());
		System.out.println("  反转: " + list.reversed());

		// 4. String Templates (预览)
		System.out.println("\n4. 字符串模板 (预览):");
		String name = "Java";
		int version = 21;
		System.out.println("  " + name + " " + version); // 传统方式

		// 5. Virtual Threads (正式版)
		System.out.println("\n5. 虚拟线程 (正式版):");
		demonstrateVirtualThreads();

		System.out.println();
	}

	// ========== 辅助方法 ==========

	// Java 17: 密封类
	static double calculateArea(Shape shape) {
		return switch (shape) {
			case Circle c -> Math.PI * c.radius() * c.radius();
			case Rectangle r -> r.width() * r.height();
		};
	}

	// Java 17: switch 模式匹配
	static String formatObject(Object obj) {
		return switch (obj) {
			case String s -> "字符串: " + s;
			case Integer i -> "整数: " + i;
			default -> "其他类型";
		};
	}

	// Java 21: switch 模式匹配
	static String describeObject(Object obj) {
		return switch (obj) {
			case null -> "  这是 null";
			case String s when s.length() > 5 -> "  长字符串: " + s;
			case String s -> "  短字符串: " + s;
			case Integer i when i > 0 -> "  正整数: " + i;
			case Integer i -> "  非正整数: " + i;
			case Double d -> "  浮点数: " + d;
			default -> "  未知类型: " + obj.getClass().getSimpleName();
		};
	}

	// Java 21: 虚拟线程演示
	static void demonstrateVirtualThreads() {
		try {
			// 创建虚拟线程
			Thread vThread = Thread.ofVirtual().start(() -> {
				System.out.println("  虚拟线程 ID: " + Thread.currentThread().threadId());
				System.out.println("  虚拟线程执行中...");
			});
			vThread.join();

			// 使用虚拟线程执行大量任务
			System.out.println("  启动 10 个虚拟线程...");
			List<Thread> threads = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				int taskId = i;
				Thread thread = Thread.ofVirtual().start(() -> {
					// 模拟工作
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				});
				threads.add(thread);
			}

			for (Thread thread : threads) {
				thread.join();
			}
			System.out.println("  所有虚拟线程完成");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.out.println("  线程被中断");
		}
	}
}

// ========== Record 定义 (Java 16+) ==========
record Person(String name, int age) {
	// Records 自动生成 constructor, getters, equals, hashCode, toString
}

record Point(int x, int y) {}

// ========== Sealed Classes (Java 17+) ==========
sealed interface Shape permits Circle, Rectangle {}

record Circle(double radius) implements Shape {}

record Rectangle(double width, double height) implements Shape {}
