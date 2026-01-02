package top.tangtian.rocketmqdemo.producer.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import top.tangtian.rocketmqdemo.producer.entity.OrderMessage;

/**
 * @author tangtian
 * @date 2026-01-02 17:17
 */
@Slf4j
@Service
public class DelayMessageProducerService {

	@Resource
	private RocketMQTemplate rocketMQTemplate;

	/**
	 * 发送延迟消息
	 * RocketMQ支持18个延迟级别：
	 * 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
	 */
	public void sendDelayMessage(OrderMessage message, int delayLevel) {
		Message msg = MessageBuilder
				.withPayload(message)
				.build();

		// delayLevel: 1=1s, 2=5s, 3=10s, 4=30s, 5=1m, 6=2m...
		SendResult sendResult = rocketMQTemplate.syncSend(
				"delay-topic",
				msg,
				3000,  // timeout
				delayLevel  // 延迟级别
		);

		log.info("延迟消息发送成功: delayLevel={}, msgId={}",
				delayLevel, sendResult.getMsgId());
	}

	/**
	 * 发送订单超时取消提醒（30分钟后）
	 */
	public void sendOrderTimeoutReminder(OrderMessage message) {
		// 延迟级别16 = 30分钟
		sendDelayMessage(message, 16);
		log.info("订单超时提醒消息已发送: orderId={}", message.getOrderId());
	}
}