package top.tangtian.functionandlambda.function;

/**
 * @author tangtian
 * @date 2026-01-05 09:58
 */
@FunctionalInterface
public interface MyFunctionalInterface {
	// 唯一的抽象方法
	int abstractMethod(int x);

	// 可以有多个默认方法
	default String defaultMethod() {
		return "这是默认方法";
	}

	default String anotherDefault() {
		return "另一个默认方法";
	}

	// 可以有多个静态方法
	static String staticMethod() {
		return "这是静态方法";
	}

	static void anotherStatic() {
		System.out.println("另一个静态方法");
	}

	// 可以有 Object 类的方法（不算抽象方法）
	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();
}
