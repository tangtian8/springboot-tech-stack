package top.tangtian.functionandlambda.lambda;

/**
 * @author tangtian
 * @date 2026-01-05 10:00
 */
// 自定义函数式接口
@FunctionalInterface
public interface MathOperation {
	int calculate(int a, int b);

	// 默认方法
	default String getOperationName() {
		return "数学运算";
	}

	// 静态方法
	static void printInfo() {
		System.out.println("这是一个数学运算接口");
	}
}
