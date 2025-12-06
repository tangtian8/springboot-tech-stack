package top.tangtian.rabbitmq.receive;

/**
 * @author tangtian
 * @date 2025-11-04 09:33
 */

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static top.tangtian.rabbitmq.config.RabbitMQConfig.*;

@Component
public class Receiver {

	//最基础的模式:一个生产者 → 一个队列 → 一个消费者
	@RabbitListener(queues = QUEUE_VISIT)
	public void receive(String message){
		System.out.println("QUEUE_VISIT收到: " + message);
	}


	//Work Queue (工作队列模式)
	//   一个生产者 → 一个队列 → 多个消费者竞争消费
	@RabbitListener(queues = QUEUE_DIAGNOSIS, concurrency = "5")
	public void receiveVisit(String message) {
		System.out.println(Thread.currentThread().getName() + " 处理消息: " + message);
		// 模拟耗时操作
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	@RabbitListener(queues = QUEUE_OUT_ORDER)
	public void receiveoutorder(String message){
		System.out.println("receiveOUTORDER收到: " + message);
	}

	@RabbitListener(queues = QUEUE_IN_ORDER)
	public void receiveinorder(String message){
		System.out.println("receiveINORDER收到: " + message);
	}


}
