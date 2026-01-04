package top.tangtian.rocketmqnativedemo;

/**
 * @author tangtian
 * @date 2026-01-03 10:28
 */

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 演示：两个不同的消费者组，独立消费同一个 Topic
 */
public class MultiConsumerGroupDemo {

	public static void main(String[] args) throws Exception {
		String namesrvAddr = args[0];
		// ========================================
		// 消费者组1：订单处理服务
		// ========================================
		DefaultMQPushConsumer consumer1 = new DefaultMQPushConsumer("order_process_group");
		consumer1.setNamesrvAddr(namesrvAddr);

		// 订阅 order_topic
		consumer1.subscribe("order_topic", "*");

		consumer1.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				System.out.println("[订单处理服务] 收到消息: " + new String(msg.getBody()));
				System.out.println("  消费者组: order_process_group");
				// 执行订单处理逻辑...
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		consumer1.start();
		System.out.println("消费者组1启动: order_process_group");

		// ========================================
		// 消费者组2：数据统计服务
		// ========================================
		DefaultMQPushConsumer consumer2 = new DefaultMQPushConsumer("data_statistics_group");
		consumer2.setNamesrvAddr(namesrvAddr);

		// 同样订阅 order_topic（独立消费，互不影响）
		consumer2.subscribe("order_topic", "*");

		consumer2.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				System.out.println("[数据统计服务] 收到消息: " + new String(msg.getBody()));
				System.out.println("  消费者组: data_statistics_group");
				// 执行数据统计逻辑...
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		consumer2.start();
		System.out.println("消费者组2启动: data_statistics_group");

		/*
		 * 关键理解：
		 *
		 * 当生产者发送一条消息到 order_topic 时：
		 *
		 * 1. order_process_group 会收到这条消息（用于处理订单）
		 * 2. data_statistics_group 也会收到这条消息（用于统计数据）
		 *
		 * 两个消费者组互不影响，各自独立消费！
		 *
		 * 这就是消费者组的意义：
		 * - 同一个消息可以被多个业务系统独立消费
		 * - 每个业务系统使用独立的消费者组
		 * - 实现了消息的"发布-订阅"模式
		 */

		Thread.sleep(Long.MAX_VALUE);
	}
}