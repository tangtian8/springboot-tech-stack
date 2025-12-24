package top.tangtian.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author tangtian
 * @date 2025-12-17 19:34
 */
// ApplicationRunner vs CommandLineRunner
@Component
@Order(1)
public class MyApplicationRunner implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("ApplicationRunner 执行");
		System.out.println("选项参数: " + args.getOptionNames());
		System.out.println("非选项参数: " + args.getNonOptionArgs());

		// 解析 --server.port=8080 这样的参数
		if (args.containsOption("debug")) {
			System.out.println("调试模式已启用");
		}
	}
}