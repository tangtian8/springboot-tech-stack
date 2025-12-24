package top.tangtian.beaninteface;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.Arrays;

/**
 * @author tangtian
 * @date 2025-12-17 19:21
 * Aware 接口系列(感知容器资源)
 * 这些接口让 Bean 能够感知到 Spring 容器的各种资源:
 * // 1. BeanNameAware - 获取 Bean 名称
 * public interface BeanNameAware extends Aware {
 *     void setBeanName(String name);
 * }
 *
 * // 2. BeanFactoryAware - 获取 BeanFactory
 * public interface BeanFactoryAware extends Aware {
 *     void setBeanFactory(BeanFactory beanFactory) throws BeansException;
 * }
 *
 * // 3. ApplicationContextAware - 获取 ApplicationContext(最常用)
 * public interface ApplicationContextAware extends Aware {
 *     void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
 * }
 *
 * // 4. EnvironmentAware - 获取环境变量
 * public interface EnvironmentAware extends Aware {
 *     void setEnvironment(Environment environment);
 * }
 *
 * // 5. ResourceLoaderAware - 获取资源加载器
 * public interface ResourceLoaderAware extends Aware {
 *     void setResourceLoader(ResourceLoader resourceLoader);
 * }
 *
 * // 6. ApplicationEventPublisherAware - 获取事件发布器
 * public interface ApplicationEventPublisherAware extends Aware {
 *     void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher);
 * }
 *
 * // 7. MessageSourceAware - 获取国际化消息源
 * public interface MessageSourceAware extends Aware {
 *     void setMessageSource(MessageSource messageSource);
 * }
 *
 * // 8. BeanClassLoaderAware - 获取类加载器
 * public interface BeanClassLoaderAware extends Aware {
 *     void setBeanClassLoader(ClassLoader classLoader);
 * }
 *
 * // 9. ServletContextAware - 获取 ServletContext(Web 环境)
 * public interface ServletContextAware extends Aware {
 *     void setServletContext(ServletContext servletContext);
 * }
 *
 * // 10. ServletConfigAware - 获取 ServletConfig(Web 环境)
 * public interface ServletConfigAware extends Aware {
 *     void setServletConfig(ServletConfig servletConfig);
 * }
 *
 * // 11. ImportAware - 获取导入该配置类的注解元数据
 * public interface ImportAware extends Aware {
 *     void setImportMetadata(AnnotationMetadata importMetadata);
 * }
 *
 * // 12. EmbeddedValueResolverAware - 获取值解析器
 * public interface EmbeddedValueResolverAware extends Aware {
 *     void setEmbeddedValueResolver(StringValueResolver resolver);
 * }
 */
@Component
public class AllAwaresBean implements
		BeanNameAware,
		BeanFactoryAware,
		ApplicationContextAware,
		EnvironmentAware,
		ResourceLoaderAware,
		ApplicationEventPublisherAware,
		MessageSourceAware,
		EmbeddedValueResolverAware {

	private String beanName;
	private BeanFactory beanFactory;
	private ApplicationContext applicationContext;
	private Environment environment;
	private ResourceLoader resourceLoader;
	private ApplicationEventPublisher eventPublisher;
	private MessageSource messageSource;
	private StringValueResolver valueResolver;

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
		System.out.println("1. BeanNameAware: " + name);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
		System.out.println("2. BeanFactoryAware");
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.applicationContext = context;
		System.out.println("3. ApplicationContextAware");
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		System.out.println("4. EnvironmentAware");
		System.out.println("   Active Profiles: " + Arrays.toString(environment.getActiveProfiles()));
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
		System.out.println("5. ResourceLoaderAware");
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.eventPublisher = publisher;
		System.out.println("6. ApplicationEventPublisherAware");
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
		System.out.println("7. MessageSourceAware");
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.valueResolver = resolver;
		System.out.println("8. EmbeddedValueResolverAware");
		// 可以解析 ${} 和 #{} 表达式
		String resolved = resolver.resolveStringValue("${server.port:8080}");
		System.out.println("   Resolved port: " + resolved);
	}

	@PostConstruct
	public void init() {
		System.out.println("\n所有 Aware 接口回调完成,可以使用容器资源:");
		System.out.println("- Bean 名称: " + beanName);
		System.out.println("- 容器中 Bean 数量: " + applicationContext.getBeanDefinitionCount());

		// 加载资源
		Resource resource = resourceLoader.getResource("classpath:application.yml");
		System.out.println("- 资源存在: " + resource.exists());

		// 发布事件
		eventPublisher.publishEvent(new CustomEvent(this, "初始化完成"));
	}
}

// 自定义事件
class CustomEvent extends ApplicationEvent {
	private String message;

	public CustomEvent(Object source, String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
