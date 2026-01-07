package top.tangtian.springbean.factorybean;

/**
 * @author tangtian
 * @date 2025-12-17 20:02
 */


class AdvancedMyService implements MyService {
	@Override
	public void doSomething() {
		System.out.println("Advanced implementation");
	}
}