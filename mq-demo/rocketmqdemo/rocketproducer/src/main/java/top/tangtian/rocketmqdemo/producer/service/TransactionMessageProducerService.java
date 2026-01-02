package top.tangtian.rocketmqdemo.producer.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import top.tangtian.rocketmqdemo.producer.entity.OrderMessage;

/**
 * @author tangtian
 * @date 2026-01-02 17:18
 */
@Slf4j
@Service
public class TransactionMessageProducerService {

	@Resource
	private RocketMQTemplate rocketMQTemplate;

	/**
	 * 发送事务消息
	 */
	public void sendTransactionMessage(OrderMessage message) {
		Message msg = MessageBuilder
				.withPayload(message)
				.build();

		// 发送事务消息，arg是传递给事务监听器的参数
		TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction(
				"transaction-topic",
				msg,
				message.getOrderId()  // 传递给监听器的参数
		);
		log.info("事务消息已发送: orderId={}", message.getOrderId());
	}
}
