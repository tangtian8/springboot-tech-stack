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
 * @date 2026-01-02 17:16
 */
@Slf4j
@Service
public class OrderlyMessageProducerService {

	@Resource
	private RocketMQTemplate rocketMQTemplate;

	/**
	 * 发送顺序消息
	 * 使用orderId作为hashKey，确保同一订单的消息发送到同一队列
	 */
	public void sendOrderlyMessage(OrderMessage orderMessage, String orderStatus) {
		String topic = "order-status-topic";
		String hashKey = orderMessage.getOrderId(); // 使用订单ID作为hash key

		Message message = MessageBuilder
				.withPayload(orderMessage)
				.setHeader("orderStatus", orderStatus)
				.build();

		// syncSendOrderly 保证消息顺序
		SendResult sendResult = rocketMQTemplate.syncSendOrderly(
				topic,
				message,
				hashKey
		);

		log.info("顺序消息发送成功: orderId={}, status={}, msgId={}",
				orderMessage.getOrderId(), orderStatus, sendResult.getMsgId());
	}

	/**
	 * 模拟订单状态变更流程
	 */
	public void processOrderStatusChange(String orderId, String userId) {
		OrderMessage message = new OrderMessage(orderId, userId, 100.0, System.currentTimeMillis());

		// 按顺序发送订单状态变化消息
		sendOrderlyMessage(message, "CREATED");
		sendOrderlyMessage(message, "PAID");
		sendOrderlyMessage(message, "SHIPPED");
		sendOrderlyMessage(message, "COMPLETED");
	}
}
