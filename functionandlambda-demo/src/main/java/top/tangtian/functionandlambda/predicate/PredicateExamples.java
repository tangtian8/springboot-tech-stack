package top.tangtian.functionandlambda.predicate;



import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author tangtian
 * @date 2026-01-05 09:54
 */
public class PredicateExamples {

	public static void main(String[] args) {
		System.out.println("=== 1. Predicate<T> 基础用法 ===");
		predicateBasics();

		System.out.println("\n=== 2. Predicate 组合操作 ===");
		predicateCombination();

		System.out.println("\n=== 3. BiPredicate<T, U> 基础用法 ===");
		biPredicateBasics();

		System.out.println("\n=== 4. BiPredicate 组合操作 ===");
		biPredicateCombination();

		System.out.println("\n=== 5. 实战应用场景 ===");
		practicalExamples();
	}

	// 1. Predicate<T> 基础用法
	// Predicate<T>: 接受一个参数，返回 boolean
	static void predicateBasics() {
		// 基本使用
		Predicate<Integer> isPositive = num -> num > 0;
		System.out.println("5 是正数? " + isPositive.test(5));
		System.out.println("-3 是正数? " + isPositive.test(-3));

		// 字符串判断
		Predicate<String> isEmpty = str -> str.isEmpty();
		Predicate<String> isLong = str -> str.length() > 10;
		System.out.println("\"\" 是空字符串? " + isEmpty.test(""));
		System.out.println("\"Hello\" 长度>10? " + isLong.test("Hello"));

		// 对象判断
		Predicate<Person> isAdult = person -> person.getAge() >= 18;
		Person alice = new Person("Alice", 25, 5000);
		Person bob = new Person("Bob", 16, 0);
		System.out.println(alice.getName() + " 是成年人? " + isAdult.test(alice));
		System.out.println(bob.getName() + " 是成年人? " + isAdult.test(bob));
	}

	// 2. Predicate 组合操作
	static void predicateCombination() {
		Predicate<Integer> isEven = num -> num % 2 == 0;
		Predicate<Integer> isPositive = num -> num > 0;
		Predicate<Integer> isGreaterThan10 = num -> num > 10;

		// and() - 逻辑与
		Predicate<Integer> isPositiveEven = isPositive.and(isEven);
		System.out.println("8 是正偶数? " + isPositiveEven.test(8));
		System.out.println("-4 是正偶数? " + isPositiveEven.test(-4));

		// or() - 逻辑或
		Predicate<Integer> isEvenOrGreaterThan10 = isEven.or(isGreaterThan10);
		System.out.println("7 是偶数或>10? " + isEvenOrGreaterThan10.test(7));
		System.out.println("15 是偶数或>10? " + isEvenOrGreaterThan10.test(15));

		// negate() - 逻辑非
		Predicate<Integer> isOdd = isEven.negate();
		System.out.println("7 是奇数? " + isOdd.test(7));

		// 复杂组合: (正数 AND 偶数) OR (大于10)
		Predicate<Integer> complex = isPositive.and(isEven).or(isGreaterThan10);
		System.out.println("组合判断 -12: " + complex.test(-12));
		System.out.println("组合判断 8: " + complex.test(8));
		System.out.println("组合判断 15: " + complex.test(15));

		// isEqual() - 静态方法，判断相等
		Predicate<String> isHello = Predicate.isEqual("Hello");
		System.out.println("\"Hello\" equals \"Hello\"? " + isHello.test("Hello"));
		System.out.println("\"World\" equals \"Hello\"? " + isHello.test("World"));
	}

	// 3. BiPredicate<T, U> 基础用法
	// BiPredicate<T, U>: 接受两个参数，返回 boolean
	static void biPredicateBasics() {
		// 数值比较
		BiPredicate<Integer, Integer> isGreater = (a, b) -> a > b;
		System.out.println("10 > 5? " + isGreater.test(10, 5));
		System.out.println("3 > 8? " + isGreater.test(3, 8));

		// 字符串比较
		BiPredicate<String, String> startsWith = (str, prefix) -> str.startsWith(prefix);
		System.out.println("\"Hello\" 以 \"He\" 开头? " + startsWith.test("Hello", "He"));

		// 字符串包含
		BiPredicate<String, String> contains = (str, sub) -> str.contains(sub);
		System.out.println("\"Hello World\" 包含 \"Wo\"? " + contains.test("Hello World", "Wo"));

		// 对象比较
		BiPredicate<Person, Integer> ageLessThan = (person, age) -> person.getAge() < age;
		Person charlie = new Person("Charlie", 25, 6000);
		System.out.println(charlie.getName() + " 年龄<30? " + ageLessThan.test(charlie, 30));

		// 两个对象的比较
		BiPredicate<Person, Person> isSameAge = (p1, p2) -> p1.getAge() == p2.getAge();
		Person david = new Person("David", 25, 5500);
		System.out.println("Charlie 和 David 同龄? " + isSameAge.test(charlie, david));
	}

