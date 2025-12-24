package top.tangtian.beaninteface;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author tangtian
 * @date 2025-12-17 19:30
 *  BeanPostProcessor 系列(Bean 后置处理器)
 *  // 1. BeanPostProcessor - 基础后置处理器
 * public interface BeanPostProcessor {
 *     // 初始化前处理
 *     default Object postProcessBeforeInitialization(Object bean, String beanName)
 *             throws BeansException {
 *         return bean;
 *     }
 *
 *     // 初始化后处理(AOP 代理在这里创建)
 *     default Object postProcessAfterInitialization(Object bean, String beanName)
 *             throws BeansException {
 *         return bean;
 *     }
 * }
 *
 * // 2. InstantiationAwareBeanPostProcessor - 实例化感知处理器
 * public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
 *     // 实例化前
 *     default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName)
 *             throws BeansException {
 *         return null;
 *     }
 *
 *     // 实例化后
 *     default boolean postProcessAfterInstantiation(Object bean, String beanName)
 *             throws BeansException {
 *         return true;
 *     }
 *
 *     // 属性注入
 *     default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
 *             throws BeansException {
 *         return null;
 *     }
 * }
 *
 * // 3. DestructionAwareBeanPostProcessor - 销毁感知处理器
 * public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {
 *     void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;
 *
 *     default boolean requiresDestruction(Object bean) {
 *         return true;
 *     }
 * }
 *
 * // 4. MergedBeanDefinitionPostProcessor - BeanDefinition 合并后处理器
 * public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {
 *     void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition,
 *                                          Class<?> beanType,
 *                                          String beanName);
 * }
 *
 * // 5. SmartInstantiationAwareBeanPostProcessor - 智能实例化处理器
 * public interface SmartInstantiationAwareBeanPostProcessor
 *         extends InstantiationAwareBeanPostProcessor {
 *
 *     // 预测 Bean 类型
 *     default Class<?> predictBeanType(Class<?> beanClass, String beanName)
 *             throws BeansException {
 *         return null;
 *     }
 *
 *     // 决定使用哪个构造函数
 *     default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
 *             throws BeansException {
 *         return null;
 *     }
 *
 *     // 获取早期引用(解决循环依赖)
 *     default Object getEarlyBeanReference(Object bean, String beanName)
 *             throws BeansException {
 *         return bean;
 *     }
 * }
 */
@Component
public class CustomBeanPostProcessor implements
		InstantiationAwareBeanPostProcessor,
		DestructionAwareBeanPostProcessor {

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName)
			throws BeansException {
		if (beanName.startsWith("my")) {
			System.out.println("实例化前: " + beanName);
		}
		return null;  // 返回 null 表示使用默认实例化
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName)
			throws BeansException {
		if (beanName.startsWith("my")) {
			System.out.println("实例化后: " + beanName);
		}
		return true;  // 返回 true 表示继续属性注入
	}

	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
			throws BeansException {
		if (beanName.startsWith("my")) {
			System.out.println("属性注入时: " + beanName);
		}
		return pvs;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if (beanName.startsWith("my")) {
			System.out.println("初始化前: " + beanName + " - @PostConstruct 在这里执行");
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (beanName.startsWith("my")) {
			System.out.println("初始化后: " + beanName + " - AOP 代理在这里创建");
		}
		return bean;
	}

	@Override
	public void postProcessBeforeDestruction(Object bean, String beanName)
			throws BeansException {
		if (beanName.startsWith("my")) {
			System.out.println("销毁前: " + beanName + " - @PreDestroy 在这里执行");
		}
	}
}
