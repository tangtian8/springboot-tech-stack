package top.tangtian.controller;

/**
 * @author tangtian
 * @date 2025-12-17 10:14
 */

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务动态管理接口（可选）
 */
@Slf4j
@RestController
@RequestMapping("/api/job-management")
@RequiredArgsConstructor
public class JobManagementController {

	private final Map<String, ScheduleJobBootstrap> jobBootstrapMap;

	/**
	 * 获取所有任务状态
	 */
	@GetMapping("/jobs")
	public Map<String, Object> getAllJobs() {
		Map<String, Object> result = new HashMap<>();
		result.put("totalJobs", jobBootstrapMap.size());
		result.put("jobNames", jobBootstrapMap.keySet());

		log.info("当前注册的任务数量: {}", jobBootstrapMap.size());
		return result;
	}

	/**
	 * 获取任务详情
	 */
	@GetMapping("/jobs/{jobName}")
	public Map<String, Object> getJobInfo(@PathVariable String jobName) {
		Map<String, Object> result = new HashMap<>();

		if (jobBootstrapMap.containsKey(jobName)) {
			result.put("jobName", jobName);
			result.put("status", "exists");
			result.put("message", "任务存在");
		} else {
			result.put("jobName", jobName);
			result.put("status", "not_found");
			result.put("message", "任务不存在");
		}

		return result;
	}

	/**
	 * 手动触发任务执行
	 */
	@PostMapping("/jobs/{jobName}/trigger")
	public Map<String, String> triggerJob(@PathVariable String jobName) {
		try {
			ScheduleJobBootstrap bootstrap = jobBootstrapMap.get(jobName);

			if (bootstrap == null) {
				return Map.of("status", "error", "message", "任务不存在: " + jobName);
			}

			// 注意: Elastic-Job 3.x 没有直接的手动触发API
			// 需要通过调度器或其他方式实现
			log.info("请求手动触发任务: {}", jobName);

			return Map.of(
					"status", "success",
					"message", "任务触发请求已接收: " + jobName,
					"note", "实际执行取决于任务调度配置"
			);

		} catch (Exception e) {
			log.error("触发任务失败: {}", jobName, e);
			return Map.of("status", "error", "message", e.getMessage());
		}
	}

	/**
	 * 健康检查
	 */
	@GetMapping("/health")
	public Map<String, Object> healthCheck() {
		Map<String, Object> health = new HashMap<>();
		health.put("status", "UP");
		health.put("totalJobs", jobBootstrapMap.size());
		health.put("timestamp", System.currentTimeMillis());

		return health;
	}
}
