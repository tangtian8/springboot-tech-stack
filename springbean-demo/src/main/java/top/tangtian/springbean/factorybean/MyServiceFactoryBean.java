package top.tangtian.springbean.factorybean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author tangtian
 * @date 2025-12-17 19:31
 */
@Component("myHighService")
public class MyServiceFactoryBean implements FactoryBean<MyService> {

	@Autowired
	private Environment environment;

	@Override
	public MyService getObject() throws Exception {
		System.out.println("FactoryBean.getObject - 创建复杂对象");

		// 根据配置创建不同实现
		String type = environment.getProperty("service.type", "default");

		if ("advanced".equals(type)) {
			return new AdvancedMyService();
		} else {
			return new DefaultMyService();
		}
	}

	@Override
	public Class<?> getObjectType() {
		return MyService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}






