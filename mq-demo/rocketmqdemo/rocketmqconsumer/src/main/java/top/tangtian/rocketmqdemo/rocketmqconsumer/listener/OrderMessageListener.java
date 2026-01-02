package top.tangtian.rocketmqdemo.rocketmqconsumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import top.tangtian.rocketmqdemo.rocketmqconsumer.entity.OrderMessage;

/**
 * @author tangtian
 * @date 2026-01-02 17:10
 */
@Slf4j
@Component
@RocketMQMessageListener(
		topic = "order-topic",
		consumerGroup = "order-consumer-group"
)
public class OrderMessageListener implements RocketMQListener<OrderMessage> {

	@Override
	public void onMessage(OrderMessage message) {
		log.info("接收到订单消息: orderId={}, userId={}, amount={}",
				message.getOrderId(), message.getUserId(), message.getAmount());

		// 处理业务逻辑
		processOrder(message);
	}

	private void processOrder(OrderMessage message) {
		// 模拟业务处理
		log.info("处理订单: {}", message.getOrderId());
	}

}