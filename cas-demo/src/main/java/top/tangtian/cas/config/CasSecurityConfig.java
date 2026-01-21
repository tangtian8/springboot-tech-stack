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
	 * 作用:
	 *
	 * 当用户未认证访问受保护资源时，触发此入口点
	 * 构造 CAS 登录 URL 并重定向用户
	 * // 用户访问受保护资源 /protected
	 * → Spring Security 检测到未认证
	 * → CasAuthenticationEntryPoint 被触发
	 * → 构造 URL: http://localhost:8080/cas/login?service=http://localhost:8081/login/cas
	 * → 重定向用户到 CAS 登录页面
	 * ```
	 *
	 * **生成的完整 URL 示例:**
	 * ```
	 * http://localhost:8080/cas/login?service=http%3A%2F%2Flocalhost%3A8081%2Flogin%2Fcas
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
	 * **作用:**
	 * - 验证从 CAS 服务器返回的 **Service Ticket (ST)**
	 * - `Cas30ServiceTicketValidator` - 支持 CAS 3.0 协议
	 *
	 * **验证流程:**
	 * ```
	 * 1. 客户端收到: http://localhost:8081/login/cas?ticket=ST-1-xxx
	 * 2. TicketValidator 向 CAS 服务器发送验证请求:
	 *    GET http://localhost:8080/cas/serviceValidate?ticket=ST-1-xxx&service=http://localhost:8081/login/cas
	 * 3. CAS 服务器返回 XML 响应:
	 *    <cas:serviceResponse>
	 *      <cas:authenticationSuccess>
	 *        <cas:user>casuser</cas:user>
	 *        <cas:attributes>
	 *          <cas:email>user@example.com</cas:email>
	 *        </cas:attributes>
	 *      </cas:authenticationSuccess>
	 *    </cas:serviceResponse>
	 * 4. TicketValidator 解析响应，提取用户信息
	 */
	@Bean
	public TicketValidator ticketValidator() {
		return new Cas30ServiceTicketValidator(casServerUrlPrefix);
	}

	/**
	 * 用户详情服务
	 * **作用:**
	 * - CAS 验证成功后，根据**用户名**加载用户详细信息
	 * - 可以从数据库加载用户的权限、角色等信息
	 *
	 * **为什么需要这个?**
	 * ```
	 * CAS 只负责认证（Authentication）- 确认用户是谁
	 * UserDetailsService 负责授权（Authorization）- 确认用户能做什么
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
	 * 作用:
	 *
	 * 核心认证组件，整合所有 CAS 认证逻辑
	 * 调用 TicketValidator 验证票据
	 * 调用 UserDetailsService 加载用户信息
	 * 创建 Spring Security 的 Authentication 对象
	 * CasAuthenticationProvider.authenticate(token) {
	 *     1. 提取 Service Ticket
	 *     2. 调用 ticketValidator.validate(ticket, service)
	 *     3. 获取 CAS 返回的用户名
	 *     4. 调用 userDetailsService.loadUserByUsername(username)
	 *     5. 创建 CasAuthenticationToken
	 *     6. 返回认证成功的 Authentication 对象
	 * }
	 * setKey() 的作用:
	 *
	 * 用于区分不同的 AuthenticationProvider
	 * 防止恶意构造的 Authentication 对象被接受
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
	 * Spring Security 的认证管理中心
	 * 管理所有 AuthenticationProvider（这里只有 CasAuthenticationProvider）
	 * 接收认证请求，委托给对应的 Provider 处理
	 */
	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(casAuthenticationProvider());
	}

	/**
	 * CAS 认证过滤器
	 * **作用:**
	 * - 拦截 `/login/cas` 路径（CAS 的回调地址）
	 * cas:
	 *   client-host-url: http://localhost:8081
	 *   # 回调地址: ${client-host-url}/login/cas
	 * - 从 URL 中提取 Service Ticket
	 * - 调用 AuthenticationManager 进行认证
	 *
	 * **处理流程:**
	 * ```
	 * 1. 用户从 CAS 重定向回来: GET /login/cas?ticket=ST-1-xxx
	 * 2. CasAuthenticationFilter 拦截请求
	 * 3. 提取 ticket 参数
	 * 4. 创建 UsernamePasswordAuthenticationToken(ticket)
	 * 5. 调用 authenticationManager.authenticate(token)
	 * 6. 认证成功 → 设置 SecurityContext → 重定向到原始请求的资源
	 * 7. 认证失败 → 返回错误
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
	 * **作用:**
	 * - 处理 CAS 服务器发起的**单点登出 (Single Logout)**
	 * - 当用户在 CAS 服务器登出时，CAS 会通知所有已登录的客户端应用
	 *
	 * **单点登出流程:**
	 * ```
	 * 1. 用户在应用 A 访问 CAS 登出: http://cas-server/logout
	 * 2. CAS 服务器查找所有登录过的应用（A, B, C）
	 * 3. CAS 向每个应用发送 POST 请求:
	 *    POST http://app-a/login/cas
	 *    POST http://app-b/login/cas
	 *    POST http://app-c/login/cas
	 *    Body: logoutRequest=<samlp:LogoutRequest>...</samlp:LogoutRequest>
	 * 4. SingleSignOutFilter 拦截这个 POST 请求
	 * 5. 解析 logoutRequest，销毁对应的 Session
	 * 6. 应用 A, B, C 的用户都被登出
	 */
	@Bean
	public SingleSignOutFilter singleSignOutFilter() {
		SingleSignOutFilter filter = new SingleSignOutFilter();
		filter.setIgnoreInitConfiguration(true);
		return filter;
	}

	/**
	 * 请求单点登出过滤器
	 * **作用:**
	 * - 处理**客户端应用**发起的登出请求
	 * - 清理本地 Session
	 * - 重定向到 CAS 服务器的登出页面
	 *
	 * **登出流程:**
	 * ```
	 * 1. 用户访问: http://localhost:8081/logout/cas
	 * 2. LogoutFilter 拦截请求
	 * 3. SecurityContextLogoutHandler 清理:
	 *    - 清除 SecurityContext
	 *    - 销毁 Session
	 *    - 清除 Cookie
	 * 4. 重定向到: http://localhost:8080/cas/logout?service=http://localhost:8081
	 * 5. CAS 服务器处理登出
	 * 6. CAS 登出后重定向回: http://localhost:8081
	 * ```
	 *
	 * **构造的登出 URL:**
	 * ```
	 * http://localhost:8080/cas/logout?service=http://localhost:8081
	 *                                           ↑ 登出后回到客户端首页
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