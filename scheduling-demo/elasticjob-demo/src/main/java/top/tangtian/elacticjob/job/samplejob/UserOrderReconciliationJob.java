package top.tangtian.elacticjob.job.samplejob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;
import top.tangtian.elacticjob.entity.Order;
import top.tangtian.elacticjob.repository.OrderRepository;
import top.tangtian.elacticjob.service.ReconciliationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author tangtian
 * @date 2025-12-17 09:38
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserOrderReconciliationJob implements SimpleJob {

	private final ReconciliationService reconciliationService;
	private final OrderRepository orderRepository;

	@Override
	public void execute(ShardingContext context) {
		log.info("========== 用户订单对账任务开始 ==========");
		log.info("分片: {}/{}, 用户ID范围: {}",
				context.getShardingItem() + 1,
				context.getShardingTotalCount(),
				context.getShardingParameter());

		try {
			// 解析用户ID范围 "0-999" -> [0, 999]
			String[] range = context.getShardingParameter().split("-");
			long minUserId = Long.parseLong(range[0]);
			long maxUserId = Long.parseLong(range[1]);

			LocalDate yesterday = LocalDate.now().minusDays(1);
			LocalDateTime startTime = yesterday.atStartOfDay();
			LocalDateTime endTime = yesterday.atTime(LocalTime.MAX);

			log.info("处理用户ID范围: {} - {}", minUserId, maxUserId);

			// 查询该用户ID范围的订单
			List<Order> orders = orderRepository.findByTimeRange(startTime, endTime)
					.stream()
					.filter(o -> o.getUserId() >= minUserId && o.getUserId() <= maxUserId)
					.toList();

			log.info("分片 {} 查询到 {} 条订单", context.getShardingItem(), orders.size());

			// 按用户分组对账
			reconciliationService.performUserOrderReconciliation(yesterday, minUserId, maxUserId, orders);

			log.info("分片 {} 完成,处理了 {} 条订单", context.getShardingItem(), orders.size());

		} catch (Exception e) {
			log.error("用户订单对账失败 - 分片: {}", context.getShardingItem(), e);
			throw new RuntimeException("用户订单对账任务执行失败", e);
		}

		log.info("========== 用户订单对账任务完成 ==========");
	}
}
