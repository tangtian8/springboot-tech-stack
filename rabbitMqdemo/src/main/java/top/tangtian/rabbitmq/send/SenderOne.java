package top.tangtian.rabbitmq.send;

/**
 * @author tangtian
 * @date 2025-11-03 19:39
 */

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.tangtian.rabbitmq.config.RabbitMQConfig;


@Component
public class SenderOne implements CommandLineRunner {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	// Routing (路由模式 - Direct)
	public void sendOneMessage(String message){
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,RabbitMQConfig.ROUTING_VISIT, message);
	}

	// Publish/Subscribe (发布订阅模式 - Fanout)
	public void sendPublishMessage(String message){
		message = "发布订阅" + message;
		rabbitTemplate.convertAndSend(RabbitMQConfig.Publish,"", message);
	}

	@Override
	public void run(String... args) throws Exception {
		sendPublishMessage("起飞！");
	}
	//
}
