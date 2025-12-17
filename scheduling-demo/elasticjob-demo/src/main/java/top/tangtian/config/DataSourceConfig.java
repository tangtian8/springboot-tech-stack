package top.tangtian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author tangtian
 * @date 2025-12-17 12:40
 */
//@Configuration
//public class DataSourceConfig {
//
//	@Bean
//	@Primary  // <--- 关键：标记这是主数据源，JPA 将默认使用它
//	@ConfigurationProperties("spring.datasource")
//	public DataSource dataSource() {
//		return DataSourceBuilder.create().build();
//	}
//}