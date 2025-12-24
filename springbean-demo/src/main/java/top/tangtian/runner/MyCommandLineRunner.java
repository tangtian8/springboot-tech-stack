package top.tangtian.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.tangtian.aop.UserService;

import java.util.Arrays;

/**
 * @author tangtian
 * @date 2025-12-17 19:34
 */
@Component
@Order(2)
public class MyCommandLineRunner implements CommandLineRunner {
	@Autowired
	UserService userService;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("CommandLineRunner 执行");
		System.out.println("原始参数: " + Arrays.toString(args));
		userService.saveUser();
	}
}