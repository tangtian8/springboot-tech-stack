package top.tangtian.rocketmqdemo.producer.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import top.tangtian.rocketmqdemo.producer.entity.OrderMessage;

/**
 * @author tangtian
 * @date 2026-01-02 17:05
 */
@Slf4j
@Service
public class MessageProducerService {

	@Resource
	private RocketMQTemplate rocketMQTemplate;

	// 1. 同步发送消息
	public void sendSyncMessage(OrderMessage message) {
		SendResult sendResult = rocketMQTemplate.syncSend("order-topic", message);
		log.info("同步发送消息结果: {}", sendResult.getSendStatus());
	}

	// 2. 异步发送消息
	public void sendAsyncMessage(OrderMessage message) {
		rocketMQTemplate.asyncSend("order-topic", message, new SendCallback() {
			@Override
			public void onSuccess(SendResult sendResult) {
				log.info("异步发送成功: {}", sendResult.getMsgId());
			}

			@Override
			public void onException(Throwable throwable) {
				log.error("异步发送失败", throwable);
			}
		});
	}

	// 3. 单向发送（不关心结果）
	public void sendOneWayMessage(OrderMessage message) {
		rocketMQTemplate.sendOneWay("order-topic", message);
		log.info("单向发送消息完成");
	}

	// 4. 发送带Tag的消息
	public void sendMessageWithTag(OrderMessage message, String tag) {
		SendResult sendResult = rocketMQTemplate.syncSend(
				"order-topic:" + tag,
				MessageBuilder.withPayload(message).build()
		);
		log.info("发送带Tag消息: tag={}, result={}", tag, sendResult.getSendStatus());
	}
}