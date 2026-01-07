package top.tangtian.springbean.aop;

import org.springframework.stereotype.Service;

/**
 * @author tangtian
 * @date 2025-12-18 10:05
 */
@Service
@MyLog // 标注注解，触发 AOP 逻辑
public class UserServiceImpl implements UserService {
	@Override
	public void saveUser() {
		System.out.println("正在保存用户...");
		try { Thread.sleep(500); } catch (InterruptedException e) {}
	}
}
