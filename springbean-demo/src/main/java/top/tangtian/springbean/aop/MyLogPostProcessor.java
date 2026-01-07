package top.tangtian.springbean.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * @author tangtian
 * @date 2025-12-18 09:59
 */
@Component
public class MyLogPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// 1. 检查类上是否有我们的自定义注解
		if (bean.getClass().isAnnotationPresent(MyLog.class)) {
			System.out.println("检测到需要增强的 Bean: " + beanName);

			// 2. 创建动态代理对象并返回
			return Proxy.newProxyInstance(
					bean.getClass().getClassLoader(),
					bean.getClass().getInterfaces(), // 注意：JDK 代理要求 Bean 必须实现接口
					(proxy, method, args) -> {
						long start = System.currentTimeMillis();

						// 执行原对象的方法
						Object result = method.invoke(bean, args);

						long end = System.currentTimeMillis();
						System.out.println("方法 [" + method.getName() + "] 执行耗时: " + (end - start) + "ms");
						return result;
					}
			);
		}
		// 如果不需要增强，直接返回原对象
		return bean;
	}
}