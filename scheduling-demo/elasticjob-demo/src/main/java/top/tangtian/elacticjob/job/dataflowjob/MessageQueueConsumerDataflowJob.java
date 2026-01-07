package top.tangtian.elacticjob.job.dataflowjob;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 消息队列消费任务 - 模拟从MQ消费消息并处理
 * 特点: 持续消费，分片处理不同主题
 */

/**
 * @author tangtian
 * @date 2025-12-17 09:42
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageQueueConsumerDataflowJob implements DataflowJob<MessageQueueConsumerDataflowJob.Message> {

	// 模拟消息队列
	private static final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();

	@Data
	public static class Message {
		private String id;
		private String topic;
		private String content;
		private LocalDateTime timestamp;
		private int retryCount;
	}

	/**
	 * 从消息队列获取消息
	 */
	@Override
	public List<Message> fetchData(ShardingContext context) {
		log.info("========== [消息消费] 获取消息 ==========");
		log.info("分片: {}/{}, 主题: {}",
				context.getShardingItem(),
				context.getShardingTotalCount(),
				context.getShardingParameter());

		try {
			String topic = context.getShardingParameter();
			List<Message> messages = new ArrayList<>();

			// 从队列中拉取最多50条消息
			for (int i = 0; i < 50; i++) {
				Message message = messageQueue.poll();
				if (message == null) {
					break;
				}

				// 只处理属于当前分片主题的消息
				if (topic.equals(message.getTopic())) {
					messages.add(message);
				} else {
					// 放回队列给其他分片处理
					messageQueue.offer(message);
				}
			}

			if (messages.isEmpty()) {
				log.info("分片 {} 暂无 {} 主题的消息", context.getShardingItem(), topic);
				return null;
			}

			log.info("分片 {} 获取到 {} 条 {} 主题的消息",
					context.getShardingItem(), messages.size(), topic);

			return messages;

		} catch (Exception e) {
			log.error("获取消息失败", e);
			return null;
		}
	}

	/**
	 * 处理消息
	 */
	@Override
	public void processData(ShardingContext context, List<Message> messages) {
		log.info("========== [消息消费] 开始处理 {} 条消息 ==========", messages.size());

		int successCount = 0;
		int failCount = 0;

		for (Message message : messages) {
			try {
				// 处理消息
				processMessage(message);
				successCount++;

				log.debug("消息 {} 处理成功", message.getId());

				// 模拟处理耗时
				Thread.sleep(20);

			} catch (Exception e) {
				log.error("消息 {} 处理失败", message.getId(), e);
				failCount++;

				// 失败重试
				message.setRetryCount(message.getRetryCount() + 1);
				if (message.getRetryCount() < 3) {
					// 放回队列重试
					messageQueue.offer(message);
					log.warn("消息 {} 放回队列，重试次数: {}",
							message.getId(), message.getRetryCount());
				} else {
					log.error("消息 {} 重试次数超限，丢弃", message.getId());
				}
			}
		}

		log.info("分片 {} 处理完成: 成功={}, 失败={}",
				context.getShardingItem(), successCount, failCount);
	}

	private void processMessage(Message message) {
		log.debug("处理消息: id={}, topic={}, content={}",
				message.getId(), message.getTopic(), message.getContent());

		// 模拟业务处理
		switch (message.getTopic()) {
			case "ORDER_CREATED":
				handleOrderCreated(message);
				break;
			case "PAYMENT_SUCCESS":
				handlePaymentSuccess(message);
				break;
			case "ORDER_SHIPPED":
				handleOrderShipped(message);
				break;
			default:
				log.warn("未知消息主题: {}", message.getTopic());
		}
	}

	private void handleOrderCreated(Message message) {
		log.debug("处理订单创建消息: {}", message.getContent());
	}

	private void handlePaymentSuccess(Message message) {
		log.debug("处理支付成功消息: {}", message.getContent());
	}

	private void handleOrderShipped(Message message) {
		log.debug("处理订单发货消息: {}", message.getContent());
	}

	/**
	 * 模拟生产消息（用于测试）
	 */
	public static void produceMessage(String id, String topic, String content) {
		Message message = new Message();
		message.setId(id);
		message.setTopic(topic);
		message.setContent(content);
		message.setTimestamp(LocalDateTime.now());
		message.setRetryCount(0);
		messageQueue.offer(message);
	}
}
