package top.tangtian.springbean.runner;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tangtian
 * @date 2025-12-17 19:35
 */
// ApplicationContextInitializer - 需要在 spring.factories 中注册
public class MyApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext context) {
		System.out.println("ApplicationContextInitializer - 容器初始化前");

		// 可以添加 BeanFactoryPostProcessor
		// 可以添加 ApplicationListener
		// 可以设置环境变量
		ConfigurableEnvironment environment = context.getEnvironment();
		Map<String, Object> props = new HashMap<>();
		props.put("custom.property", "value");
		environment.getPropertySources().addFirst(
				new MapPropertySource("customProperties", props)
		);
	}
}