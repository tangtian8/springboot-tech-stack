package top.tangtian.factorybean;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tangtian
 * @date 2025-12-17 19:32
 */
// 使用
@Component
public class ServiceUser {

	@Autowired
	private MyService myService;  // 注入的是 FactoryBean 创建的对象

	@Autowired
	private MyServiceFactoryBean myServiceFactoryBean;  // 使用 & 前缀获取 FactoryBean 本身

	@PostConstruct
	public void test() {
		myService.doSomething();
	}
}