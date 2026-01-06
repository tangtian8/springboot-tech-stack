package top.tangtian.functionandlambda.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author tangtian
 * @date 2026-01-05 09:57
 */
public class LambdaAndFunctionalInterface {
	public static void main(String[] args) {
		System.out.println("=== 1. 函数式接口的定义 ===");
		functionalInterfaceDefinition();

		System.out.println("\n=== 2. Lambda 是函数式接口的实现 ===");
		lambdaAsImplementation();

		System.out.println("\n=== 3. 三种实现方式对比 ===");
		threeImplementationWays();

		System.out.println("\n=== 4. Lambda 的本质 ===");
		lambdaEssence();

		System.out.println("\n=== 5. 函数式接口的要求 ===");
		functionalInterfaceRequirements();
	}

	// 1. 函数式接口的定义
	static void functionalInterfaceDefinition() {
		System.out.println("函数式接口 = 只有一个抽象方法的接口");
		System.out.println("可以用 @FunctionalInterface 注解标记（可选但推荐）\n");

		// 示例：自定义函数式接口
		System.out.println("自定义函数式接口示例:");
		System.out.println("@FunctionalInterface");
		System.out.println("interface Calculator {");
		System.out.println("    int calculate(int a, int b);  // 唯一的抽象方法");
		System.out.println("}");
	}

	// 2. Lambda 是函数式接口的实现
	static void lambdaAsImplementation() {
		System.out.println("关键理解：Lambda 表达式是函数式接口的一个实例\n");

		// Lambda 实现 Calculator 接口
		Calculator add = (a, b) -> a + b;
		Calculator multiply = (a, b) -> a * b;

		System.out.println("add 的类型: " + add.getClass().getName());
		System.out.println("10 + 5 = " + add.calculate(10, 5));
		System.out.println("10 * 5 = " + multiply.calculate(10, 5));

		// Lambda 实现 Java 内置函数式接口
		Predicate<String> isEmpty = str -> str.isEmpty();
		Function<Integer, String> intToStr = num -> "Number: " + num;

		System.out.println("\nempty string? " + isEmpty.test(""));
		System.out.println(intToStr.apply(42));
	}

	// 3. 三种实现方式对比
	static void threeImplementationWays() {
		System.out.println("实现函数式接口的三种方式:\n");

		// 方式1: 传统匿名内部类
		Calculator calc1 = new Calculator() {
			@Override
			public int calculate(int a, int b) {
				return a + b;
			}
		};
		System.out.println("1. 匿名内部类: " + calc1.calculate(3, 4));

		// 方式2: Lambda 表达式（推荐）
		Calculator calc2 = (a, b) -> a + b;
		System.out.println("2. Lambda 表达式: " + calc2.calculate(3, 4));

		// 方式3: 方法引用
		Calculator calc3 = LambdaAndFunctionalInterface::addMethod;
		System.out.println("3. 方法引用: " + calc3.calculate(3, 4));

		System.out.println("\n三者本质相同，都是函数式接口的实例!");
		System.out.println("Lambda 是最简洁的写法");
	}

	static int addMethod(int a, int b) {
		return a + b;
	}

	// 4. Lambda 的本质
	static void lambdaEssence() {
		System.out.println("Lambda 的本质理解:\n");

		// Lambda 表达式的组成
		System.out.println("Lambda = (参数列表) -> { 方法体 }");
		System.out.println("实际上是实现了函数式接口的唯一抽象方法\n");

		// 类型推断
		Calculator calc = (a, b) -> a - b;  // 编译器推断出这是 Calculator 类型
		System.out.println("编译器会自动推断 Lambda 的类型");
		System.out.println("这里推断为: Calculator");
		System.out.println("因为 Lambda 的签名匹配 calculate(int, int) 方法\n");

		// Lambda 必须有对应的函数式接口
		// 以下代码无法编译，因为没有对应的函数式接口
		// var lambda = (a, b) -> a + b;  // 错误！无法推断类型

		System.out.println("重要：Lambda 不能独立存在，必须有函数式接口作为目标类型!");
	}

	// 5. 函数式接口的要求
	static void functionalInterfaceRequirements() {
		System.out.println("函数式接口的规则:\n");

		System.out.println("✓ 必须：有且仅有一个抽象方法");
		System.out.println("✓ 可以：有多个默认方法 (default)");
		System.out.println("✓ 可以：有多个静态方法 (static)");
		System.out.println("✓ 可以：继承 Object 类的方法\n");

		// 演示合法的函数式接口
		MyFunctionalInterface impl = x -> x * 2;
		System.out.println("调用抽象方法: " + impl.abstractMethod(5));
		System.out.println("调用默认方法: " + impl.defaultMethod());
		System.out.println("调用静态方法: " + MyFunctionalInterface.staticMethod());

		System.out.println("\n演示不同场景:");

		// 场景1: 使用 Lambda
		processWithCalculator((a, b) -> a * b, 4, 5);

		// 场景2: 使用方法引用
		processWithCalculator(Math::max, 10, 20);

		// 场景3: 传递 Lambda 作为参数
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
		System.out.println("\n过滤偶数:");
		filterList(numbers, n -> n % 2 == 0).forEach(System.out::println);
	}

	static void processWithCalculator(Calculator calc, int a, int b) {
		System.out.println("结果: " + calc.calculate(a, b));
	}

	static <T> List<T> filterList(List<T> list, Predicate<T> predicate) {
		List<T> result = new ArrayList<>();
		for (T item : list) {
			if (predicate.test(item)) {
				result.add(item);
			}
		}
		return result;
	}
}
