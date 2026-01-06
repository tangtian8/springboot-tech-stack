package top.tangtian.functionandlambda.lambda;

/**
 * @author tangtian
 * @date 2026-01-05 10:00
 */
public class LambdaPerson {
	String name;
	int age;
	int salary;

	LambdaPerson(String name, int age, int salary) {
		this.name = name;
		this.age = age;
		this.salary = salary;
	}

	@Override
	public String toString() {
		return name + "(" + age + "岁, ¥" + salary + ")";
	}
}