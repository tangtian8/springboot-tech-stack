package top.tangtian.rocketmqdemo.rocketmqconsumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import top.tangtian.rocketmqdemo.rocketmqconsumer.entity.OrderMessage;

/**
 * @author tangtian
 * @date 2026-01-02 17:16
 */
@Slf4j
@Component
@RocketMQMessageListener(
		topic = "order-status-topic",
		consumerGroup = "order-status-consumer-group",
		consumeMode = ConsumeMode.ORDERLY  // 关键：顺序消费模式
)
public class OrderStatusListener implements RocketMQListener<OrderMessage> {

	@Override
	public void onMessage(OrderMessage message) {
		log.info("顺序消费订单状态消息: orderId={}, timestamp={}",
				message.getOrderId(), message.getTimestamp());

		// 模拟处理耗时
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}