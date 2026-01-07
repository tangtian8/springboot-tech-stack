package top.tangtian.elacticjob.config;

import jakarta.annotation.Resource;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author tangtian
 * @date 2025-12-17 13:00
 */
@Configuration
public class ElasticJobConfig {

	// 注入 Spring 管理的主 DataSource
	@Resource
	private DataSource dataSource;

	@Bean
	public TracingConfiguration<DataSource> tracingConfiguration() {
		// 将主数据源传给 ElasticJob
		return new TracingConfiguration<>("RDB", dataSource);
	}
}
