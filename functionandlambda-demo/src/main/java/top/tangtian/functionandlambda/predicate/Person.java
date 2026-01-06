package top.tangtian.functionandlambda.predicate;

/**
 * @author tangtian
 * @date 2026-01-05 09:56
 */
public class Person {
	private String name;
	private int age;
	private int salary;

	public Person(String name, int age, int salary) {
		this.name = name;
		this.age = age;
		this.salary = salary;
	}

	public String getName() { return name; }
	public int getAge() { return age; }
	public int getSalary() { return salary; }

	@Override
	public String toString() {
		return name + "(年龄:" + age + ", 薪水:" + salary + ")";
	}
}
