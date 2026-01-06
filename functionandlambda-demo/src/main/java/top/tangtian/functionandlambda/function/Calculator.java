package top.tangtian.functionandlambda.function;

/**
 * @author tangtian
 * @date 2026-01-05 09:57
 */
@FunctionalInterface
public interface Calculator {
	// 唯一的抽象方法
	int calculate(int a, int b);

	// 可以有默认方法
	default void printResult(int a, int b) {
		System.out.println("Result: " + calculate(a, b));
	}

	// 可以有静态方法
	static void info() {
		System.out.println("This is a Calculator interface");
	}
}
