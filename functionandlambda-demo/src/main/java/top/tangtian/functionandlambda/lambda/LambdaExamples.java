package top.tangtian.functionandlambda.lambda;

/**
 * @author tangtian
 * @date 2026-01-04 13:18
 */
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * 2. 常用函数式接口
 * 接口方法签名用途Function<T, R>R apply(T t)转换数据Predicate<T>boolean test(T t)判断条件Consumer<T>void accept(T t)消费数据Supplier<T>T get()提供数据BiFunction<T,U,R>R apply(T t, U u)两参数转换
 */

public class LambdaExamples {

	public static void main(String[] args) {
		System.out.println("=== 1. Lambda 基本语法 ===");
		basicLambdaSyntax();

		System.out.println("\n=== 2. 常用函数式接口 ===");
		functionalInterfaces();

		System.out.println("\n=== 3. 方法引用 ===");
		methodReferences();

		System.out.println("\n=== 4. Stream API 实战 ===");
		streamApiExamples();

		System.out.println("\n=== 5. 自定义函数式接口 ===");
		customFunctionalInterface();
	}

	// 1. Lambda 基本语法示例
	static void basicLambdaSyntax() {
		// 无参数
		Runnable r1 = () -> System.out.println("Hello Lambda!");
		r1.run();

		// 单个参数(可省略括号)
		Consumer<String> c1 = (s) -> System.out.println("输入: " + s);
		c1.accept("Lambda");

		// 多个参数
		BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
		System.out.println("5 + 3 = " + add.apply(5, 3));

		// 多行代码块
		BiFunction<Integer, Integer, Integer> multiply = (a, b) -> {
			int result = a * b;
			System.out.println("计算: " + a + " * " + b);
			return result;
		};
		System.out.println("结果: " + multiply.apply(4, 6));
	}

	// 2. 常用函数式接口
	static void functionalInterfaces() {
		// Function<T, R>: 接受一个参数,返回结果
		Function<String, Integer> strLength = str -> str.length();
		System.out.println("字符串长度: " + strLength.apply("Hello"));

		// Predicate<T>: 接受参数,返回 boolean
		Predicate<Integer> isEven = num -> num % 2 == 0;
		System.out.println("10 是偶数? " + isEven.test(10));

		// Consumer<T>: 接受参数,无返回值
		Consumer<String> printer = msg -> System.out.println("消息: " + msg);
		printer.accept("函数式编程");

		// Supplier<T>: 无参数,返回结果
		Supplier<Double> randomNum = () -> Math.random();
		System.out.println("随机数: " + randomNum.get());

		// BiFunction<T, U, R>: 接受两个参数,返回结果
		BiFunction<String, String, String> concat = (s1, s2) -> s1 + " " + s2;
		System.out.println(concat.apply("Hello", "World"));

		// UnaryOperator<T>: Function 的特例,输入输出类型相同
		UnaryOperator<Integer> square = x -> x * x;
		System.out.println("平方: " + square.apply(5));

		// BinaryOperator<T>: BiFunction 的特例,两个输入和输出类型相同
		BinaryOperator<Integer> max = (a, b) -> a > b ? a : b;
		System.out.println("最大值: " + max.apply(10, 20));
	}

	// 3. 方法引用
	static void methodReferences() {
		List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

		// 静态方法引用
		names.forEach(System.out::println);

		// 实例方法引用
		String prefix = "Name: ";
		Consumer<String> printWithPrefix = prefix::concat;

		// 构造器引用
		Supplier<List<String>> listSupplier = ArrayList::new;
		List<String> newList = listSupplier.get();

		// 类型方法引用
		Function<String, Integer> parser = Integer::parseInt;
		System.out.println("解析: " + parser.apply("123"));
	}

	// 4. Stream API 实战
	static void streamApiExamples() {
		List<LambdaPerson> people = Arrays.asList(
				new LambdaPerson("Alice", 25, 5000),
				new LambdaPerson("Bob", 30, 6000),
				new LambdaPerson("Charlie", 35, 7000),
				new LambdaPerson("David", 28, 5500),
				new LambdaPerson("Eve", 32, 6500)
		);

		// 过滤、映射、排序
		System.out.println("年龄大于28且薪水大于5500的人员名单:");
		people.stream()
				.filter(p -> p.age > 28)
				.filter(p -> p.salary > 5500)
				.sorted(Comparator.comparing(p -> p.age))
				.map(p -> p.name)
				.forEach(System.out::println);

		// 统计操作
		double avgSalary = people.stream()
				.mapToDouble(p -> p.salary)
				.average()
				.orElse(0);
		System.out.println("平均薪水: " + avgSalary);

		// 分组
		Map<Boolean, List<LambdaPerson>> partitioned = people.stream()
				.collect(Collectors.partitioningBy(p -> p.age >= 30));
		System.out.println("30岁以上: " + partitioned.get(true).size() + "人");

		// 归约
		int totalSalary = people.stream()
				.map(p -> p.salary)
				.reduce(0, Integer::sum);
		System.out.println("总薪水: " + totalSalary);

		// findFirst 和 Optional
		Optional<LambdaPerson> youngest = people.stream()
				.min(Comparator.comparing(p -> p.age));
		youngest.ifPresent(p -> System.out.println("最年轻: " + p.name));
	}

	// 5. 自定义函数式接口
	static void customFunctionalInterface() {
		// 使用自定义函数式接口
		MathOperation addition = (a, b) -> a + b;
		MathOperation subtraction = (a, b) -> a - b;

		System.out.println("加法: " + operate(10, 5, addition));
		System.out.println("减法: " + operate(10, 5, subtraction));

		// 使用默认方法
		System.out.println("操作名称: " + addition.getOperationName());
	}

	static int operate(int a, int b, MathOperation operation) {
		return operation.calculate(a, b);
	}
}





