package top.tangtian.elacticjob.job.dataflowjob;

/**
 * @author tangtian
 * @date 2025-12-17 09:40
 */

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import top.tangtian.elacticjob.entity.Order;
import top.tangtian.elacticjob.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单状态同步任务 - 持续监控并同步订单状态到第三方系统
 * 特点: 流式处理，持续获取数据直到没有新数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusSyncDataflowJob implements DataflowJob<Order> {

	private final OrderRepository orderRepository;
	private static final int BATCH_SIZE = 50;

	/**
	 * 获取需要同步的订单
	 * 返回null或空列表时，任务停止
	 */
	@Override
	public List<Order> fetchData(ShardingContext context) {
		log.info("========== [订单同步] 获取待同步订单 ==========");
		log.info("分片信息: {}/{}", context.getShardingItem(), context.getShardingTotalCount());

		try {
			// 根据分片项获取不同状态的订单
			Order.OrderStatus status = getStatusBySharding(context.getShardingItem());

			// 获取最近1小时内创建的指定状态订单
			LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
			Pageable pageable = PageRequest.of(0, BATCH_SIZE);

			List<Order> orders = orderRepository
					.findByStatusAndTimeRange(status, oneHourAgo, LocalDateTime.now())
					.stream()
					.limit(BATCH_SIZE)
					.toList();

			if (orders.isEmpty()) {
				log.info("分片 {} 暂无 {} 状态的订单需要同步", context.getShardingItem(), status);
				return null; // 返回null，任务停止
			}

			log.info("分片 {} 获取到 {} 条 {} 状态的订单",
					context.getShardingItem(), orders.size(), status);

			return orders;

		} catch (Exception e) {
			log.error("获取订单数据失败", e);
			return null;
		}
	}

	/**
	 * 处理订单同步
	 */
	@Override
	public void processData(ShardingContext context, List<Order> orders) {
		log.info("========== [订单同步] 开始处理 {} 条订单 ==========", orders.size());

		int successCount = 0;
		int failCount = 0;

		for (Order order : orders) {
			try {
				// 模拟同步到第三方系统
				syncOrderToThirdParty(order);

				// 更新订单备注，标记已同步
				order.setRemark("已同步到第三方系统 - " + LocalDateTime.now());
				orderRepository.save(order);

				successCount++;
				log.debug("订单 {} 同步成功", order.getOrderNo());

				// 模拟网络延迟
				Thread.sleep(50);

			} catch (Exception e) {
				log.error("订单 {} 同步失败", order.getOrderNo(), e);
				failCount++;
			}
		}

		log.info("分片 {} 处理完成: 成功={}, 失败={}",
				context.getShardingItem(), successCount, failCount);
	}

	private Order.OrderStatus getStatusBySharding(int shardingItem) {
		return switch (shardingItem) {
			case 0 -> Order.OrderStatus.PENDING;
			case 1 -> Order.OrderStatus.PAID;
			case 2 -> Order.OrderStatus.SHIPPED;
			default -> Order.OrderStatus.DELIVERED;
		};
	}

	private void syncOrderToThirdParty(Order order) {
		// 模拟调用第三方API
		log.debug("同步订单到第三方: orderNo={}, amount={}",
				order.getOrderNo(), order.getAmount());
	}
}