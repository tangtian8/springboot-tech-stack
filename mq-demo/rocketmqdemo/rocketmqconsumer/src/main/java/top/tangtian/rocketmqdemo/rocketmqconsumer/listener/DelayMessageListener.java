package top.tangtian.rocketmqdemo.rocketmqconsumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import top.tangtian.rocketmqdemo.rocketmqconsumer.entity.OrderMessage;

/**
 * @author tangtian
 * @date 2026-01-02 17:17
 */
@Slf4j
@Component
@RocketMQMessageListener(
		topic = "delay-topic",
		consumerGroup = "delay-consumer-group"
)
public class DelayMessageListener implements RocketMQListener<OrderMessage> {

	@Override
	public void onMessage(OrderMessage message) {
		log.info("收到延迟消息: orderId={}, 发送时间={}, 接收时间={}",
				message.getOrderId(),
				message.getTimestamp(),
				System.currentTimeMillis());

		// 检查订单状态，如果未支付则取消订单
		checkAndCancelOrder(message.getOrderId());
	}

	private void checkAndCancelOrder(String orderId) {
		log.info("检查订单状态并处理超时订单: {}", orderId);
		// 实际业务逻辑
	}
}
