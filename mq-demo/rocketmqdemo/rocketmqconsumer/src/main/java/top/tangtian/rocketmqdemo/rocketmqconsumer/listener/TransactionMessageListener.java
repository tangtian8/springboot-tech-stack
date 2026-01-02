package top.tangtian.rocketmqdemo.rocketmqconsumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import top.tangtian.rocketmqdemo.rocketmqconsumer.entity.OrderMessage;

/**
 * @author tangtian
 * @date 2026-01-02 17:23
 */
@Slf4j
@Component
@RocketMQMessageListener(
		topic = "transaction-topic",
		consumerGroup = "transaction-consumer-group"
)
public class TransactionMessageListener implements RocketMQListener<OrderMessage> {

	@Override
	public void onMessage(OrderMessage message) {
		log.info("接收到事务消息: orderId={}", message.getOrderId());

		// 执行下游业务逻辑
		processTransactionMessage(message);
	}

	private void processTransactionMessage(OrderMessage message) {
		// 处理业务
		log.info("处理事务消息: {}", message.getOrderId());
	}
}