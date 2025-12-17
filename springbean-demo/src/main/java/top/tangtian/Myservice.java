package top.tangtian;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author tangtian
 * @date 2025-12-16 12:04
 */
@Service()
@Slf4j
public class Myservice implements BeanNameAware, InitializingBean, DisposableBean {
	private String beanName;
	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void destroy() throws Exception {
		log.info("这个bean的名称：{},被摧毁",beanName);

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("这个bean的名称：{}",beanName);
	}
}
