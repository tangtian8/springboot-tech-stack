package top.tangtian.rocketmqdemo.producer.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import top.tangtian.rocketmqdemo.producer.entity.OrderMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-02 17:23
 */
@Slf4j
@Service
public class BatchMessageProducerService {

	@Resource
	private RocketMQTemplate rocketMQTemplate;

	/**
	 * 批量发送消息
	 */
	public void sendBatchMessages(List<OrderMessage> messages) {
		List<Message> msgList = new ArrayList<>();

		for (OrderMessage orderMessage : messages) {
			Message msg = MessageBuilder
					.withPayload(orderMessage)
					.build();
			msgList.add(msg);
		}

		// 批量同步发送
		SendResult sendResult = rocketMQTemplate.syncSend(
				"batch-topic",
				msgList,
				3000
		);

		log.info("批量发送{}条消息成功: {}", messages.size(), sendResult.getSendStatus());
	}
}