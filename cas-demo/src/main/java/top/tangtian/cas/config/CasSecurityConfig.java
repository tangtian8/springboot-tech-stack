package top.tangtian.cas.config;

import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Cas30ServiceTicketValidator;
import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

/**
 * @author tangtian
 * @date 2026-01-12 18:36
 */
@Configuration
@EnableWebSecurity
public class CasSecurityConfig {

	@Value("${cas.server-url-prefix}")
	private String casServerUrlPrefix;

	@Value("${cas.server-login-url}")
	private String casServerLoginUrl;

	@Value("${cas.client-host-url}")
	private String clientHostUrl;

	/**
	 * CAS 服务属性配置
	 */
	@Bean
	public ServiceProperties serviceProperties() {
		ServiceProperties serviceProperties = new ServiceProperties();
		serviceProperties.setService(clientHostUrl + "/login/cas");
		serviceProperties.setSendRenew(false);
		return serviceProperties;
	}

	/**
	 * CAS 认证入口点
	 */
	@Bean
	public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
		CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
		entryPoint.setLoginUrl(casServerLoginUrl);
		entryPoint.setServiceProperties(serviceProperties());
		return entryPoint;
	}

	/**
	 * Ticket 验证器
	 */
	@Bean
	public TicketValidator ticketValidator() {
		return new Cas30ServiceTicketValidator(casServerUrlPrefix);
	}

	/**
	 * 用户详情服务
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		return username -> User.builder()
				.username(username)
				.password("")
				.authorities("ROLE_USER")
				.build();
	}

	/**
	 * CAS 认证提供者
	 */
	@Bean
	public CasAuthenticationProvider casAuthenticationProvider() {
		CasAuthenticationProvider provider = new CasAuthenticationProvider();
		provider.setServiceProperties(serviceProperties());
		provider.setTicketValidator(ticketValidator());
		provider.setUserDetailsService(userDetailsService());
		provider.setKey("cas-client-key");
		return provider;
	}

	/**
	 * 认证管理器
	 */
	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(casAuthenticationProvider());
	}

	/**
	 * CAS 认证过滤器
	 */
	@Bean
	public CasAuthenticationFilter casAuthenticationFilter() {
		CasAuthenticationFilter filter = new CasAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager());
		filter.setFilterProcessesUrl("/login/cas");
		return filter;
	}

	/**
	 * 单点登出过滤器
	 */
	@Bean
	public SingleSignOutFilter singleSignOutFilter() {
		SingleSignOutFilter filter = new SingleSignOutFilter();
		filter.setIgnoreInitConfiguration(true);
		return filter;
	}

	/**
	 * 请求单点登出过滤器
	 */
	@Bean
	public LogoutFilter requestCasLogoutFilter() {
		LogoutFilter filter = new LogoutFilter(
				casServerUrlPrefix + "/logout?service=" + clientHostUrl,
				new SecurityContextLogoutHandler()
		);
		filter.setFilterProcessesUrl("/logout/cas");
		return filter;
	}

	/**
	 * Spring Security 6.x 安全过滤器链配置
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 授权配置 - 使用新的 Lambda DSL 风格
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/", "/public/**", "/error").permitAll()
						.anyRequest().authenticated()
				)

				// 异常处理 - CAS 认证入口
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(casAuthenticationEntryPoint())
				)

				// 添加过滤器
				.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
				.addFilterBefore(requestCasLogoutFilter(), LogoutFilter.class)
				.addFilter(casAuthenticationFilter())

				// 登出配置
				.logout(logout -> logout
						.logoutSuccessUrl("/")
						.permitAll()
				)

				// CSRF 配置
				.csrf(csrf -> csrf.disable());

		return http.build();
	}
}