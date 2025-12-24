package top.tangtian.beaninteface;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * @author tangtian
 * @date 2025-12-17 19:27
 * 生命周期回调接口
 * // 1. InitializingBean - 初始化回调
 * public interface InitializingBean {
 *     void afterPropertiesSet() throws Exception;
 * }
 *
 * // 2. DisposableBean - 销毁回调
 * public interface DisposableBean {
 *     void destroy() throws Exception;
 * }
 *
 * // 3. SmartInitializingSingleton - 所有单例 Bean 初始化后回调
 * public interface SmartInitializingSingleton {
 *     void afterSingletonsInstantiated();
 * }
 *
 * // 4. Lifecycle - 生命周期管理
 * public interface Lifecycle {
 *     void start();
 *     void stop();
 *     boolean isRunning();
 * }
 *
 * // 5. SmartLifecycle - 增强的生命周期管理
 * public interface SmartLifecycle extends Lifecycle, Phased {
 *     boolean isAutoStartup();
 *     void stop(Runnable callback);
 *     int getPhase();  // 控制启动顺序
 * }
 */
@Component
public class LifecycleBean implements
		InitializingBean,
		DisposableBean,
		SmartInitializingSingleton,
		SmartLifecycle {

	private volatile boolean running = false;



	// InitializingBean
	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("1. InitializingBean.afterPropertiesSet - Bean 初始化");
	}

	// SmartInitializingSingleton - 所有单例 Bean 初始化后调用
	@Override
	public void afterSingletonsInstantiated() {
		System.out.println("2. SmartInitializingSingleton.afterSingletonsInstantiated");
		System.out.println("   所有单例 Bean 已初始化完成");
	}

	// SmartLifecycle - 自动启动
	@Override
	public void start() {
		if (!running) {
			System.out.println("3. SmartLifecycle.start - 启动组件");
			running = true;
			// 启动后台任务、连接池等
		}
	}

	@Override
	public void stop() {
		System.out.println("4. SmartLifecycle.stop - 停止组件");
		running = false;
	}

	@Override
	public void stop(Runnable callback) {
		System.out.println("5. SmartLifecycle.stop(callback) - 异步停止");
		stop();
		callback.run();
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean isAutoStartup() {
		return true;  // 自动启动
	}

	@Override
	public int getPhase() {
		return 0;  // 启动顺序,数字越小越先启动
	}

	// DisposableBean
	@Override
	public void destroy() throws Exception {
		System.out.println("6. DisposableBean.destroy - Bean 销毁");
	}
}
