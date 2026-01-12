package top.tangtian.cas.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tangtian
 * @date 2026-01-12 18:33
 */
@Controller
public class HomeController {

	/**
	 * 首页（无需认证）
	 */
	@GetMapping("/")
	public String home() {
		return "index";
	}

	/**
	 * 受保护页面（需要认证）
	 */
	@GetMapping("/protected")
	public String protectedPage(Model model, Principal principal) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("username", principal.getName());
		model.addAttribute("authorities", auth.getAuthorities());
		return "protected";
	}

	/**
	 * 获取当前用户信息（JSON）
	 */
	@GetMapping("/api/user")
	@ResponseBody
	public Map<String, Object> getCurrentUser(Principal principal) {
		Map<String, Object> result = new HashMap<>();
		if (principal != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			result.put("username", principal.getName());
			result.put("authorities", auth.getAuthorities());
			result.put("authenticated", auth.isAuthenticated());
		} else {
			result.put("authenticated", false);
		}
		return result;
	}

	/**
	 * 测试页面
	 */
	@GetMapping("/test")
	@ResponseBody
	public String test(Principal principal) {
		if (principal != null) {
			return "Hello, " + principal.getName() + "! You are authenticated via CAS.";
		}
		return "You are not authenticated.";
	}
}