	// 4. BiPredicate 组合操作
	static void biPredicateCombination() {
		BiPredicate<Integer, Integer> isGreater = (a, b) -> a > b;
		BiPredicate<Integer, Integer> isEqual = (a, b) -> a.equals(b);
		BiPredicate<Integer, Integer> bothPositive = (a, b) -> a > 0 && b > 0;

		// and() - 逻辑与
		BiPredicate<Integer, Integer> greaterAndPositive = isGreater.and(bothPositive);
		System.out.println("10>5 且都为正? " + greaterAndPositive.test(10, 5));
		System.out.println("10>-5 且都为正? " + greaterAndPositive.test(10, -5));

		// or() - 逻辑或
		BiPredicate<Integer, Integer> greaterOrEqual = isGreater.or(isEqual);
		System.out.println("10>=10? " + greaterOrEqual.test(10, 10));
		System.out.println("5>=10? " + greaterOrEqual.test(5, 10));

		// negate() - 逻辑非
		BiPredicate<Integer, Integer> isNotGreater = isGreater.negate();
		System.out.println("10 不大于 5? " + isNotGreater.test(10, 5));
		System.out.println("3 不大于 8? " + isNotGreater.test(3, 8));
	}

	// 5. 实战应用场景
	static void practicalExamples() {
		List<Person> employees = Arrays.asList(
				new Person("Alice", 25, 5000),
				new Person("Bob", 30, 6000),
				new Person("Charlie", 35, 7000),
				new Person("David", 28, 5500),
				new Person("Eve", 32, 6500)
		);

		System.out.println("--- Predicate 在 Stream 中的应用 ---");

		// 单一条件过滤
		Predicate<Person> highSalary = p -> p.getSalary() > 6000;
		System.out.println("高薪员工:");
		employees.stream()
				.filter(highSalary)
				.forEach(System.out::println);

		// 复合条件过滤
		Predicate<Person> isYoung = p -> p.getAge() < 30;
		Predicate<Person> isWellPaid = p -> p.getSalary() >= 5500;
		System.out.println("\n年轻且薪水不错的员工:");
		employees.stream()
				.filter(isYoung.and(isWellPaid))
				.forEach(System.out::println);

		// 自定义过滤方法
		System.out.println("\n使用自定义过滤方法:");
		List<Person> filtered = filterEmployees(employees,
				p -> p.getAge() >= 30 && p.getSalary() < 7000);
		filtered.forEach(System.out::println);

		System.out.println("\n--- BiPredicate 实际应用 ---");

		// 比较两个员工
		BiPredicate<Person, Person> earnMoreThan =
				(p1, p2) -> p1.getSalary() > p2.getSalary();

		Person alice = employees.get(0);
		Person charlie = employees.get(2);
		System.out.println(alice.getName() + " 比 " + charlie.getName() +
				" 薪水高? " + earnMoreThan.test(alice, charlie));

		// 验证操作
		BiPredicate<Person, Integer> canAfford =
				(person, price) -> person.getSalary() > price * 12;
		System.out.println(charlie.getName() + " 能负担月租500? " +
				canAfford.test(charlie, 500));

		// 查找匹配的配对
		System.out.println("\n年龄相差不超过3岁的员工配对:");
		findMatchingPairs(employees,
				(p1, p2) -> Math.abs(p1.getAge() - p2.getAge()) <= 3);
	}

	// 工具方法：使用 Predicate 过滤列表
	static List<Person> filterEmployees(List<Person> employees, Predicate<Person> condition) {
		return employees.stream()
				.filter(condition)
				.collect(Collectors.toList());
	}

	// 工具方法：使用 BiPredicate 查找配对
	static void findMatchingPairs(List<Person> employees, BiPredicate<Person, Person> matcher) {
		for (int i = 0; i < employees.size(); i++) {
			for (int j = i + 1; j < employees.size(); j++) {
				if (matcher.test(employees.get(i), employees.get(j))) {
					System.out.println("  " + employees.get(i).getName() +
							" <-> " + employees.get(j).getName());
				}
			}
		}
	}
}
