package top.tangtian.rocketmqdemo.rocketmqconsumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import top.tangtian.rocketmqdemo.rocketmqconsumer.entity.OrderMessage;

/**
 * @author tangtian
 * @date 2026-01-02 17:14
 */
@Slf4j
@Component
@RocketMQMessageListener(
		topic = "order-topic",
		consumerGroup = "vip-order-consumer-group",
		selectorType = SelectorType.TAG,
		selectorExpression = "VIP || URGENT"  // 只消费VIP或URGENT标签的消息
)
public class VipOrderMessageListener implements RocketMQListener<OrderMessage> {

	@Override
	public void onMessage(OrderMessage message) {
		log.info("VIP订单消费者接收到消息: {}", message.getOrderId());
		// VIP订单特殊处理
	}
}
